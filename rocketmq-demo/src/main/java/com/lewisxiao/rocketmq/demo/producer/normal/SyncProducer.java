package com.lewisxiao.rocketmq.demo.producer.normal;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.nio.charset.StandardCharsets;

/**
 * 普通消息 —— 发送同步消息
 * 同步发送是最常用的方式，是指消息发送方发出一条消息后，会在收到服务端同步响应之后才发下一条消息的通讯方式，可靠的同步传输被广泛应用于各种场景，如重要的通知消息、短消息通知等
 *
 * @author xiaoweiqian
 * @date 2022/9/16 17:47
 */
public class SyncProducer {
    public static void main(String[] args) throws Exception {
        send();
    }

    public static void send() throws Exception {
        // 初始化一个producer并设置Producer group name
        DefaultMQProducer producer = new DefaultMQProducer("sync_sms_group");

        // 设置NameServer地址
        producer.setNamesrvAddr("localhost:9876");
        producer.setSendMsgTimeout(30 * 60 * 1000);

        // 启动producer
        producer.start();

        try {
            for (int i = 0; i < 100; i++) {
                Message message = new Message("syncSmsTopic", "smsTagA", ("你好 RocketMQ" + i).getBytes(StandardCharsets.UTF_8));
                message.putUserProperty("subTag1", "subTag1");
                SendResult sendResult = producer.send(message);
                System.out.printf("%s%n", sendResult);
            }
        } finally {

            // 使用完毕后，关闭producer
            producer.shutdown();
        }
    }
}

