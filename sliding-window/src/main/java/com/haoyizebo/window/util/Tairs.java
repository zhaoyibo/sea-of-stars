package com.haoyizebo.window.util;

import redis.clients.jedis.Jedis;

/**
 * @author yibo
 * @since 2020-10-28
 */
public class Tairs {

    public static Jedis getJedis() {
        return new Jedis("localhost", 6379);
    }

}
