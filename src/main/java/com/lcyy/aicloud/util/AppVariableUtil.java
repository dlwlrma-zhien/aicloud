package com.lcyy.aicloud.util;

/**
 * @author: dlwlrma
 * @data 2024年11月10日 16:00
 * @Description: TODO:全局变量
 */
public class AppVariableUtil {

    //讨论表点赞名称
    public static final String DISCUSS_SUPPORT_TOPIC = "DISCUSS_SUPPORT_TOPIC";
    //每页显示的条数
    public static final int PAGE_SIZE = 4;

    //获取分布式锁的key
    public static String getModelLockKey(Long uid,int model,int type){
        return "MODEL_LOCK_"+uid+"_"+model+"_"+type;
    }

    //添加列表缓存的key
    public static String getListCacheKey(Long uid,int model,int type){
        return "MODEL_CACHE_"+uid+"_"+model+"_"+type;
    }
}
