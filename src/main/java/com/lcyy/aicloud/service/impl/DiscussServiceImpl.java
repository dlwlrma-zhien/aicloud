package com.lcyy.aicloud.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lcyy.aicloud.model.entity.Comment;
import com.lcyy.aicloud.model.entity.Discuss;
import com.lcyy.aicloud.mapper.DiscussMapper;
import com.lcyy.aicloud.model.entity.Support;
import com.lcyy.aicloud.model.entity.User;
import com.lcyy.aicloud.model.vo.CommentVo;
import com.lcyy.aicloud.model.vo.DiscussVo;
import com.lcyy.aicloud.service.ICommentService;
import com.lcyy.aicloud.service.IDiscussService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lcyy.aicloud.service.ISupportService;
import com.lcyy.aicloud.service.IUserService;
import com.lcyy.aicloud.util.AppVariableUtil;
import com.lcyy.aicloud.util.ResponseEntity;
import com.lcyy.aicloud.util.SecurityUtil;
import jakarta.annotation.Resource;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dlwlrma
 * @since 2024-11-07
 */
@Service
public class DiscussServiceImpl extends ServiceImpl<DiscussMapper, Discuss> implements IDiscussService {

    @Resource
    private ThreadPoolTaskExecutor threadPool;
    @Resource
    private IUserService userService;
    @Resource
    private CommentServiceImpl commentService;
    @Resource
    private DiscussMapper discussMapper;
    @Resource
    private KafkaTemplate kafkaTemplate;
    @Resource
    private ISupportService supportService;
    @Override
    public ResponseEntity addDiscuss(Discuss discuss) {
        discuss.setUid(SecurityUtil.getCurrentUserId().getUid());
        //向数据库中添加数据
        boolean save = this.save(discuss);
        if(save){
            return ResponseEntity.success("发布成功");
        }
        return ResponseEntity.failure("发布失败");
    }

    @Override
    public ResponseEntity getMyList() {
        return ResponseEntity.success(this.list(
                Wrappers.lambdaQuery(Discuss.class)
                        .eq(Discuss::getUid, SecurityUtil.getCurrentUserId().getUid())
                        .orderByDesc(Discuss::getDid)
        ));
    }

    @Override
    public ResponseEntity delete(Long did) {
        if(did == null || did <= 0){
            return ResponseEntity.failure("删除失败，请重试");
        }
        boolean removeResult = this.remove(
                Wrappers.lambdaQuery(Discuss.class)
                        .eq(Discuss::getDid, did)
                        .eq(Discuss::getUid, SecurityUtil.getCurrentUserId().getUid())
        );
        if(removeResult){
            return ResponseEntity.success("删除成功");
        }
        return ResponseEntity.failure("删除失败，请重试");
    }

    @Override
    public ResponseEntity detail(Long did) throws ExecutionException, InterruptedException {
        if(did<=0 || did == null){
            return ResponseEntity.failure("参数错误");
        }
        Discuss discuss = this.getById(did);
        if(discuss!=null && discuss.getDid()>0) {
            //0.增加阅读量
            threadPool.submit(()->{
                //1.更新数据
                this.updateReadCountInt(did);
                //2.返回对象数据+1
                discuss.setReadcount(discuss.getReadcount()+1);
            });


            //使用多线程异步编排任务执行
            //1.查询discuss表中的 username
            CompletableFuture<DiscussVo> task = CompletableFuture.supplyAsync(() -> {
                //对象转换
                DiscussVo discussVo = BeanUtil.toBean(discuss, DiscussVo.class);
                User user = userService.getById(discuss.getUid());
                if(user != null && user.getUid() > 0){
                    discussVo.setUsername(user.getUsername());
                }
                return discussVo;
            }, threadPool);

            //2.查询 discuss所对应的comment列表
            CompletableFuture<List<CommentVo>> task2 = CompletableFuture.supplyAsync(() -> {
                return commentService.getCommentList(did);
            }, threadPool);
            CompletableFuture.allOf(task, task2);
            //拼接结果返回数据
            HashMap<String, Object> result = new HashMap<>();
            result.put("discuss", task.get());
            result.put("commentlist", task2.get());
            return ResponseEntity.success(result);
        }
        return ResponseEntity.failure("该话题不存在");
    }

    @Override
    public int updateReadCountInt(Long did) {
        return discussMapper.updateReadCountInt(did);
    }

    @Override
    public int updateSupportCountInt(Long did) {
        return discussMapper.updateSupportCountInt(did);
    }

    @Override
    public ResponseEntity doSupport(Long did) {
        if(did == null || did <= 0){
            return ResponseEntity.failure("参数错误");
        }
        //向kafka发送消息
        kafkaTemplate.send(AppVariableUtil.DISCUSS_SUPPORT_TOPIC,did + "_" + SecurityUtil.getCurrentUserId().getUid());
        return ResponseEntity.success(true);
    }

    @Override
    public ResponseEntity listPage(Integer page, Integer type) {
        //参数预处理
        if(page == null || page <= 0){
            page = 1;
        }
        if(type == null || type <= 0){
            type = 1;
        }
        QueryWrapper<Discuss> queryWrapper = new QueryWrapper<>();
        if(type == 1){
            queryWrapper.orderByDesc("supportcount");
        }else{
            queryWrapper.orderByDesc("did");
        }
        //分页实现
        Page<Discuss> pageResult = this.page(new Page<>(page, AppVariableUtil.PAGE_SIZE),
                queryWrapper);
        return ResponseEntity.success(pageResult);
    }

    @KafkaListener(topics = {AppVariableUtil.DISCUSS_SUPPORT_TOPIC})
    public void listen(String data, Acknowledgment acknowledgment){
        long did = Long.parseLong(data.split("_")[0]);
        long uid = Long.parseLong(data.split("_")[1]);
        //1.先判断用户是否已经点赞过
        //获取用户信息
        List<Discuss> list = this.list(Wrappers.lambdaQuery(Discuss.class)
                .eq(Discuss::getDid, did)
                .eq(Discuss::getUid, uid)
        );
        if(list != null || list.size() > 0){
            //2.修改讨论表信息
            int result = this.updateSupportCountInt(did);
            if(result > 0){
                //3.在点赞表中添加点赞详情信息
                Support support = new Support()
                        .setDid(did)
                        .setUid(uid);
                //添加数据
                supportService.save(support);
            }
        }
        //手动确认消息
        acknowledgment.acknowledge();
    }
}
