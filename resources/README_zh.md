# LimeBrowser(青柠浏览器)：

#### 简单的Android浏览器，快速部署在您的应用程序中，实现应用程序内部浏览器，支持多窗口管理。

#### 布局 

* 顶部标题栏:自定义图标，标题内容和标题栏背景
* 中间内容区:使用setContentLayout自定义你需要的主页样式
* 底部功能栏:默认显示前进、后退、主页、多个窗口、退出按钮，可以使用XML或setXXXVisibility设置需要显示的按钮。底部按钮实现监听回调，可以设置您需要的事件逻辑

## 样式截图

  ![HomePage](https://github.com/YassKnight/LimeBrowser/blob/main/resources/homepage.png)
  ![Window management interface](https://github.com/YassKnight/LimeBrowser/blob/main/resources/multiwindows.png)

---

## 快速使用

Gradle

```
dependencies{
    //e.g. 'com.snxun:browser:1.0.3'
  implementation 'com.snxun:browser:${LATEST_VERSION}'
}
```

```
<com.snxun.browser.widget.browser.LimeBrowser
        android:id="@+id/browser"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:titleIcon="@drawable/xxx"
        app:titleText="xxxxx" />
```

```
limeBrowser.setWebViewFactory(new TestWebViewFactory());
limeBrowser.setContentLayout(R.layout.layout_custom_content);
mBtn = (Button) limeBrowser.findContentLayoutChildViewById(R.id.mybtn);
```

* 如果你需要自定义WebSettings, WebViewClient，和WebChromeClient，使用setWebViewFactory方法，参数需要实现WebViewFactory接口

## 问题
* 如果你发现了BUG或有任何建议，欢迎到 [Github Issues](https://github.com/YassKnight/LimeBrowser/issues) 提出你的问题或者建议

## 许可证

```
Copyright 2020 [yknight]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

