package com.yupi.yupao.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yupao.model.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: YL
 * @Desc:
 * @create: 2024-05-03 22:32
 **/

@SpringBootTest
@Slf4j
public class RedissonTest {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    UserService userService;
    @Autowired
    RedisTemplate redisTemplate;
    @Test
    public void redission() {
        List<String> list = new ArrayList<>();
        list.add("zhangsan");
        System.out.println(list.get(0));
        list.remove(0);

        RList<Object> rList = redissonClient.getList("test");
//        rList.add("lisi");
        System.out.println(rList.get(0));
        rList.remove(0);

    }
    private List<Long> mainUserList = Arrays.asList(1L,2L,3L,4L,5L,6L,7L,8L,9L,10L,11L,12L,13L,14L,15L,16L,17L,18L);
    @Test
    public void testWatchDog(){
        RLock lock = redissonClient.getLock("recommend:lock:%s");
        try {
            if(lock.tryLock(0,-1, TimeUnit.MILLISECONDS)){
                log.info("getLock:"+ Thread.currentThread().getId());
                Thread.sleep(300000);
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
            }else{
                log.info("no lock");
            }
        } catch (InterruptedException e) {
            log.error("redis lock error",e);
        }finally {
            if(lock.isHeldByCurrentThread()){
                System.out.println("unlock"+Thread.currentThread().getId());
                lock.unlock();
            }
        }

    }
}
