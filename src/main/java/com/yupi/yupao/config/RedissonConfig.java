package com.yupi.yupao.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: YL
 * @Desc:
 * @create: 2024-05-03 21:57
 **/
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {
    private String host;
    private String port;
    @Bean
    public RedissonClient redissonClient(){
        String address = String.format("redis://%s:%s", host, port);
        Config config = new Config();
        config.useSingleServer().setAddress(address).setDatabase(3);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
