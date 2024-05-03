package com.yupi.yupao.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yupao.model.domain.User;
import com.yupi.yupao.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: YL
 * @Desc:
 * @create: 2024-05-02 16:12
 **/
@Component
@Slf4j
public class PreCacheByMySelf {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    private List<Long> mainUserList = Arrays.asList(1L,2L,3L,4L,5L,6L,7L,8L,9L,10L,11L,12L,13L,14L,15L,16L,17L,18L);
    @Scheduled(cron = "0 * * * * * ")
    public void CacheMainUser(){
        for (Long userId : mainUserList) {
            QueryWrapper<User> objectQueryWrapper = new QueryWrapper<>();
            Page<User> page = userService.page(new Page<>(1, 20), objectQueryWrapper);
            String recommendKey = String.format("recommend:user:%s", userId);
            log.info("Scheduled Task,recommendKey: {}",recommendKey);
            try {
                redisTemplate.opsForValue().set(recommendKey,page,30000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.error("redis set key error",e);
            }
        }
    }
}
