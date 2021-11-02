package com.wangpeng.rabbitmq.three;

import com.rabbitmq.client.Channel;
import com.wangpeng.rabbitmq.utils.RabbitMqUtils;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Producer01 {
    private static final String QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception{
        //创建channel
        Channel channel = RabbitMqUtils.getChannel();
        //声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入信息");
        while(scanner.hasNext()) {
            String str = scanner.next();
            channel.basicPublish("", QUEUE_NAME, null, str.getBytes(StandardCharsets.UTF_8));
            System.out.println("生产者发布消息：" + str);
        }
    }

}
