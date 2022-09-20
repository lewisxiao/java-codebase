package com.lewisxiao.codebase.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CountDownLaunchDemo {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        new Thread(() -> {
            try {
                System.out.println("begin to do something...");
                TimeUnit.SECONDS.sleep(3);
                System.out.println("finished");

                // 线程已经执行完，计数器减1
                countDownLatch.countDown();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        // 等待上面的线程执行完
        countDownLatch.await();
        System.out.println("main thread finished");
    }
}
