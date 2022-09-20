package com.lewisxiao.rocketmq.demo.producer.batch;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量发送消息
 * 在对吞吐率有一定要求的情况下，Apache RocketMQ可以将一些消息聚成一批以后进行发送，可以增加吞吐率，并减少API和网络调用次数。
 *
 * 注意：
 *  1. 批量消息的大小不能超过 1MiB（否则需要自行分割）
 *  2. 同一批 batch 中 topic 必须相同
 *
 * @author xiaoweiqian
 * @date 2022/9/19 11:08
 */
public class BatchProducer {
    public static void send() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("batch_sms_group");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        String topic = "batchSmsTopic";
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(topic, "batchSmsTag", "OrderID001", "Hello world 0".getBytes()));
        messages.add(new Message(topic, "batchSmsTag", "OrderID002", "Hello world 1".getBytes()));
        messages.add(new Message(topic, "batchSmsTag", "OrderID003", "Hello world 2".getBytes()));
        producer.send(messages);
        producer.shutdown();
    }
}
