package com.yupi.yupao.service;
import java.util.Date;

import com.yupi.yupao.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * @author: YL
 * @Desc:
 * @create: 2024-05-01 12:43
 **/
@SpringBootTest
public class redisTestByMySelf {
    @Autowired
    RedisTemplate redisTemplate;


    @Test
    public void redisInsert(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("test01","zhangsan");
        User user = new User();
        user.setId(0L);
        user.setUsername("YL");

        valueOperations.set("user",user);

        Object test01 = valueOperations.get("test01");
        System.out.println(test01);
        Object user1 = valueOperations.get("user");
        System.out.println(user1);

    }
}
