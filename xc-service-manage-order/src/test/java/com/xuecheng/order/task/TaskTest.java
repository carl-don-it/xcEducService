package com.xuecheng.order.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * todo
 *
 * @author Carl Don
 * @version V1.0
 * @date 2020年02月24日 下午 1:22
 */
@Component
public class TaskTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskTest.class);

    //定义任务调试策略
//    @Scheduled(cron="0/3 * * * * *")//每隔3秒去执行
//       @Scheduled(fixedRate = 3000) //在任务开始后3秒执行下一次调度
//       @Scheduled(fixedDelay = 3000) //在任务结束后3秒后才开始执行
    public void task1() {
        LOGGER.info("===============测试定时任务1开始===============");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("===============测试定时任务1结束===============");

    }

    //定义任务调试策略
//    @Scheduled(cron="0/3 * * * * *")//每隔3秒去执行
//    @Scheduled(fixedRate = 3000) //在任务开始后3秒执行下一次调度
//       @Scheduled(fixedDelay = 3000) //在任务结束后3秒后才开始执行
    public void task2() {
        LOGGER.info("===============测试定时任务2开始===============");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("===============测试定时任务2结束===============");

    }
}
