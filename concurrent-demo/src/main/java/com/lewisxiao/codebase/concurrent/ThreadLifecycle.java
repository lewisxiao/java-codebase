package com.lewisxiao.codebase.concurrent;

public class ThreadLifecycle {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("内部：" + Thread.currentThread().getState());
            }
        });

        thread.start();
        thread.join(3000);
        System.out.println(thread.getState());
    }
}
