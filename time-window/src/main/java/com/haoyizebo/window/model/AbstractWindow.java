package com.haoyizebo.window.model;

import com.aliyun.tair.tairhash.TairHash;
import com.haoyizebo.window.constant.WindowTypeEnum;
import com.haoyizebo.window.util.Tairs;
import redis.clients.jedis.Jedis;


/**
 * @author yibo
 * @since 2020-11-06
 */
abstract class AbstractWindow implements IWindow {

    protected abstract WindowTypeEnum getWindowType();

    protected Jedis getJedis() {
        return Tairs.getJedis();
    }

    protected TairHash getTairHash() {
        return new TairHash(getJedis());
    }

}
