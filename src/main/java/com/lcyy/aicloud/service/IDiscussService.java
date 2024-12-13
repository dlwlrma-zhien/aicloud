package com.lcyy.aicloud.service;

import com.lcyy.aicloud.model.entity.Discuss;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lcyy.aicloud.util.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.concurrent.ExecutionException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dlwlrma
 * @since 2024-11-07
 */
public interface IDiscussService extends IService<Discuss> {

    ResponseEntity addDiscuss(Discuss discuss);

    ResponseEntity getMyList();

    ResponseEntity delete(Long did);

    ResponseEntity detail(Long did) throws ExecutionException, InterruptedException;

    int updateReadCountInt(@RequestParam("did") Long did);

    int updateSupportCountInt(@RequestParam("did") Long did);

    ResponseEntity doSupport(Long did);

    ResponseEntity listPage(Integer page, Integer size);
}
