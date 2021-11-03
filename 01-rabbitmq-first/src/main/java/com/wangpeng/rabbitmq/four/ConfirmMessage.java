package com.wangpeng.rabbitmq.four;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.wangpeng.rabbitmq.utils.RabbitMqUtils;

import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class ConfirmMessage {

    public static void main(String[] args) throws Exception{
        // publishMessageIndividually();
        // publishMessageBatch();
        publishMessageAsync();
    }

    public static void publishMessageIndividually() throws Exception{
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, false, false, false, null);
        // 信道开启发布确认
        channel.confirmSelect();
        long begin = System.currentTimeMillis();
        for(int i = 0; i < 1000; i++) {
            String message = String.valueOf(i);
            // 发布消息
            channel.basicPublish("", queueName, null, message.getBytes());
            // 等待确认（同步方式）
            boolean flag = channel.waitForConfirms();
            //if(flag) System.out.println("消息发送成功");
        }
        long end = System.currentTimeMillis();

        System.out.println("发布1000个单独确认消息，耗时" + (end - begin) + "毫秒");
    }

    public static void publishMessageBatch() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, false, false, false, null);
        // 信道开启发布确认
        channel.confirmSelect();
        long begin = System.currentTimeMillis();
        for(int i = 0; i < 1000; i++) {
            String message = String.valueOf(i);
            // 发布消息
            channel.basicPublish("", queueName, null, message.getBytes());
            // 每100个消息确认一次
            if((i + 1) % 100 == 0) {
                channel.waitForConfirms();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("发布1000个批量确认消息，耗时" + (end - begin) + "毫秒");
    }

    public static void publishMessageAsync() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, false, false, false, null);
        // 开启发布确认
        channel.confirmSelect();
        /**
         * 建一个线程安全有序的哈希表
         * 1. 将序号和消息进行关联
         * 2. 轻松批量删除条目，只要给到序列号
         * 3. 支持并发访问
         */
        ConcurrentSkipListMap<Long, String> outstandingConfirms
                = new ConcurrentSkipListMap<>();

        /**
         * 确认收到消息的回调
         * 1. 消息序列号
         * 2. true可以确认小于等于当前序列号的消息
         *      false确认当前序列号消息
         */
        ConfirmCallback ackCallback = (deliveryTag, multiple) -> {
            if(multiple) {
                // 返回的是小于等于当前序列号的未确认消息，是一个map
                ConcurrentNavigableMap<Long, String> confirmed
                        = outstandingConfirms.headMap(deliveryTag, true);
                confirmed.clear();
            } else {
                outstandingConfirms.remove(deliveryTag);
            }
        };
        /**
         * 未被确认的消息
         */
        ConfirmCallback nackCallback = (deliveryTag, multiple) -> {
            String message = outstandingConfirms.get(deliveryTag);
            System.out.println("发布的消息" + message + "未被确认，序列号" + deliveryTag);
        };
        /**
         * 添加一个异步确认监听器
         */
        channel.addConfirmListener(ackCallback, nackCallback);
        long begin = System.currentTimeMillis();
        for(int i = 0; i < 1000; i++) {
            String message = String.valueOf(i);
            /**
             * channel.getNextPublishSeqNo 可以获取下一个消息的序列号
             * 通过序列号与消息提进行一个关联
             * 全部都是未确认的消息提
             */
            outstandingConfirms.put(channel.getNextPublishSeqNo(), message);
            channel.basicPublish("", queueName, null, message.getBytes());
        }
        long end = System.currentTimeMillis();
        System.out.println("发布1000个异步确认消息，耗时" + (end - begin) + "毫秒");
    }

}
