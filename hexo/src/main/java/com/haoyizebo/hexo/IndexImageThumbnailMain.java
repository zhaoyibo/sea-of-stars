package com.haoyizebo.hexo;

import com.haoyizebo.hexo.constant.HexoConstants;
import com.haoyizebo.hexo.util.FileUtils;
import com.haoyizebo.hexo.util.YamlUtils;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author yibo
 * @since 2020-08-10
 */
public class IndexImageThumbnailMain {

    static String originDir = "/Users/yibo/hexo-blog/resource/img/";
    static String targetDir = "/Users/yibo/hexo-blog-fluid/index-images/";

    public static void main(String[] args) throws IOException {
//        copy();
//        thumbnail();
    }

    private static void copy() throws IOException {

        FileUtils.processFiles(HexoConstants.POST_DIR, (file, s) -> {
            LinkedHashMap<String, Object> frontMatterMap = YamlUtils.getFrontMatterMap(s);

            String indexImgUrl = (String) Objects.requireNonNull(frontMatterMap).get("index_img");
            if (indexImgUrl == null || indexImgUrl.length() == 0) {
                System.out.println(file.getName() + " has no indexImg, skip.");
                return;
            }

            String imgFileName = indexImgUrl.substring(indexImgUrl.lastIndexOf('/') + 1);

            File image = new File(originDir + imgFileName);
            if (!image.exists()) {
                System.err.println(image.getName() + " not exist!");
                return;
            }

            try {
                Files.copy(image.toPath(), Paths.get(targetDir, imgFileName), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }


    private static void thumbnail() throws IOException {
        Thumbnails.of(Objects.requireNonNull(new File(targetDir).listFiles()))
                .height(200)
                .outputQuality(0.8)
                .useOriginalFormat()
                .toFiles(Rename.SUFFIX_HYPHEN_THUMBNAIL);
    }

}
