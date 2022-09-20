package com.lewisxiao.codebase.rocketmq.consumer;

import org.apache.rocketmq.client.consumer.*;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Pull消费 —— 客户端需要主动到服务端取数据，优点是客户端可以依据自己的消费能力进行消费，但拉取的频率也需要用户自己控制，
 * 拉取频繁容易造成服务端和客户端的压力，拉取间隔长又容易造成消费不及时。
 *
 * @author xiaoweiqian
 * @date 2022/9/19 11:41
 */
public class PullConsumer {
    public static volatile boolean running = true;

    public static void main(String[] args) throws Exception {
        consume();
    }

    /**
     * 比较原始的的 Pull Consumer，不提供相关的订阅方法，需要客户端自己主动进行拉取、并自己更新位点，处理起来很麻烦。
     * 一般不再使用该方法，而是使用 Lite Pull Consumer，在 4.9.4 版本中已经明确表示 “已废弃”。
     */
    public static void consume() throws Exception {
        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer();
        consumer.setNamesrvAddr("localhost:9876");
        consumer.start();

        try {
            MessageQueue mq = new MessageQueue();
            mq.setQueueId(0);
            mq.setTopic("TopicTest");
            mq.setBrokerName("jinrongtong-MacBook-Pro.local");
            long offset = 26;
            PullResult pullResult = consumer.pull(mq, "*", offset, 32);
            if (pullResult.getPullStatus().equals(PullStatus.FOUND)) {
                System.out.printf("%s%n", pullResult.getMsgFoundList());
                consumer.updateConsumeOffset(mq, pullResult.getNextBeginOffset());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.shutdown();
        }
    }

    /**
     * Lite Pull Consumer是RocketMQ 4.6.0推出的Pull Consumer，相比于原始的Pull Consumer更加简单易用，它提供了Subscribe和Assign两种模式
     */
    public static void liteConsumerInSubscribeMode() throws Exception {
        DefaultLitePullConsumer litePullConsumer = new DefaultLitePullConsumer("lite_pull_consumer_group");
        litePullConsumer.subscribe("syncSmsTopic", "*");
        litePullConsumer.setPullBatchSize(20);
        litePullConsumer.start();

        try {
            while (running) {
                List<MessageExt> messageExts = litePullConsumer.poll();
                System.out.printf("%s%n", messageExts);
            }
        } finally {
            litePullConsumer.shutdown();
        }
    }

    public static void liteConsumerInAssignMode() throws Exception {
        DefaultLitePullConsumer litePullConsumer = new DefaultLitePullConsumer("please_rename_unique_group_name");

        // 关闭 AutoCommit 后，需要手动设置提交位点
        litePullConsumer.setAutoCommit(false);
        litePullConsumer.start();

        // Assign模式下没有自动的负载均衡机制，需要用户自行指定需要拉取的队列
        Collection<MessageQueue> mqSet = litePullConsumer.fetchMessageQueues("TopicTest");
        List<MessageQueue> list = new ArrayList<>(mqSet);
        List<MessageQueue> assignList = new ArrayList<>();
        for (int i = 0; i < list.size() / 2; i++) {
            assignList.add(list.get(i));
        }
        litePullConsumer.assign(assignList);

        // 设置拉取数据的起始位点
        litePullConsumer.seek(assignList.get(0), 10);
        try {
            while (running) {
                List<MessageExt> messageExts = litePullConsumer.poll();
                System.out.printf("%s %n", messageExts);

                // 拉取到消息后，手动提交位点
                litePullConsumer.commitSync();
            }
        } finally {
            litePullConsumer.shutdown();
        }
    }
}
