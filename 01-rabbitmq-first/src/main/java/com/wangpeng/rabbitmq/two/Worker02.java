package com.wangpeng.rabbitmq.two;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.wangpeng.rabbitmq.utils.RabbitMqUtils;

public class Worker02 {
    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        // 成功回调
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            String messageStr = new String(message.getBody());
            System.out.println("接收到的消息：" + messageStr);
        };
        // 取消回调
        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println(consumerTag + "消费者取消消费");
        };
        System.out.println("C2消费者等待消费");
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
