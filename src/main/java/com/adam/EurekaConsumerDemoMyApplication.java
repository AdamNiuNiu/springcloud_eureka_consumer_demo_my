package com.adam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@EnableHystrixDashboard
@EnableCircuitBreaker
@EnableFeignClients
@SpringBootApplication
public class EurekaConsumerDemoMyApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaConsumerDemoMyApplication.class, args);
    }

    @Bean
    @LoadBalanced //客户端添加支持负载均衡注解,使用负载均衡策略轮训到调用服务接口
    public RestTemplate restTemplate() {
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }

}
