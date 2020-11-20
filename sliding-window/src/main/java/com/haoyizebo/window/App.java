package com.haoyizebo.window;

import com.haoyizebo.window.model.TimeWindow;

/**
 * @author yibo
 * @since 2020-10-20
 */
public class App {

    public static void main(String[] args) {
        TimeWindow timeWindow = TimeWindow.parse("w1;2h;100");
        boolean ok = timeWindow.incrThenCompare("1", 100, 1000);
    }

}
