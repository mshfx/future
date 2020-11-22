package com.hyb.future.demo;

import com.google.common.util.concurrent.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: future
 * @description:
 * @author: yubin.huang
 * @create: 2020-11-22 11:48
 **/
public class GuavaFutureDemo {
    private static final Logger log = LoggerFactory.getLogger(GuavaFutureDemo.class);

    public static final int SLEEP_GAP = 500;

    public static String getCurrentThreadName() {
        return Thread.currentThread().getName();
    }

    static class BoilWaterJob implements Callable<Boolean> {

        public Boolean call() throws Exception {
            try {
                log.info("洗好水壶");
                log.info("灌上凉水");
                log.info("放在火上");
                Thread.sleep(SLEEP_GAP);
                log.info("水开了");
            } catch (InterruptedException ie) {
                log.error("执行失败");
                return false;
            }
            log.info("运行结束");
            return true;
        }
    }

    static class CleanJob implements Callable<Boolean> {

        public Boolean call() throws Exception {
            try {
                log.info("洗茶壶");
                log.info("洗茶杯");
                log.info("拿茶叶");
                Thread.sleep(SLEEP_GAP);
                log.info("洗完了");
            } catch (InterruptedException ie) {
                log.error("执行失败");
                return false;
            }
            log.info("运行结束");
            return true;
        }
    }

    // 泡茶喝主线程类
    static class MainJob implements Runnable {
        boolean boilWaterOk = false;
        boolean cleanOk = false;
        int gap = SLEEP_GAP / 10;

        public void run() {
            while (true) {
                try {
                    Thread.sleep(gap);
                    log.info("读书中");
                }catch (InterruptedException ie) {
                    log.error(getCurrentThreadName() + "执行失败");
                }
                if (boilWaterOk && cleanOk) {
                    drinkTea(boilWaterOk, cleanOk);
                }
            }
        }

        public void drinkTea(Boolean boilWaterOk, Boolean cleanOk) {
            if (boilWaterOk && cleanOk) {
                log.info("泡茶喝");
                this.boilWaterOk = false;
                this.gap = SLEEP_GAP * 100;
            } else if (!boilWaterOk) {
                log.error("烧水失败，没有茶喝");
            } else if (!cleanOk) {
                log.error("杯子洗不了，没有茶喝");
            }
        }
    }

    public static void main(String[] args) {
        // 泡茶主线程
        final MainJob mainJob = new MainJob();
        Thread mainThread = new Thread(mainJob);
        mainThread.setName("主线程");
        mainThread.start();
        // 烧水
        Callable<Boolean> boilWaterJob = new BoilWaterJob();
        // 清洗
        Callable<Boolean> cleanJob = new CleanJob();

        // 线程池
        ExecutorService jPool = Executors.newFixedThreadPool(10);
        // 构建Guava线程池
        ListeningExecutorService gPool = MoreExecutors.listeningDecorator(jPool);

        // 提交烧水线程实例，Guava线程池获取异步任务
        ListenableFuture<Boolean> boilWaterTask = gPool.submit(boilWaterJob);
        // 绑定异步回调，执行完成，设置喝水线程的boilWaterOk设置为true
        Futures.addCallback(boilWaterTask, new FutureCallback<Boolean>() {
            public void onSuccess(@Nullable Boolean r) {
                if (r) {
                    mainJob.boilWaterOk = true;
                }
            }

            public void onFailure(Throwable throwable) {
                log.error("执行失败");
            }
        }, jPool);

        // 提交清洗线程实例，Guava线程池获取异步任务
        ListenableFuture<Boolean> cleanTask = gPool.submit(cleanJob);
        // 绑定异步回调，执行完成，设置喝水线程的cleanOk设置为true
        Futures.addCallback(cleanTask, new FutureCallback<Boolean>() {
            public void onSuccess(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    mainJob.cleanOk = true;
                }
            }

            public void onFailure(Throwable throwable) {
                log.error("执行失败");
            }
        }, jPool);
    }
}
