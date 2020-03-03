package com.xuecheng.learning.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.learning.respones.LearningCode;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.learning.config.RabbitMQConfig;
import com.xuecheng.learning.service.LearningService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author Walker_Don
 * @version V1.0
 * @ClassName ChooseCourseTask
 * @date 2019年07月11日 下午 2:18
 */
@Component
public class ChooseCourseTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);

    @Autowired
    private LearningService learningService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 监听并添加选课,完成后发送mq信息
     */
    @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_ADDCHOOSECOURSE)
    public void receiveAddChooseCourseTask(XcTask xcTask) {

        //1. 监听到信息,验证数据
        if (xcTask == null || StringUtils.isEmpty(xcTask.getId())) {
            ExceptionCast.cast(LearningCode.CHOOSECOURSE_TASKISNULL);
        }
        LOGGER.info("receive choose course task,taskId:{}", xcTask.getId());

        //2. 取出消息的内容
        String requestBody = xcTask.getRequestBody();
        Map map = JSON.parseObject(requestBody, Map.class);
        String userId = (String) map.get("userId");
        String courseId = (String) map.get("courseId");

        // 2.1 解析出valid, Date startTime, Date endTime...
        Date startTime = null;
        Date endTime = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY‐MM‐dd HH:mm:ss");
        if (map.get("startTime") != null) {
            try {
                startTime = dateFormat.parse((String) map.get("startTime"));
            } catch (ParseException e) {
                e.printStackTrace();
                System.out.println("startTime解析失败");
            }
        }
        if (map.get("endTime") != null) {
            try {
                endTime = dateFormat.parse((String) map.get("endTime"));
            } catch (ParseException e) {
                e.printStackTrace();
                System.out.println("endTime解析失败");
            }
        }

        //3. 添加选课，就是付费成功的课程
        boolean result = learningService.addCourse(userId, courseId, null, startTime, endTime, xcTask);

        //4. 返回信息
        if (result) {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EX_LEARNING_ADDCHOOSECOURSE,
                    RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE_KEY, xcTask);
        }

        LOGGER.info("send finish choose course taskId:{}", xcTask.getId());
    }
}
