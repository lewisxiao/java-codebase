package com.lewisxiao.rocketmq.demo.producer.normal;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;

/**
 * 普通消息 —— 发送异步消息
 * 异步发送是指发送方发出一条消息后，不等服务端返回响应，接着发送下一条消息的通讯方式。
 * 异步发送一般用于链路耗时较长，对响应时间较为敏感的业务场景。
 *
 * @author xiaoweiqian
 * @date 2022/9/16 18:25
 */
public class AsyncProducer {
    public static void send() throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("async_sms_group");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();
        producer.setRetryTimesWhenSendAsyncFailed(0);

        for (int i = 0; i < 100; i++) {
            final int index = i;
            Message message = new Message("asyncSmsTopic", "smsTagA", ("Hello RocketMQ" + i).getBytes(StandardCharsets.UTF_8));
            producer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.printf("%-10d OK %s %n", index, sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable e) {
                    System.out.printf("%-10d Exception %s %n", index, e);
                    e.printStackTrace();
                }
            });
        }

        // 异步发送，直接关闭会导致未发送出去的消息报错
//        producer.shutdown();
    }
}
