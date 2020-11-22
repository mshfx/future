package com.hyb.future.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: future
 * @description:
 * @author: yubin.huang
 * @create: 2020-11-22 11:34
 **/
public class JavaFutureDemo {
    private static final Logger log = LoggerFactory.getLogger(JavaFutureDemo.class);

    public final static int SLEEP_GAP = 500;

    public static String getCurrentThreadName() {
        return Thread.currentThread().getName();
    }

    static class BoilWaterThread extends Thread {
        public BoilWaterThread() {
            super("烧水线程");
        }

        @Override
        public void run() {
            try {
                log.info("洗好水壶");
                log.info("灌上凉水");
                log.info("放在火上");
                Thread.sleep(SLEEP_GAP);
                log.info("水开了");
            } catch (InterruptedException ie) {
                log.error("执行失败");
            }
            log.info("运行结束");
        }
    }

    static class CleanThread extends Thread {
        public CleanThread() {
            super("清洗线程");
        }

        @Override
        public void run() {
            try {
                log.info("洗茶壶");
                log.info("洗茶杯");
                log.info("拿茶叶");
                Thread.sleep(SLEEP_GAP);
                log.info("洗完了");
            } catch (InterruptedException ie) {
                log.error("执行失败");
            }
            log.info("运行结束");
        }
    }

    public static void main(String[] args) {
        Thread thread1 = new BoilWaterThread();
        Thread thread2 = new CleanThread();
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
            Thread.currentThread().setName("主线程");
            log.info("泡茶喝");
        } catch (InterruptedException ie) {
            log.error( getCurrentThreadName()+ "执行失败");
        }
        log.info("运行结束");
    }
}
