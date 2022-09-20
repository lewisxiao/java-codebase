package com.lewisxiao.rxjava2.demo;

import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RxJava2Demo {
    public static void main(String[] args) throws InterruptedException {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                System.out.println("emitter");
                emitter.onNext("11111");
                emitter.onNext("22222");
            }
        }).subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("onSubscribe");
            }

            @Override
            public void onNext(Object o) {
                System.out.println(o);
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e.getCause());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });

        System.out.println("released");
    }
}
