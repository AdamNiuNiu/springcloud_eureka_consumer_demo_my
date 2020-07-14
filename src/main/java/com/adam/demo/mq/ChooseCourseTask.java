package com.adam.demo.mq;

import com.adam.demo.config.RabbitMQConfig;
import com.adam.demo.entity.XcTask;
import com.adam.demo.modle.ResponseResult;
import com.adam.demo.service.LearningService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class ChooseCourseTask {

    @Autowired
    private LearningService learningService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_ADDCHOOSECOURSE)
    public void receiveChoosecourseTask(XcTask xcTask) {

        log.info("receive choose course task,taskId:{}", xcTask.getId());
        //接收到的消息id
        String id = xcTask.getId();
        try {
            //取出消息的内容
            String requestBody = xcTask.getRequestBody();
            Map map = JSON.parseObject(requestBody, Map.class);
            String userId = (String) map.get("userId");
            String courseId = (String) map.get("courseId");
            String valid = (String) map.get("valid");
            Date startTime = null;
            Date endTime = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY‐MM‐dd HH:mm:ss");
            if (map.get("startTime") != null) {
                startTime = dateFormat.parse((String) map.get("startTime"));
            }
            if (map.get("endTime") != null) {
                endTime = dateFormat.parse((String) map.get("endTime"));
            }
            ResponseResult addcourse = learningService.addcourse(userId, courseId, valid, startTime, endTime, xcTask);
            if(addcourse.isSuccess()){
                //添加选课成功，要向mq发送完成添加选课的消息
                rabbitTemplate.convertAndSend(RabbitMQConfig.EX_LEARNING_ADDCHOOSECOURSE,RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE_KEY,xcTask);
                log.error("send finish choose course taskId:{}", id);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            log.error("send finish choose course taskId:{}", id);
        }
    }
}
