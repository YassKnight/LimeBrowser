# 青柠浏览器(LimeBrowser)：
---
## 简介
    简洁的Android浏览器，支持多窗口管理
---
## 使用场景
    内嵌与APP应用内，通过activity跳转实现APP内部浏览器
---
## 截图
![主界面](https://github.com/YassKnight/LimeBrowser/blob/main/homepage.png?raw=true)
![多窗口界面](https://github.com/YassKnight/LimeBrowser/blob/main/multiwindows.png?raw=true)
---
## 应用列表
   + 需要在应用内嵌入浏览器使用可以使用activity跳转：调用LimeBrowserActivity.start(context,Arraylist<WebApplicationBean>);
   + 其中Arraylist<WebApplicationBean>是主界面展示应用的列表数据
   
    ```java
    public class WebApplicationBean implements Parcelable {
        /**
         * webview应用需要加载的url
         */
        public String appLoadUrl;
        /**
         * app图标的路径
         */
        public String appIconUrl;
        /**
         * app的名称
         */
        public String appName;
        ...
        }
    ```