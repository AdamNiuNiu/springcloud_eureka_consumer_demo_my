package com.adam.eureka_consumer_demo_my.service;

import com.adam.eureka_consumer_demo_my.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//value对应服务提供方的服务名，也就是yaml配置文件中spring.application.name对应的value值
@FeignClient(value = "eureka-provider-demo-my",fallback = UserServiceImplHystrixCallBack.class)
public interface UserService {

    //RequestMapping 注解中的value值对用url映射，method对应请求方法，根据官方文档提示这里不能用GetMapping注解
    @RequestMapping(value = "/provider/getUserInfo/{id}",method = RequestMethod.POST)
    //注解 PathVariable 里面需要填充变量的名字，不然也是运行不成功的
    //如果入参是一个对象的话，那么这个方法在 feign 里面默认为 POST 方法，就算你写成 GET 方式也无济于事。
    public User getUserInfo(@PathVariable("id") String id);
}
