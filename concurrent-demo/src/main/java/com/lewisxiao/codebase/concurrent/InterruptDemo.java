package com.lewisxiao.codebase.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * 中断线程
 *
 * @author xiaoweiqian
 * @date 2022/9/29 17:06
 */
public class InterruptDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("子线程被中断了");
                    boolean flag = Thread.interrupted();
                    System.out.println("清除中断标志：" + flag);
                } else {
                    System.out.println("执行中");
                }
            }
        });
        thread.start();
        TimeUnit.SECONDS.sleep(1);
        thread.interrupt();
        TimeUnit.SECONDS.sleep(1);
    }
}
