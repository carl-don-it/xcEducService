package com.xuecheng.order.controller;

import com.xuecheng.framework.model.response.Response;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单类api
 *
 * @author Carl Don
 * @version V1.0
 * @date 2020年02月24日 下午 1:24
 */
@RestController
public class OrderController {


    /**
     * 完成分布式事务的第一步
     * 进行订单的付费操作，支付成功后，订单服务向本地数据库更新订单状态，并向消息表写入“添加选课消息”，通过本地数据库保证订单状态和添加选课消息的事务。
     */
    public Response pay(Object o) {
        //..实际上直接利用数据库现场的选课任务
        return null;
    }
}
