package com.wangpeng.rabbitmq.one;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        // 创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 远程服务器ip
        factory.setHost("192.168.199.146");
        // rabbitmq的用户名
        factory.setUsername("admin");
        // rabbitmq的密码
        factory.setPassword("123");
        // 新建rabbitmq的连接
        Connection connection = factory.newConnection();
        // 创建信道
        Channel channel = connection.createChannel();
        /**
         * 队列声明
         * 1. 队列名称
         * 2. durable:消息是否持久化，默认消息存储在内存中
         * 3. exclusive:排他，true为单个消费者，false是多个消费者
         * 4. autoDelete:是否自动删除，最后一个消费者断开连接后，队列是否自动删除。
         * 5. 其他参数
         */
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String message = "hello rabbitmq";
        /**
         * 发送一个消息
         * 1. 发送到哪个交换机，我们暂时不使用交换机
         * 2. 路由的key是什么，填队列名称
         * 3. 其他的参数信息
         * 4. 发送消息的消息体（注意要getbyte）
         */
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println("消息发送完毕");
    }
}
