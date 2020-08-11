package com.haoyizebo.hexo.util;

import com.haoyizebo.hexo.constant.HexoConstants;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * @author yibo
 * @since 2020-08-11
 */
@UtilityClass
public class FileUtils {

    /**
     * 处理制定目录下的所有文件，忽略 .DS_Store
     *
     * @param path     目录
     * @param consumer 文件，全文
     * @throws IOException
     */
    public void processFiles(String path, BiConsumer<File, String> consumer) throws IOException {
        for (File file : Objects.requireNonNull(new File(path).listFiles())) {

            if (file.getName().equals(HexoConstants.DS_Store)) {
                continue;
            }
            String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            consumer.accept(file, content);
        }
    }

}
