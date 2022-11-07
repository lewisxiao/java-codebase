package com.lewisxiao.codebase.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.MarshallingCodec;
import org.redisson.config.Config;

public class RedisDemo {
    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379");

        RedissonClient redisson = Redisson.create(config);
        System.out.println(redisson.getBucket("age", new MarshallingCodec()).get());
    }
}
