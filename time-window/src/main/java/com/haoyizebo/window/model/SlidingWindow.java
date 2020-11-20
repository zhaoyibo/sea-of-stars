package com.haoyizebo.window.model;

import com.aliyun.tair.tairhash.TairHash;
import com.haoyizebo.window.constant.WindowTypeEnum;

/**
 * 滑动窗口
 *
 * @author yibo
 * @since 2020-11-06
 */
class SlidingWindow extends AbstractWindow implements IWindow {

    @Override
    public boolean incrThenCompare(TimeWindow timeWindow, String uid, long amount, long threshold) {
        int seconds = Math.toIntExact(timeWindow.getParentSeconds());
        if (seconds <= 0) {
            return true;
        }

        TairHash tairHash = getTairHash();

        String redisKey = timeWindow.getRedisKey(getWindowType(), uid);
        String field = timeWindow.generateChildKey();

        if (amount != 0) {
            Long val = tairHash.exhincrBy(redisKey, field, amount);
            if (val == amount) {
                // 避免 -1 + 1 + 1 这样类似 ABA 问题。
                // exhttl 返回值说明：
                //      key或者field不存在：-2。
                //      field存在但是没有设置过期时间：-1。
                //      field存在且设置了过期时间：过期时间，单位为秒。
                Long exhttl = tairHash.exhttl(redisKey, field);
                if (exhttl != null && exhttl == -1) {
                    tairHash.exhexpire(redisKey, field, seconds);
                }
            }
        }

        long sum = tairHash.exhvals(redisKey)
                .stream().mapToLong(Long::parseLong).sum();

        if (sum > threshold) {
            // 兜底，给 redis key 设一个 x2 过期时间
            getJedis().expire(redisKey, (int) (timeWindow.getParentSeconds() * 2));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean compareThenIncr(TimeWindow timeWindow, String uid, long amount, long threshold) {
        return false;
    }

    @Override
    public WindowTypeEnum getWindowType() {
        return WindowTypeEnum.SLIDING;
    }

}
