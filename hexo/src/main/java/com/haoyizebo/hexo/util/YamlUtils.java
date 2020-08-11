package com.haoyizebo.hexo.util;

import lombok.experimental.UtilityClass;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yibo
 * @since 2020-08-11
 */
@UtilityClass
public class YamlUtils {

    private Yaml yaml;

    /**
     * 得到 yaml 对象
     *
     * @return
     */
    public Yaml getYaml() {
        if (yaml == null) {
            // 设置 yaml 输出格式
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            options.setIndent(4);
            options.setIndicatorIndent(2);
            options.setTimeZone(TimeZone.getTimeZone("+8"));
            yaml = new Yaml(options);
        }
        return yaml;
    }

    Pattern pattern = Pattern.compile("---\\n([\\s\\S]+)\\n---");

    public LinkedHashMap<String, Object> getFrontMatterMap(String content) {
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            Yaml yaml = getYaml();
            return yaml.load(matcher.group(1));
        }
        return null;
    }

    public String process(String content, Consumer<Map<String, Object>> consumer) {
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            Yaml yaml = getYaml();
            LinkedHashMap<String, Object> map = yaml.load(matcher.group(1));
            consumer.accept(map);
            String dump = yaml.dump(map);

            return "---\n" + dump + "---"
                    + content.substring(matcher.group(0).length());
        }
        return content;
    }


}
