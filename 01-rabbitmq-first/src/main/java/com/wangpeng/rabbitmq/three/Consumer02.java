package com.wangpeng.rabbitmq.three;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.wangpeng.rabbitmq.utils.RabbitMqUtils;

import java.util.concurrent.TimeUnit;

public class Consumer02 {

    private static final String QUEUE_NAME = "ack_queue";
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        System.out.println("Consumer02 消息处理时间较长");
        // 被交付回调
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            String str = new String(message.getBody());
            // 睡眠1秒钟，模拟消息处理时间为1秒
            try {
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 控制台输出一下
            System.out.println("消息成功处理：" + str);
            // 应答消息
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
        };
        // 消费消息（采用手动应答方式）
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, (consumerTag) -> {
            System.out.println("消息取消");
        });
    }
}
