package com.adam.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 向RabbitMQ声明两个队列：添加选课、完成选课，交换机使用路由模式
 */
@Configuration
public class RabbitMQConfig {


    //添加选课任务交换机
    public static final String EX_LEARNING_ADDCHOOSECOURSE = "ex_learning_addchoosecourse";

    //添加选课消息队列
    public static final String XC_LEARNING_ADDCHOOSECOURSE = "xc_learning_addchoosecourse";

    //完成添加选课消息队列
    public static final String XC_LEARNING_FINISHADDCHOOSECOURSE = "xc_learning_finishaddchoosecourse";

    //添加选课路由key
    public static final String XC_LEARNING_ADDCHOOSECOURSE_KEY = "addchoosecourse";

    //完成添加选课路由key
    public static final String XC_LEARNING_FINISHADDCHOOSECOURSE_KEY = "finishaddchoosecourse";

    /**
     * 交换机配置
     *
     * @return
     */
    @Bean(EX_LEARNING_ADDCHOOSECOURSE)
    public Exchange EX_DECLARE() {
        return ExchangeBuilder.directExchange(EX_LEARNING_ADDCHOOSECOURSE).durable(true).build();
    }

    //声明完成添加选课消息队列
    @Bean(XC_LEARNING_FINISHADDCHOOSECOURSE)
    public Queue QUEUE_XC_LEARNING_FINISHADDCHOOSECOURSE() {
        Queue queue = new Queue(XC_LEARNING_FINISHADDCHOOSECOURSE);
        return queue;
    }

    //声明添加选课消息队列
    @Bean(XC_LEARNING_ADDCHOOSECOURSE)
    public Queue QUEUE_XC_LEARNING_ADDCHOOSECOURSE() {
        Queue queue = new Queue(XC_LEARNING_ADDCHOOSECOURSE);
        return queue;
    }

    /**
     * 绑定队列到交换机
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding BINDING_QUEUE_FINISHADDCHOOSECOURSE(
            @Qualifier(XC_LEARNING_FINISHADDCHOOSECOURSE) Queue queue,
            @Qualifier(EX_LEARNING_ADDCHOOSECOURSE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange)
                .with(XC_LEARNING_FINISHADDCHOOSECOURSE_KEY).noargs();
    }

    @Bean public Binding BINDING_QUEUE_ADDCHOOSECOURSE(
            @Qualifier(XC_LEARNING_ADDCHOOSECOURSE) Queue queue,
            @Qualifier(EX_LEARNING_ADDCHOOSECOURSE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange)
                .with(XC_LEARNING_ADDCHOOSECOURSE_KEY).noargs();
    }
}
