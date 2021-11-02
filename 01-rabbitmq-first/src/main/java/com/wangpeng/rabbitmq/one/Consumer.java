package com.wangpeng.rabbitmq.one;

import com.rabbitmq.client.*;

public class Consumer {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.199.146");
        factory.setUsername("admin");
        factory.setPassword("123");
        // 创建连接
        Connection connection = factory.newConnection();
        // 创建信道
        Channel channel = connection.createChannel();
        System.out.println("等待接收消息...");
        // 被交付的回调
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message =  new String(delivery.getBody());
            System.out.println(message);
        };
        // 取消的回调
        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println("消息消费被中断");
        };
        /**
         * 消费者消费消息
         * 1. 消费的队列名
         * 2. 消费成功后是否要自动应答，true自动应答，false手动应答
         * 3. 消费者成功回调
         * 4. 取消消费回调
         */
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
