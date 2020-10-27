package com.haoyizebo.window.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yibo
 * @since 2020-10-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WindowInfo {

    /**
     * 时间窗的秒数
     */
    private Long parentSeconds;
    /**
     * 子窗口的数量
     */
    private Integer childCount;
    /**
     * 每个子窗口的秒数
     */
    private Long childSeconds;

    /**
     * 将时间窗字符串解析为 WindowInfo 对象
     *
     * @param text 1d2h3m4s[;100]
     * @return WindowInfo
     */
    public static WindowInfo parse(String text) {
        String[] arr = StringUtils.split(text, ';');

        if (arr.length < 1) {
            throw new IllegalArgumentException();
        }

        String express = arr[0];
        if (StringUtils.isBlank(express)) {
            throw new IllegalArgumentException();
        }

        Duration duration = Duration.parse(express);

        long seconds = duration.getParentSeconds();
        int count;
        if (arr.length >= 2 && NumberUtils.isDigits(arr[1])) {
            count = NumberUtils.toInt(arr[1]);
        } else {
            count = duration.getDefaultCount();
        }

        count = calculate(seconds, count);

        return WindowInfo.builder()
                .parentSeconds(seconds)
                .childCount(count)
                .childSeconds(seconds / count)
                .build();
    }

    /**
     * 通过当前时间算出对应的滑动窗口内子窗口的唯一标识
     *
     * @return 当前时间对应的子窗口的标识
     */
    public long generateChildKey() {
        /**
         * 当前时间戳(秒)
         */
        long nowSeconds = LocalDateTime.now().withYear(1970).toEpochSecond(ZoneOffset.UTC);
        return nowSeconds / this.childSeconds;
    }

    private final DateTimeFormatter CHILD_KEY_FORMATTER = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss");

    /**
     * 将子窗口的标识格式化为 MM-dd HH:mm:ss 这样的时间
     *
     * @param childKey 子窗口的 key
     * @return 时间，格式：MM-dd HH:mm:ss
     */
    public String formatChildKey(long childKey) {
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


}
