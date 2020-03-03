package com.xuecheng.order.service;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.order.dao.XcTaskHisRepository;
import com.xuecheng.order.dao.XcTaskRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 **/
@Service
public class TaskService {

    @Autowired
    XcTaskRepository xcTaskRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    XcTaskHisRepository xcTaskHisRepository;

    /**
     * 查询 updateTime 时间之前的前 size 条任务
     */
    public List<XcTask> findXcTaskList(Date updateTime, int size) {
        //设置分页参数
        Pageable pageable = new PageRequest(0, size);
        //查询前n条任务
        Page<XcTask> all = xcTaskRepository.findByUpdateTimeBefore(updateTime, pageable);
        List<XcTask> list = all.getContent();
        return list;
    }

    /**
     * 通过乐观锁执行，更新时间，确定任务在至少一分钟内被唯一分配,不被其他服务执行,
     */
    @Transactional
    public int updateTask(String id, int version) {
        //通过乐观锁的方式来更新数据表，如果结果大于0说明取到任务,同时更新时间
        return xcTaskRepository.updateTaskVersion(id, version, new Date());
    }

    /**
     * 发布消息
     */
    public void publish(XcTask xcTask, String ex, String routingKey) {
        //1. 发送前再次确认任务是否还在，感觉没有必要
        Optional<XcTask> optional = xcTaskRepository.findById(xcTask.getId());
        if (optional.isPresent()) {
            //2. 发送消息
            rabbitTemplate.convertAndSend(ex, routingKey, xcTask);
            //3. 更新任务时间  todo 上面updateTask的时候已经更新时间了，所以这里多余了，或者直接把1、3步合并成cas
            XcTask one = optional.get();
            one.setUpdateTime(new Date());
            xcTaskRepository.save(one);
        }
    }

    /**
     * 完成选课任务
     */
    @Transactional
    public void finishTask(String taskId) {
        Optional<XcTask> optionalXcTask = xcTaskRepository.findById(taskId);
        if (optionalXcTask.isPresent()) {
            //当前任务
            XcTask xcTask = optionalXcTask.get();
            //历史任务
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask, xcTaskHis);
            xcTaskHisRepository.save(xcTaskHis);
            xcTaskRepository.delete(xcTask);
        }
    }
}
