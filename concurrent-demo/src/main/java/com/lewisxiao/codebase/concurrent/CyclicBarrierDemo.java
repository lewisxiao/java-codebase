package com.lewisxiao.codebase.concurrent;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class CyclicBarrierDemo {
    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(5, () -> {
            System.out.println("All finished, go!!!");
        });

        for (int i = 0; i < 5; i++) {
            int index = i;
            new Thread(() -> {
                try {
                    System.out.println("sub " + index + " start...");
                    TimeUnit.SECONDS.sleep(2);

                    // 等待所有线程到达
                    barrier.await();
                    System.out.println("sub " + index + " finished");
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

        // 跟 CountDownLatch 不同，主线程不会阻塞
        System.out.println("main finished");
    }
}
