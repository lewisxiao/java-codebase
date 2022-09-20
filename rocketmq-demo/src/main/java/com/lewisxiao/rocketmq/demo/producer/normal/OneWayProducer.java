package com.lewisxiao.rocketmq.demo.producer.normal;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;

/**
 * 普通消息 —— 单向发送消息
 * 发送方只负责发送消息，不等待服务端返回响应且没有回调函数触发，即只发送请求不等待应答。
 * 此方式发送消息的过程耗时非常短，一般在微秒级别。适用于某些耗时非常短，但对可靠性要求并不高的场景，例如日志收集。
 *
 * @author xiaoweiqian
 * @date 2022/9/16 18:32
 */
public class OneWayProducer {
    public static void send() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("one_way_sms_group");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        for (int i = 0; i < 100; i++) {
            Message message = new Message("oneWaySmsTopic", "smsTagA", ("Hello RocketMQ" + i).getBytes(StandardCharsets.UTF_8));
            producer.sendOneway(message);
            System.out.println("SEND_SUCCESS " + i);
        }

        // 一旦producer不再使用，关闭producer
        producer.shutdown();
    }
}
