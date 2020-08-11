package com.haoyizebo.hexo;

import com.haoyizebo.hexo.util.FileUtils;
import com.haoyizebo.hexo.util.YamlUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.haoyizebo.hexo.constant.HexoConstants.PATTERN;
import static com.haoyizebo.hexo.constant.HexoConstants.POST_DIR;

/**
 * @author yibo
 */
public class FrontMatterMain {

    public static void main(String[] args) throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);

        FileUtils.processFiles(POST_DIR, (file, content) -> {
            String fileName = file.getName();

            // 文件名示例 2016-01-03-golang-note-1.md
            String title = fileName.substring(11, fileName.length() - 3);

            String post = YamlUtils.process(content, frontMatterMap -> {
                // 去除不要的数据
                frontMatterMap.remove("id");
                frontMatterMap.remove("permalink");
                frontMatterMap.remove("addrlink");

                // 需要将 Date 转为 String，否则默认的格式不对
                Object date = frontMatterMap.get("date");
                if (date instanceof Date) {
                    frontMatterMap.put("date", sdf.format(date));
                }

                // 处理 index_img
                if (!frontMatterMap.containsKey("index_img")) {
                    frontMatterMap.put("index_img", "");
                } else {
                    String imgUrl = (String) frontMatterMap.get("index_img");
                    if (imgUrl == null || imgUrl.length() <= 0) {
                        return;
                    }
                    if (imgUrl.contains("thumbnail")) {
                        return;
                    }
                    String[] split = imgUrl.substring(imgUrl.lastIndexOf('/') + 1).split("\\.");
                    imgUrl = "https://cdn.jsdelivr.net/gh/zhaoyibo/resource@gh-pages/img/" + split[0] + "-thumbnail." + split[1];
                    frontMatterMap.put("index_img", imgUrl);
                }
            });

            // 写文件
            try {
                Files.write(file.toPath(), post.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

}
