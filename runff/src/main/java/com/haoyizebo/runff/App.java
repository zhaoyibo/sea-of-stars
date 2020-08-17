package com.haoyizebo.runff;

/**
 * @author yibo
 * @since 2020-08-17
 */
public class App {

    public static void main(String[] args) {
        PhotoDownloader downloader = new PhotoDownloader(args[0], args[1], args[2]);
        downloader.downloadAllWithoutWatermark();
//        downloader.downloadFavWithoutWatermark();
    }

}
