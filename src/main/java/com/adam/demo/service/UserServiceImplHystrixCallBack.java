package com.adam.demo.service;

import com.adam.demo.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserServiceImplHystrixCallBack implements UserService{

    /**
     * getUserInfo降级方法
     * @param id
     * @return
     */
    @Override
    public User getUserInfo(String id) {
        User user = new User();
        user.setUsername("getUserInfo方法熔断:触发降级方法");
        return user;
    }
}
