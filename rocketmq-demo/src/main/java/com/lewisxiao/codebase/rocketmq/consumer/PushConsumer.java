package com.lewisxiao.codebase.rocketmq.consumer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Push消费 —— 服务端主动推送消息给客户端，优点是及时性较好，但如果客户端没有做好流控，一旦服务端推送大量消息到客户端时，就会导致客户端消息堆积甚至崩溃。
 *
 *
 * @author xiaoweiqian
 * @date 2022/9/19 11:41
 */
public class PushConsumer {
    public static void main(String[] args) throws Exception {
//        consumeConcurrently();
        consumeOrderly();
    }

    /**
     * 并发消费
     */
    public static void consumeConcurrently() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("push_sms_group");
        consumer.setNamesrvAddr("localhost:9876");

        // 订阅一个或多个 topic, 并指定 tag 过滤条件, * 表示接收所有消息, 在多个Tag之间用两个竖线（||）分隔
        //
        // 注意：
        // 如果同一个消费者多次订阅某个Topic下的Tag，以最后一次订阅为准。
        //
        // 如下错误代码中，Consumer只能订阅到TagFilterTest下TagB的消息，而不能订阅TagA的消息。
        // consumer.subscribe("TagFilterTest", "TagA");
        // consumer.subscribe("TagFilterTest", "TagB");
        consumer.subscribe("syncSmsTopic", "*");

        consumer.setMessageModel(MessageModel.CLUSTERING);

        // 注册回调接口来处理从Broker中收到的消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), new String(msgs.get(0).getBody(), StandardCharsets.UTF_8));
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });
        consumer.start();
        System.out.printf("Concurrently Consumer Started.%n");
    }

    /**
     * 顺序消费
     */
    public static void consumeOrderly() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("push_sms_group");
        consumer.setNamesrvAddr("localhost:9876");

        // 订阅一个或多个 topic, 并指定 tag 过滤条件, * 表示接收所有消息
        consumer.subscribe("syncSmsTopic", "*");

        consumer.setMessageModel(MessageModel.CLUSTERING);

        // 注册回调接口来处理从Broker中收到的消息
        consumer.registerMessageListener(new MessageListenerOrderly() {
            final AtomicLong consumeTimes = new AtomicLong(0);
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
                this.consumeTimes.incrementAndGet();
                if ((this.consumeTimes.get() % 2) == 0) {
                    return ConsumeOrderlyStatus.SUCCESS;
                } else if ((this.consumeTimes.get() % 5) == 0) {
                    context.setSuspendCurrentQueueTimeMillis(3000);
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });
        consumer.start();
        System.out.printf("Orderly Consumer Started.%n");
    }
}
