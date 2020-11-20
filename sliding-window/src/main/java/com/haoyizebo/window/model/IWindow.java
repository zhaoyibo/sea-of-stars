package com.haoyizebo.window.model;

/**
 * @author yibo
 * @since 2020-11-06
 */
interface IWindow {

    /**
     * 先累加，再判断。
     * 比如 threshold=100
     * 第一次 amount=50: redis+50（=50） 再判断 redis < 100（true）返回 true
     * 第二次 amount=60: redis+60（=110） 再判断 redis < 100（false）返回 true
     * 第三次 amount=10: redis+10（=120） 再判断 redis < 100（false）返回 false
     * 【注意】主要用于报警不阻断的情况。如果阻断使用的话，会有问题。
     *
     * @param timeWindow 时间窗信息
     * @param uid        传用户的 uid 时，按用户个人来统计；传 null 或 "" 时，按全局来统计
     * @param amount     增加的数值
     * @param threshold  阈值
     * @return 未超阈值：true；超过阈值：false；
     */
    boolean incrThenCompare(TimeWindow timeWindow, String uid, long amount, long threshold);

    /**
     * 先判断，再累加。
     * 比如 threshold=100
     * 第一次 amount=50: 先判断 redis.get + 50 < 100（0+50, true），再 redis+50（=50），返回 true
     * 第二次 amount=20: 先判断 redis.get + 20 < 100（50+20, true），再 redis+20（=70），返回 true
     * 第三次 amount=50: 先判断 redis.get + 50 < 100（70+50, false），redis 里不累加，直接返回 false
     * 【注意】主要用于阻断的情况。如果仅报警使用的话，会有问题。
     *
     * @param timeWindow 时间窗信息
     * @param uid        传用户的 uid 时，按用户个人来统计；传 null 或 "" 时，按全局来统计
     * @param amount     增加的数值
     * @param threshold  阈值
     * @return 未超阈值：true；超过阈值：false；
     */
    boolean compareThenIncr(TimeWindow timeWindow, String uid, long amount, long threshold);

}
