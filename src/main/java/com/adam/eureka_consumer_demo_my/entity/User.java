package com.adam.eureka_consumer_demo_my.entity;


import lombok.Data;

@Data
public class User implements java.io.Serializable{

    private Long id;

    private String username;

    private String password;

    private Integer age;
}
