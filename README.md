# 青柠浏览器(LimeBrowser)：

## 简介

简洁的Android浏览器，支持多窗口管理。
浏览器布局包含：

* 顶部标题栏：可自定义icon图标、标题内容及标题栏背景
* 中间内容区域：可自定义你想要的主页样式，通过setContentLayoutId（）
* 底部功能栏：默认显示主页、多窗口、退出按钮，可通过xml设置显示隐藏前进和后退按钮。底部按钮皆以实现监听回调，用户可以设置另外的点击事件

## demo样式

* 浏览器主页：
  ![主界面](https://github.com/YassKnight/LimeBrowser/blob/main/resources/homepage.png)

---

* 多窗口管理界面
  ![多窗口界面](https://github.com/YassKnight/LimeBrowser/blob/main/resources/multiwindows.png)

---

## 快速使用

buid.grade :  dependency

```
implementation 'com.snxun:browser:1.0.1'
```

xml

```
<com.snxun.browser.widget.browser.LimeBrowser
    android:id="@+id/browser"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:showGoForwardBtn="true"
    app:showGobackBtn="true"
    app:titleIcon="@drawable/ic_home"
    app:titleText="xxxxx" />
```

view

```
//设置自定义内容布局

limeBrowser.setContentLayoutId(R.layout.layout_custom_content);
```

```
//设置标题颜色
limeBrowser.setTitleBackgroud(R.color.themePink);
```

```
//绑定自定义内容布局的控件
mBtn = limeBrowser.getContentLayoutById(R.id.customLayout).findViewById(R.id.mybtn);
```


