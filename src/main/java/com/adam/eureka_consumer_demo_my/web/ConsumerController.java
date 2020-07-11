package com.adam.eureka_consumer_demo_my.web;

import com.adam.eureka_consumer_demo_my.entity.User;
import com.adam.eureka_consumer_demo_my.service.UserService;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RequestMapping("/consumer")
@RestController
public class ConsumerController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserService userService;

    public static final String SERVICEID = "eureka-provider-demo-my";


    @HystrixCommand(fallbackMethod = "getUserInfofallback")
    @RequestMapping(value = "/getUserInfoByRestTemplate",method = RequestMethod.POST)
    public String getUserInfo() {

        String result = restTemplate.postForObject("http://" + SERVICEID + "/provider/getUserInfo/444", null, String.class);
        System.out.println("==========调用结果为："+result.toString()+"==========");
        return result;
    }

    public String getUserInfofallback() {
        return "getUserInfoByRestTemplate方法熔断:触发降级方法 ";
    }


    @RequestMapping(value = "/getUserInfoByFeign",method = RequestMethod.POST)
    public String getUserInfoByFeign(String id) {

        User user = userService.getUserInfo(id);
        System.out.println("==========调用结果为："+user.toString()+"==========");
        return "调用user信息成功！";
    }

}
