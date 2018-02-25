# monkey
一个基于Matisse的选择Android设备本地图片以及视频的框架。<br>
<br>

在Matisse的基础上新增如功能：
- 支持拍照，录像
- 支持日期分组，可以按照拍摄时间进行分组，更加一目了然
- 返回媒体文件多种元数据。包括拍摄时间，文件大小，图片长度，图片宽度，视频时长
经纬度，本地路径，Uri，本地媒体库Id，mineType类型等数据
- 新增多种监听器，item被勾选监听器,获取最新一条文件监听器等
- 支持拍摄后刷新并停留在选择页面，并且勾选拍摄的媒体文件，可继续勾选
- 支持单选模式
- 支持自定义页面的Toolbar布局





## Download

Gradle:
```
repositories {
    jcenter()
}

dependencies {
    compile 'com.miraclehen:monkey:1.0.8@aar '
}
```

Maven:
```
<dependency>
  <groupId>com.miraclehen</groupId>
  <artifactId>monkey</artifactId>
  <version>1.0.8</version>
  <type>pom</type>
</dependency>
```

## Usage
### simple use

多选图片
```
Monkey.from(MainActivity.this)
                        .choose(MimeType.ofImageExcludeGif())
                        .countable(true)
                        .maxSelectable(20)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE);
 ```
<br>

 单选图片
 ```
 Monkey.from(MainActivity.this)
                         .choose(MimeType.ofImageExcludeGif())
                         .singleResultModel(true)
                         .imageEngine(new GlideEngine())
                         .forResult(REQUEST_CODE_CHOOSE);
 ```
 <br>

多选视频
 ```
  Monkey.from(MainActivity.this)
                         .choose(MimeType.ofVideo())
                         .countable(true)
                         .maxSelectable(20)
                         .imageEngine(new GlideEngine())
                         .forResult(REQUEST_CODE_CHOOSE);
 ```

 <br>

 单选视频
 ```
 Monkey.from(MainActivity.this)
                         .choose(MimeType.ofVideo())
                         .singleResultModel(true)
                         .imageEngine(new GlideEngine())
                         .forResult(REQUEST_CODE_CHOOSE);
 ```

### advance use
```
Monkey.from(MainActivity.this)
                        .choose(MimeType.ofImageExcludeGif())
                        .countable(false)
                        .spanCount(4)
                        .selectedMediaItem(dataList)
                        .captureFinishBack(false)
                        .captureType(CaptureType.Image)
                        .captureStrategy(new CaptureStrategy(true, "com.miraclehen.sample.fileprovider"))
                        .maxSelectable(20)
                        .toolbarLayoutId(R.layout.layout_custom_tool_bar)
                        .theme(R.style.ZhihuTheme1)
                        .groupByDate(true)
                        .inflateItemViewCallback(new InflateItemViewCallback() {
                            @Override
                            public void callback(MediaItem mediaItem, MediaGrid mediaGrid) {
                                ImageView imageView = new ImageView(MainActivity.this);
                                imageView.setImageResource(R.drawable.img_cloud);
                                mediaGrid.addView(imageView, new FrameLayout.LayoutParams(UIUtils.convertDIPToPixels(MainActivity.this, 25),
                                        UIUtils.convertDIPToPixels(MainActivity.this, 25), Gravity.START | Gravity.TOP));
                            }
                        })
                        .checkListener(new OnItemCheckChangeListener() {
                            @Override
                            public void onCheck(MediaItem mediaItem, boolean check) {
                                Log.i(TAG, mediaItem.toString() + "  \n" + check);
                            }
                        })
                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE);
```

## attribute
attribute | description
----|------
mimeTypeSet|显示的数据MimeType集合
themeId | 窗口主题，默认提供两种。R.style.Matisse_Zhihu 以及 R.style.Matisse_Dracula
orientation | 数据显示屏幕方向
countable | 是否支持勾选可数
maxSelectable | 最大可选中数量
filters | 选中过滤器。在勾选时候会触发此过滤器。假如你的application不想要超过5M的视频文件，你就可以使用此过滤器。



