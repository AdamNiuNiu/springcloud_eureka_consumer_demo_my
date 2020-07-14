package com.adam.demo.exception;


/**
 * @author Administrator
 * @version 1.0
 * @create 2018-09-14 17:31
 **/
public class ExceptionCast {

    public static void cast(LearningCode resultCode){
        throw new CustomException(resultCode);
    }
}
