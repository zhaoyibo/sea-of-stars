## 使用说明

**首先在手机上**

0. 用微信关注公众号"跑步维生素"，找到对应赛事的照片入口，通过"人脸识别"或"查找号码牌"添加出要下载的照片列表

**然后在电脑上**

1. 使用 Chrome 打开 https://www.runff.com/html/live/event.html
3. 选择你下载照片的赛事（这时会跳到登录页）
2. 先打开 Chrome 的"开发者工具"（右侧三个点-更多工具-开发者工具），勾选 XHR
  ![](https://cdn.jsdelivr.net/gh/zhaoyibo/resource@gh-pages/img/1597655816973.png)
4. 微信扫码登录
  ![](https://cdn.jsdelivr.net/gh/zhaoyibo/resource@gh-pages/img/1597655932085.png)
5. 上图中①处的 "s2746" 即为 sid
6. 点上图中②处，复制下图中红线标注的完整的 cookie 信息
  ![](https://cdn.jsdelivr.net/gh/zhaoyibo/resource@gh-pages/img/1597656119123.png)
7. 运行
    ```shell script
    java -jar App.jar {sid} {cookie} {dir}
    ```
    例如
    ```shell script
    java -jar App.jar "s2746" "ASP.NET_SessionId=ccc; bxmssmemberinfo=userinfo=aaa; SERVERID=yyyy|zzzz|xxxx" "/Users/yibo/Downloads/168"
    ```
 
> 如果要下载「我的-收藏照片」里的，下载源码然后把 `downloader.downloadFavWithoutWatermark();` 注释打开。
