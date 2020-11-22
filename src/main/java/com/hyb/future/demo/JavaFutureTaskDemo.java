package com.hyb.future.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @program: future
 * @description:
 * @author: yubin.huang
 * @create: 2020-11-22 11:34
 **/
public class JavaFutureTaskDemo {
    private static final Logger log = LoggerFactory.getLogger(JavaFutureDemo.class);

    public final static int SLEEP_GAP = 500;

    public static String getCurrentThreadName() {
        return Thread.currentThread().getName();
    }

    static class BoilWaterCallable implements Callable<Boolean> {

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

    static class CleanCallable implements Callable<Boolean> {

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

    public static void drinkTea(Boolean boilWaterOk, Boolean cleanOk) {
        if (boilWaterOk && cleanOk) {
            log.info("泡茶喝");
        } else if (!boilWaterOk) {
            log.error("烧水失败，没有茶喝");
        } else if (!cleanOk) {
            log.error("杯子洗不了，没有茶喝");
        }
    }

    public static void main(String[] args) {
        Callable<Boolean> boilWaterCallable = new BoilWaterCallable();
        FutureTask<Boolean> boilWaterTask = new FutureTask<Boolean>(boilWaterCallable);
        Thread thread1 = new Thread(boilWaterTask, "烧水线程");

        Callable<Boolean> cleanCallable = new CleanCallable();
        FutureTask<Boolean> cleanTask = new FutureTask<Boolean>(cleanCallable);
        Thread thread2 = new Thread(cleanTask, "清洗线程");

        thread1.start();
        thread2.start();
        Thread.currentThread().setName("主线程");

        try {
            boolean boilWaterOk = boilWaterTask.get();
            boolean cleanOk = cleanTask.get();
            drinkTea(boilWaterOk, cleanOk);
        } catch (InterruptedException ie) {
            log.error(getCurrentThreadName() + "运行失败");
        } catch (ExecutionException ee) {
            ee.printStackTrace();
        }
        log.info(getCurrentThreadName() + "运行结束");
    }
}
