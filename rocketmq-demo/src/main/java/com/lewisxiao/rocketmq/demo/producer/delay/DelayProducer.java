package com.lewisxiao.rocketmq.demo.producer.delay;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;

/**
 * 延迟消息
 *
 * @author xiaoweiqian
 * @date 2022/9/19 10:58
 */
public class DelayProducer {
    public static void send() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("delay_sms_group");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        int totalMessageToSend = 100;
        for (int i = 0; i < 100; i++) {
            Message message = new Message("delaySmsTopic", ("Hello delay message " + i).getBytes(StandardCharsets.UTF_8));
            message.setDelayTimeLevel(3);
            producer.send(message);
        }
        producer.shutdown();
    }
}
