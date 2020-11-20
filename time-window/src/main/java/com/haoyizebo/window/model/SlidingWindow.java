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

        doIncr(tairHash, redisKey, field, amount, seconds);

        long sum = getBeforeAmount(tairHash, redisKey);

        if (sum > threshold) {
            guarantee(redisKey, timeWindow);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean compareThenIncr(TimeWindow timeWindow, String uid, long amount, long threshold) {
        int seconds = Math.toIntExact(timeWindow.getParentSeconds());
        if (seconds <= 0) {
            return true;
        }

        TairHash tairHash = getTairHash();

        String redisKey = timeWindow.getRedisKey(getWindowType(), uid);

        long sum = amount + getBeforeAmount(tairHash, redisKey);

        if (sum > threshold) {
            guarantee(redisKey, timeWindow);
            return false;
        }

        String field = timeWindow.generateChildKey();

        doIncr(tairHash, redisKey, field, amount, seconds);

        return true;
    }

    @Override
    public WindowTypeEnum getWindowType() {
        return WindowTypeEnum.SLIDING;
    }

    private long getBeforeAmount(TairHash tairHash, String redisKey) {
        return tairHash.exhvals(redisKey)
                .stream().mapToLong(Long::parseLong).sum();
    }

    private void doIncr(TairHash tairHash, String redisKey, String field, long amount, int seconds) {
        // 当 amount == 0 时，incr 无意义
        if (amount != 0) {
            Long val = tairHash.exhincrBy(redisKey, field, amount);
            if (val == amount) {
                // 理论上 val == amount 只出现在第一次时候，
                // 但为了避免 -1 + 1 + 1 这样类似 ABA 问题，再 ttl 一次
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
    }

    private void guarantee(String redisKey, TimeWindow timeWindow) {
        // 兜底策略，给 redis key 设一个 x2 过期时间
        getJedis().expire(redisKey, (int) (timeWindow.getParentSeconds() * 2));
    }

}
