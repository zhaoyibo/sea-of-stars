package com.haoyizebo.window.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author yibo
 * @since 2020-10-26
 */
@UtilityClass
public class TimeUtils {
    /**
     * (?<=\D)(?=\d)-匹配非数字(\D)和数字(\d)之间的位置
     * (?<=\d)(?=\D)-匹配数字和非数字之间的位置
     */
    private static final Pattern PATTERN;

    private static final Map<String, TimeUnit> TIME_UNIT_MAP;

    static {
        PATTERN = Pattern.compile("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

        TIME_UNIT_MAP = new HashMap<>();
        TIME_UNIT_MAP.put("d", TimeUnit.DAYS);
        TIME_UNIT_MAP.put("day", TimeUnit.DAYS);
        TIME_UNIT_MAP.put("days", TimeUnit.DAYS);
        TIME_UNIT_MAP.put("天", TimeUnit.DAYS);
        TIME_UNIT_MAP.put("日", TimeUnit.DAYS);
        TIME_UNIT_MAP.put("h", TimeUnit.HOURS);
        TIME_UNIT_MAP.put("hour", TimeUnit.HOURS);
        TIME_UNIT_MAP.put("hours", TimeUnit.HOURS);
        TIME_UNIT_MAP.put("时", TimeUnit.HOURS);
        TIME_UNIT_MAP.put("小时", TimeUnit.HOURS);
        TIME_UNIT_MAP.put("m", TimeUnit.MINUTES);
        TIME_UNIT_MAP.put("min", TimeUnit.MINUTES);
        TIME_UNIT_MAP.put("minute", TimeUnit.MINUTES);
        TIME_UNIT_MAP.put("minutes", TimeUnit.MINUTES);
        TIME_UNIT_MAP.put("分", TimeUnit.MINUTES);
        TIME_UNIT_MAP.put("分钟", TimeUnit.MINUTES);
        TIME_UNIT_MAP.put("s", TimeUnit.SECONDS);
        TIME_UNIT_MAP.put("sec", TimeUnit.SECONDS);
        TIME_UNIT_MAP.put("seconds", TimeUnit.SECONDS);
        TIME_UNIT_MAP.put("second", TimeUnit.SECONDS);
        TIME_UNIT_MAP.put("秒", TimeUnit.SECONDS);

    }


    /**
     * 将时间字符串表达式转为秒数
     *
     * @param timeStr 时间字符串，类似于 1h2m3s
     * @return 转换成的秒数
     */
    public static long convertStrToSeconds(String timeStr) {
        String[] arr = PATTERN.split(timeStr);
        System.out.println(Arrays.toString(arr));
        if (arr.length % 2 != 0) {
            throw new IllegalArgumentException();
        }
        long seconds = 0L;
        for (int i = 0; i < arr.length; i += 2) {
            long duration = Long.parseLong(arr[i]);
            if (duration == 0L) {
                continue;
            }
            TimeUnit timeUnit = TIME_UNIT_MAP.get(arr[i + 1]);
            if (timeUnit == null) {
                throw new IllegalArgumentException();
            }
            seconds += timeUnit.toSeconds(duration);
        }
        return seconds;
    }

    /**
     * 通过当前时间算出对应的滑动窗口内子窗口的唯一标识
     *
     * @param parentWindowSeconds 滑动窗口大小
     * @param childWindowCount    子窗口数量
     * @return 当前时间对应的子窗口的标识
     */
    private static long generateChildWindowKey(int parentWindowSeconds, int childWindowCount) {
        /**
         *  时间子窗口的大小（秒）
         */
        int childWindowSeconds = parentWindowSeconds / childWindowCount;
        /**
         * 当前时间戳(秒)
         */
        long nowSeconds = LocalDateTime.now().withYear(1970).toEpochSecond(ZoneOffset.UTC);
        return nowSeconds / childWindowSeconds;
    }

    /**
     * 将子窗口的标识格式化为 MM-dd HH:mm:ss 这样的时间
     *
     * @param childWindowKey      子窗口的 key
     * @param parentWindowSeconds 父窗口的秒数
     * @param childWindowCount    子窗口的数量
     * @return 时间，格式：MM-dd HH:mm:ss
     */
    public static String formatChildWindowKey(long childWindowKey, int parentWindowSeconds, int childWindowCount) {
        childWindowKey = childWindowKey * (parentWindowSeconds / childWindowCount);
        LocalDateTime t = LocalDateTime.ofEpochSecond(childWindowKey, 0, ZoneOffset.UTC);
        return t.format(DateTimeFormatter.ofPattern("MM-dd HH:mm:ss"));
    }

    public static void main(String[] args) {
        // 时间窗标识;时间窗时长[;时间窗内子窗口数量]
        String str = "w1;1hour2m30s;100";
        String timeStr = StringUtils.split(str, ';')[1];
        System.out.println(convertStrToSeconds(timeStr));
    }

}
