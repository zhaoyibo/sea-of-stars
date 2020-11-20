package com.haoyizebo.window.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.haoyizebo.window.constant.WindowTypeEnum;
import com.haoyizebo.window.exception.WindowException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.beans.ConstructorProperties;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.haoyizebo.window.constant.CommonConstants.GLOBAL_UID;


/**
 * @author yibo
 * @since 2020-11-02
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimeWindow {

    /**
     * 标识
     */
    private final String label;
    /**
     * 时间窗的秒数
     */
    private final Long parentSeconds;
    /**
     * 子窗口的数量
     */
    private final Integer childCount;
    /**
     * 每个子窗口的秒数
     */
    private final Long childSeconds;

    /**
     * 规则 id
     */
    @JsonIgnore
    @Setter
    private String ruleId;

    @Setter
    @JsonIgnore
    private String keySuffix;

    @ConstructorProperties({"label", "parentSeconds", "childCount"})
    public TimeWindow(String label, Long parentSeconds, Integer childCount) {
        this.label = label;
        this.parentSeconds = parentSeconds;
        this.childCount = childCount;
        this.childSeconds = parentSeconds / childCount;
    }

    /**
     * 将时间窗字符串解析为 TimeWindow 对象
     *
     * @param text label;time-express[;shard] 例如 w1;1d2h3m4s[;100] 最后一项可选（最大值 1440）
     * @return TimeWindow
     */
    public static TimeWindow parse(String text) {
        String[] arr = StringUtils.split(text, ';');

        if (arr.length < 2) {
            throw new WindowException("时间窗格式有误：" + text);
        }

        String label = arr[0];
        String express = arr[1];
        if (StringUtils.isEmpty(label) || StringUtils.isBlank(express)) {
            throw new WindowException("时间窗格式有误：" + text);
        }

        Duration duration = Duration.parse(express);

        long seconds = duration.getParentSeconds();
        int count;
        if (arr.length >= 3 && NumberUtils.isDigits(arr[2])) {
            count = NumberUtils.toInt(arr[2]);
        } else {
            count = duration.getDefaultCount();
        }

        count = calculate(seconds, count);

        return new TimeWindow(label, seconds, count);
    }

    /**
     * 通过当前时间算出对应的滑动窗口内子窗口的唯一标识
     *
     * @return 当前时间对应的子窗口的标识
     */
    String generateChildKey() {
        /*
         * 当前时间戳(秒)
         */
        long nowSeconds = LocalDateTime.now().withYear(1970).toEpochSecond(ZoneOffset.UTC);
        return String.valueOf(nowSeconds / this.childSeconds);
    }

    private static final DateTimeFormatter CHILD_KEY_FORMATTER = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss");

    /**
     * 将子窗口的标识格式化为 MM-dd HH:mm:ss 这样的时间
     *
     * @param childKey 子窗口的 key
     * @return 时间，格式：MM-dd HH:mm:ss
     */
    String formatChildKey(long childKey) {
        childKey = childKey * this.childSeconds;
        LocalDateTime t = LocalDateTime.ofEpochSecond(childKey, 0, ZoneOffset.UTC);
        return t.format(CHILD_KEY_FORMATTER);
    }

    /**
     * 最大子窗口数量，这里设为 24*60，让 秒数/max 尽量能整除
     */
    private static final int MAX_CHILD_WINDOW_COUNT = 1440;

    private static int calculate(long seconds, int count) {
        count = Math.min(count, MAX_CHILD_WINDOW_COUNT);
        if (seconds % count == 0) {
            return count;
        }
        // 获取所有的约数并按 小->大 排序
        List<Long> divisors = divisors(seconds)
                .stream().sorted().collect(Collectors.toList());
        // 去掉 1 和 自身
        divisors = divisors.subList(1, divisors.size() - 1);
        // 找到和传入的 count 最接近的约数
        for (int i = 0; i < divisors.size(); i++) {
            int left = divisors.get(i).intValue();
            int right = divisors.get(i + 1).intValue();
            if (left < count && right > count) {
                if (right <= MAX_CHILD_WINDOW_COUNT) {
                    return right;
                } else {
                    return left;
                }
            }
        }
        // 如果是质数，返回 1
        return 1;
    }

    private static List<Long> divisors(long n) {
        List<Long> res = new ArrayList<>();
        res.add(1L);
        if (n == 1) {
            return res;
        }
        res.add(n);
        final long sqrt = (long) Math.sqrt(n);
        for (long i = 2; i < sqrt; i++) {
            if (n % i == 0) {
                res.add(n / i);
                res.add(i);
            }
        }
        if (sqrt * sqrt == n) {
            res.add(sqrt);
        }
        return res;
    }

    private static final IWindow FIXED = new FixedWindow();
    private static final IWindow SLIDING = new SlidingWindow();

    /**
     * 先累加，再判断。
     * 比如 threshold=100
     * 第一次 amount=50: 先判断 redis.get + 50 < 100（0+50, true），再 redis+50（=50），返回 true
     * 第二次 amount=20: 先判断 redis.get + 20 < 100（50+20, true），再 redis+20（=70），返回 true
     * 第三次 amount=50: 先判断 redis.get + 50 < 100（70+50, false），redis 里不累加，直接返回 false
     * 【注意】主要用于阻断的情况。如果仅报警使用的话，会有问题。
     *
     * @param uid       传用户的 uid 时，按用户个人来统计；传 null 或 "" 时，按全局来统计
     * @param amount    增加的数值
     * @param threshold 阈值
     * @return 未超阈值：true；超过阈值：false；
     */
    public boolean incrThenCompare(String uid, long amount, long threshold) {
        if (parentSeconds <= 0) {
            return true;
        }
        // 时间周期等于 1s，直接用固定窗口
        // 否则用滑动窗口
        IWindow window = parentSeconds <= 1 ? FIXED : SLIDING;
        return window.incrThenCompare(this, uid, amount, threshold);
    }

    /**
     * 先判断，再累加。
     * 比如 threshold=100
     * 第一次 amount=50: 先判断 redis.get + 50 < 100（0+50, true），再 redis+50（=50），返回 true
     * 第二次 amount=20: 先判断 redis.get + 20 < 100（50+20, true），再 redis+20（=70），返回 true
     * 第三次 amount=50: 先判断 redis.get + 50 < 100（70+50, false），redis 里不累加，直接返回 false
     * 【注意】主要用于阻断的情况。如果仅报警使用的话，会有问题。
     *
     * @param uid       传用户的 uid 时，按用户个人来统计；传 null 或 "" 时，按全局来统计
     * @param amount    增加的数值
     * @param threshold 阈值
     * @return 未超阈值：true；超过阈值：false；
     */
    public boolean compareThenIncr(String uid, long amount, long threshold) {
        if (parentSeconds <= 0) {
            return true;
        }
        // 时间周期等于 1s，直接用固定窗口
        // 否则用滑动窗口
        IWindow window = parentSeconds <= 1 ? FIXED : SLIDING;
        return window.compareThenIncr(this, uid, amount, threshold);
    }

    String getRedisKey(WindowTypeEnum windowType, String uid) {
        if (StringUtils.isBlank(uid)) {
            uid = GLOBAL_UID;
        }
        String key = String.format(windowType.getPrefix() + ":%s:%s:%s", ruleId, label, uid);
        if (StringUtils.isNotBlank(keySuffix)) {
            key += ":" + keySuffix;
        }
        return key;
    }

}
