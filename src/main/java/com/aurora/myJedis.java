package com.aurora;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
@Slf4j
public class myJedis {
	private static JedisPool jedisPool;
    public static void configJedis(){
    	try {
    		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
    		jedisPoolConfig.setMaxTotal(50);
    		jedisPoolConfig.setMaxIdle(10);
    		jedisPool = new JedisPool(jedisPoolConfig, "onecloud", 6379);
	    	log.info("成功连接redis!");
			new error("成功连接redis!");
		} catch (Exception e) {
			log.error("redis连接失败", e);
			new error(" redis连接失败!"+e.getMessage());
		}
    }
    public static Jedis getJedis(){
        Jedis resource = jedisPool.getResource();
        return  resource;
    }
}
