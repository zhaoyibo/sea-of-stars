package com.haoyizebo.window.model;


import com.haoyizebo.window.constant.WindowTypeEnum;
import org.apache.commons.lang3.math.NumberUtils;
import redis.clients.jedis.Jedis;

/**
 * 固定窗口
 *
 * @author yibo
 * @since 2020-11-06
 */
class FixedWindow extends AbstractWindow implements IWindow {

    @Override
    public boolean incrThenCompare(TimeWindow timeWindow, String uid, long amount, long threshold) {
        int seconds = Math.toIntExact(timeWindow.getParentSeconds());
        if (seconds <= 0) {
            return true;
        }

        Jedis jedis = getJedis();

        String redisKey = timeWindow.getRedisKey(getWindowType(), uid);

        long val = jedis.incrBy(redisKey, amount);
        if (val == amount) {
            jedis.expire(redisKey, seconds);
        }
        if (val > threshold) {
            guarantee(getJedis(), redisKey);
            return false;
        }
        return true;
    }

    @Override
    public boolean compareThenIncr(TimeWindow timeWindow, String uid, long amount, long threshold) {
        int seconds = Math.toIntExact(timeWindow.getParentSeconds());
        if (seconds <= 0) {
            return true;
        }

        Jedis jedis = getJedis();
        String redisKey = timeWindow.getRedisKey(getWindowType(), uid);

        long val = amount;

        String s = jedis.get(redisKey);
        if (NumberUtils.isDigits(s)) {
            val += Long.parseLong(s);
        }

        if (val > threshold) {
            guarantee(jedis, redisKey);
            return false;
        }

        jedis.incrBy(redisKey, amount);
        return false;
    }

    private void guarantee(Jedis jedis, String redisKey) {
        // 兜底策略，避免上边 expire 没有设置上。
        // ttl 返回值说明：
        //      当 key 不存在时，返回 -2 。
        //      当 key 存在但没有设置剩余生存时间时，返回 -1 。
        //      否则，以秒为单位，返回 key 的剩余生存时间。
        long ttl = jedis.ttl(redisKey);
        if (ttl == -1) {
            jedis.del(redisKey);
        }
    }

    @Override
    public WindowTypeEnum getWindowType() {
        return WindowTypeEnum.FIXED;
    }

}
