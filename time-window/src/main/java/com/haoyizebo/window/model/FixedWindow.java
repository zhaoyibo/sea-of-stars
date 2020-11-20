package com.haoyizebo.window.model;


import com.haoyizebo.window.constant.WindowTypeEnum;
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


        Jedis redis = getJedis();

        String redisKey = timeWindow.getRedisKey(getWindowType(), uid);

        long val = redis.incrBy(redisKey, amount);
        if (val == amount) {
            redis.expire(redisKey, seconds);
        }
        if (val > threshold) {
            // 兜底策略，避免上边 expire 没有设置上。
            // ttl 返回值说明：
            //      当 key 不存在时，返回 -2 。
            //      当 key 存在但没有设置剩余生存时间时，返回 -1 。
            //      否则，以秒为单位，返回 key 的剩余生存时间。
            long ttl = redis.ttl(redisKey);
            if (ttl == -1) {
                redis.del(redisKey);
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean compareThenIncr(TimeWindow timeWindow, String uid, long amount, long threshold) {
        return false;
    }

    @Override
    public WindowTypeEnum getWindowType() {
        return WindowTypeEnum.FIXED;
    }

}
