package com.lewisxiao.codebase.rocketmq.producer.order;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.List;

/**
 * 顺序消息
 * 顺序消息是一种对消息发送和消费顺序有严格要求的消息
 *
 * 对于一个指定的Topic，消息严格按照先进先出（FIFO）的原则进行消息发布和消费，即先发布的消息先消费，后发布的消息后消费。在 Apache RocketMQ 中支持分区顺序消息
 *
 * @author xiaoweiqian
 * @date 2022/9/16 18:47
 */
public class OrderProducer {
    public static void send() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("order_sms_group");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        String[] tags = new String[] { "TagA", "TagB", "TagC", "TagD", "TagE" };
        for (int i = 0; i < 100; i++) {
            int orderId = i % 10;
            Message message = new Message("orderSmsTopic", tags[i % tags.length], "KEY" + i,
                    ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult sendResult = producer.send(message, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    Integer id = (Integer) arg;
                    int index = id % mqs.size();
                    return mqs.get(index);
                }
            }, orderId);
            System.out.printf("%s%n", sendResult);
        }
        producer.shutdown();
    }
}
