# LimeBrowser(青柠浏览器)：

#### Simple Android browser, quickly deployed in your application, the implementation of the application internal browser, support for multi-window management.

[中文文档](https://github.com/YassKnight/LimeBrowser/blob/main/resources/README_zh.md)

#### Layout 

* Top title bar: Customizable icon icon, title content, and title bar background
* Intermediate content area: Customize the style of the home page you want, using setContentLayout
* Bottom function bar: Default display forward, back, home page, multiple Windows, exit buttons, you can use XML or setXXXVisibility Settings to display the user's buttons.The bottom button implements the listening callback, which can set the event logic you need


## Screenshot

  ![HomePage](https://github.com/YassKnight/LimeBrowser/blob/main/resources/homepage.png)
  ![Window management interface](https://github.com/YassKnight/LimeBrowser/blob/main/resources/multiwindows.png)

---

## Quick to use

Gradle

```
dependencies{
    //e.g. 'com.snxun:browser:1.0.5'
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

* If you need to customize WebSettings, WebViewClient, and WebChromeClient, use the setWebViewFactory method, and parameter you need to implement WebViewFactory interface

## Issues
* If your partner finds a BUG or has any suggestions,Welcome to [Github Issues](https://github.com/YassKnight/LimeBrowser/issues) Ask your questions

## License

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

