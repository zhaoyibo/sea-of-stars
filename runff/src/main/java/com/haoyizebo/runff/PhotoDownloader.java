package com.haoyizebo.runff;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import okhttp3.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yibo
 * @since 2020-08-17
 */
public class PhotoDownloader {

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
    private static final ObjectMapper xmlMapper = new XmlMapper();
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    private final String sid;
    private final String cookie;
    private final String dir;

    public PhotoDownloader(String sid, String cookie, String dir) {
        this.sid = sid;
        this.cookie = cookie;
        this.dir = dir;
    }

    public List<Photo> getPhotoList() {
        String url = "https://www.runff.com/html/live/" + sid + ".html?isbxapimode=true&_xmltime=" + System.currentTimeMillis() + ".0.31219757728568875";

        Headers headers = new Headers.Builder()
                .add("Origin", "https://www.runff.com")
                .add("X-Requested-With", "XMLHttpRequest")
                .add("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 13_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/7.0.15(0x17000f25) NetType/WIFI Language/zh_CN")
                .add("Referer", "https://www.runff.com/html/live/" + sid + ".html")
                .add("Host", "www.runff.com")
                .add("Content-Type", "text/plain")
                .add("Cookie", cookie)
                .build();

        String bodyStr = "<?xml version=\"1.0\" encoding=\"utf-8\"?><BxMessage><AppId>BxAPI</AppId><Type>1</Type><Action>getNumberList</Action><Data/></BxMessage>";
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(bodyStr.getBytes()))
                .headers(headers)
                .build();
        List<Integer> ids;
        try (Response response = okHttpClient.newCall(request).execute()) {
            String ret = Objects.requireNonNull(response.body()).string();
            BxMessage bxMessage = xmlMapper.readValue(ret, BxMessage.class);
            if (bxMessage.getStateCode() == 2 && bxMessage.getMessage().equals("请登录")) {
                throw new RunffException("Cookie 无效，请重新获取！");
            }
            String list = bxMessage.getData().getList();
            ids = jsonMapper.readValue(list, new TypeReference<List<Map<String, Object>>>() {
            })
                    .stream()
                    .map(m -> {
                        Object id = m.get("id");
                        return (int) id;
                    })
                    .filter(id -> id > 0)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RunffException("获取列表 id 失败！");
        }
        List<Photo> photos = new ArrayList<>();
        for (Integer id : ids) {
            bodyStr = "<?xml version=\"1.0\" encoding=\"utf-8\"?><BxMessage><AppId>BxAPI</AppId><Type>1</Type><Action>getPhotoList</Action><Data><fid>" + id + "</fid><number></number><pageindex>1</pageindex><time></time><sign>false</sign><pagesize>200</pagesize></Data></BxMessage>";
            request = new Request.Builder()
                    .post(RequestBody.create(bodyStr.getBytes()))
                    .headers(headers)
                    .url(url)
                    .build();
            try (Response response = okHttpClient.newCall(request).execute()) {
                String ret = Objects.requireNonNull(response.body()).string();
                BxMessage bxMessage = xmlMapper.readValue(ret, BxMessage.class);
                photos.addAll(jsonMapper.readValue(bxMessage.getData().getList(), new TypeReference<List<Photo>>() {
                }));
            } catch (IOException e) {
                System.err.println("获取照片列表失败 id：" + id);
                continue;
            }
            sleep(500);
        }
        return photos;
    }

    public void print(List<Photo> photos) {
        for (Photo photo : photos) {
            System.out.println("https://www.runff.com" + photo.getBig());
        }
    }

    public void downloadFavWithoutWatermark() {
        down(getFavPhotoList(), "https://www.runff.com", Paths.get(dir, "fav-without-watermark"));
    }

    public void downloadAllWithWatermark() {
        downloadWithWatermark(getPhotoList());
    }

    public void downloadWithWatermark(List<Photo> photos) {
        down(photos.stream().map(Photo::getBig).collect(Collectors.toList()), "https://p.chinarun.com", Paths.get(dir, "with-watermark"));
    }

    public void downloadAllWithoutWatermark() {
        downloadWithoutWatermark(getPhotoList());
    }

    public void downloadWithoutWatermark(List<Photo> photos) {
        down(photos.stream().map(Photo::getBig).collect(Collectors.toList()), "https://www.runff.com", Paths.get(dir, "without-watermark"));
    }

    private void down(List<String> paths, String domain, Path dir) {
        int skipCount = 0;
        int successCount = 0;
        int failureCount = 0;
        List<String> failureUrls = new ArrayList<>();
        File dirFile = dir.toFile();
        if (!dirFile.exists()) {
            boolean mkdirs = dirFile.mkdirs();
            if (mkdirs) {
                System.out.println("文件夹 " + dirFile.getAbsolutePath() + " 不存在，已自动创建。");
            } else {
                throw new RunffException("文件夹 " + dirFile.getAbsolutePath() + " 不存在，自动创建失败，请手动创建。");
            }
        } else {
            System.out.println("文件夹 " + dirFile.getAbsolutePath() + " 已存在。");
        }

        for (String path : paths) {
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            File file = new File(dirFile, fileName);
            if (file.exists()) {
                skipCount++;
                continue;
            }

            String url = domain + path;
            System.out.println("开始下载：" + url);
            Request request = new Request.Builder()
                    .get()
                    .url(url)
                    .header("Origin", "https://www.runff.com")
                    .header("Referer", "https://www.runff.com/html/live/" + sid + ".html")
                    .header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 13_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/7.0.15(0x17000f25) NetType/WIFI Language/zh_CN")
                    .build();
            Call call = okHttpClient.newCall(request);

            try (Response response = call.execute();
                 InputStream is = Objects.requireNonNull(response.body()).byteStream();
                 FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buf = new byte[2048];
                int len;
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                fos.flush();
                successCount++;
            } catch (IOException e) {
                failureUrls.add(url);
                failureCount++;
            }

            sleep(500);
        }

        System.out.println("======================================");
        System.out.println("skipCount: " + skipCount);
        System.out.println("successCount: " + successCount);
        System.out.println("failureCount: " + failureCount);
        if (failureCount > 0) {
            System.out.println("failureUrls:");
            for (String failureUrl : failureUrls) {
                System.out.println("\t- " + failureUrl);
            }
        }
        System.out.println("======================================");
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<String> getFavPhotoList() {
        long l = System.currentTimeMillis();
        int pid = Integer.parseInt(sid.substring(1));
        String url = "https://app.chinarun.com/html/apiuser/api.ashx?method=photo&action=getfavphotolist&callback=jQuery112408491772726405646_" + l + "&pageindex=1&pid=" + pid + "&_=" + l;
        Headers headers = new Headers.Builder()
                .add("X-Requested-With", "XMLHttpRequest")
                .add("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 13_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/7.0.15(0x17000f25) NetType/WIFI Language/zh_CN miniProgram")
                .add("Referer", "https://app.chinarun.com/shtml/app/html/user/photo_fav.html")
                .add("Host", "app.chinarun.com")
                .add("Cookie", cookie)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .headers(headers)
                .build();
        List<String> paths = new ArrayList<>();
        try (Response response = okHttpClient.newCall(request).execute()) {
            String ret = Objects.requireNonNull(response.body()).string();
            String json = ret.substring(42, ret.length() - 1);
            JsonNode jsonNode = jsonMapper.readTree(json);
            JsonNode list = jsonNode.get("data").get("list");
            list.elements().forEachRemaining(node -> {
                String img = node.get("img").asText();
                img = img.replace("//p.chinarun.com", "");
                img = img.replace("/small/", "/big/");
                img = img.substring(0, img.lastIndexOf('?'));
                paths.add(img);
            });
        } catch (IOException e) {
            System.err.println("获取收藏列表失败 pid：" + pid);
        }
        return paths;
    }

}
