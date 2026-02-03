package xyz.bx25.demo.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private String port;

    @Value("${spring.data.redis.password}")
    private String password;

    @Value("${spring.data.redis.database}")
    private int database;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        // 单节点模式
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port) // 必须加 redis:// 前缀
                .setPassword(password)
                .setDatabase(database)
                .setTimeout(3000)
                .setConnectionMinimumIdleSize(2)
                .setConnectionPoolSize(8);

        return Redisson.create(config);
    }
}