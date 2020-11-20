package com.haoyizebo.window.constant;


import java.util.regex.Pattern;

/**
 * @author yibo
 * @since 2020-10-30
 */
public class CommonConstants {

    public static final String DATETIME_REGEXP = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}";
    public static final String DATE_REGEXP = "[0-9]{4}-[0-9]{2}-[0-9]{2}";

    public static final Pattern WINDOW_PATTERN = Pattern.compile("^\\[(?<exp>\\S+?;\\S+?(;\\S*)?)]$");

    public static final String GLOBAL_UID = "0";

}
