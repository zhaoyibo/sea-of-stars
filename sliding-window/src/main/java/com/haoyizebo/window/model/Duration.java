package com.haoyizebo.window.model;

import com.haoyizebo.window.exception.WindowException;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

/**
 * @author yibo
 * @since 2020-11-02
 */
@Data
class Duration {

    private int days;
    private int hours;
    private int minutes;
    private int seconds;

    public long getParentSeconds() {
        return TimeUnit.DAYS.toSeconds(days)
                + TimeUnit.HOURS.toSeconds(hours)
                + TimeUnit.MINUTES.toSeconds(minutes)
                + seconds;
    }

    public int getDefaultCount() {
        return days > 0 ? days * 24 :
                hours > 0 ? hours * 60 :
                        minutes > 0 ? minutes * 60 :
                                seconds > 0 ? seconds : 1;
    }

    /**
     * (?<=\D)(?=\d)-匹配非数字(\D)和数字(\d)之间的位置
     * (?<=\d)(?=\D)-匹配数字和非数字之间的位置
     */
    private static final Pattern PATTERN;

    private static final Map<String, BiConsumer<Duration, Integer>> TIME_UNIT_MAP;

    static {
        PATTERN = Pattern.compile("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

        TIME_UNIT_MAP = new HashMap<>();

        TIME_UNIT_MAP.put("d", Duration::setDays);
        TIME_UNIT_MAP.put("day", TIME_UNIT_MAP.get("d"));
        TIME_UNIT_MAP.put("days", TIME_UNIT_MAP.get("d"));
        TIME_UNIT_MAP.put("天", TIME_UNIT_MAP.get("d"));
        TIME_UNIT_MAP.put("日", TIME_UNIT_MAP.get("d"));

        TIME_UNIT_MAP.put("h", Duration::setHours);
        TIME_UNIT_MAP.put("hour", TIME_UNIT_MAP.get("h"));
        TIME_UNIT_MAP.put("hours", TIME_UNIT_MAP.get("h"));
        TIME_UNIT_MAP.put("时", TIME_UNIT_MAP.get("h"));
        TIME_UNIT_MAP.put("小时", TIME_UNIT_MAP.get("h"));

        TIME_UNIT_MAP.put("m", Duration::setMinutes);
        TIME_UNIT_MAP.put("min", TIME_UNIT_MAP.get("m"));
        TIME_UNIT_MAP.put("minute", TIME_UNIT_MAP.get("m"));
        TIME_UNIT_MAP.put("minutes", TIME_UNIT_MAP.get("m"));
        TIME_UNIT_MAP.put("分", TIME_UNIT_MAP.get("m"));
        TIME_UNIT_MAP.put("分钟", TIME_UNIT_MAP.get("m"));

        TIME_UNIT_MAP.put("s", Duration::setSeconds);
        TIME_UNIT_MAP.put("sec", TIME_UNIT_MAP.get("s"));
        TIME_UNIT_MAP.put("seconds", TIME_UNIT_MAP.get("s"));
        TIME_UNIT_MAP.put("second", TIME_UNIT_MAP.get("s"));
        TIME_UNIT_MAP.put("秒", TIME_UNIT_MAP.get("s"));

    }

    /**
     * 将时间字符串表达式转为秒数
     *
     * @param text 时间字符串，类似于 1h2m3s
     * @return Duration
     */
    public static Duration parse(String text) {
        String[] arr = PATTERN.split(text);
        if (arr.length % 2 != 0) {
            throw new WindowException("周期格式有误：" + text);
        }
        Duration duration = new Duration();
        for (int i = 0; i < arr.length; i += 2) {
            int anInt = Integer.parseInt(arr[i]);
            if (anInt == 0L) {
                continue;
            }
            BiConsumer<Duration, Integer> consumer = TIME_UNIT_MAP.get(arr[i + 1]);
            consumer.accept(duration, anInt);
        }
        return duration;
    }

}
