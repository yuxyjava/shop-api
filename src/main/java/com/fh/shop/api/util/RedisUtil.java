package com.fh.shop.api.util;

import redis.clients.jedis.Jedis;

public class RedisUtil {

    public static void expire(String key, int seconds) {
        Jedis jedis = null;
        try {
            jedis = RedisPool.getResource();
            jedis.expire(key, seconds);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public static boolean exist(String key) {
        Jedis jedis = null;
        try {
            jedis = RedisPool.getResource();
            return jedis.exists(key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public static Long delete(String key) {
        Jedis jedis = null;
        try {
            jedis = RedisPool.getResource();
            return jedis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public static void setEx(String key, String value, int seconds) {
        Jedis jedis = null;
        try {
            jedis = RedisPool.getResource();
            jedis.setex(key, seconds, value);
        } catch (Exception e) {
            // 打印
            e.printStackTrace();
            // 抛出异常
            throw new RuntimeException(e);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public static void set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = RedisPool.getResource();
            jedis.set(key, value);
        } catch (Exception e) {
            // 打印
            e.printStackTrace();
            // 抛出异常
            throw new RuntimeException(e);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public static String get(String key) {
        Jedis jedis = null;
        try {
            jedis = RedisPool.getResource();
            String res = jedis.get(key);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            // 抛出异常
            throw new RuntimeException(e);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }
}
