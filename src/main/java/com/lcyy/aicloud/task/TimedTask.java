package com.lcyy.aicloud.task;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lcyy.aicloud.model.entity.User;
import com.lcyy.aicloud.service.IUserService;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author: dlwlrma
 * @data 2024年11月13日 16:37
 * @Description xxljob定时任务
 */
@Component
public class TimedTask {

    @Resource
    private IUserService userService;

    /**
     * 每天凌晨给用户300次的调度额度
     * @author dlwlrma
     * @date 2024/11/13 16:54
     */
    @XxlJob("resetUserCount")
    public void resetUserCount(){
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.set("usecount",300);
        boolean updateResult = userService.update(wrapper);
        if(!updateResult){
            //todo:通知相关人员检查异常情况
        }
    }
}
