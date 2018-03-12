![](file:///Users/hd/Documents/view.jpg)
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
- 支持滚动指定日期





## Download

Gradle:
```
repositories {
    jcenter()
}

dependencies {
    compile 'com.miraclehen:monkey:1.1.1@aar '
}
```

Maven:
```
<dependency>
  <groupId>com.miraclehen</groupId>
  <artifactId>monkey</artifactId>
  <version>1.1.1</version>
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

## add FileProvider
当拍照或者录像时候，monkey需要和相机程序进行数据交互。
在android7.0以上，应用之间数据传输无法直接通过 "file://" 进行数据交互。需要通过FileProvider进行交互：
在你的`AndroidManifest.xml`添加：
```
<provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="你的包名称.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths_public"></meta-data>
        </provider>
```
创建 在res文件下，创建xml文件夹，并且创建`file_paths_public.xml`文件，文件内容如下
```
<paths>
    <external-path
        name="my_images"
        path="Pictures"/>

    <external-path
        name="my_video"
        path="Movies"/>
</paths>
```


## attribute
attribute | description
----|------
theme|Activity的主题颜色，默认支持两种（R.style.Matisse_Zhihu,R.style.Dracula）
countable|是否支持数字选中模式
mimeTypeSet|显示的数据MimeType集合
themeId | 窗口主题，默认提供两种。R.style.Matisse_Zhihu 以及 R.style.Matisse_Dracula
orientation | 数据显示屏幕方向
countable | 是否支持勾选可数
maxSelectable | 最大可选中数量
addFilter | 选中过滤器。在勾选时候会触发此过滤器。假如你的application不想要超过5M的视频文件，你就可以使用此过滤器
autoScrollToDate | 滚动到指定日期位置，毫秒为单位
captureStrategy | 拍摄策略，提供一个FileProvider。在android7.0，应用间访问文件数据需要通过FileProvider
captureType | 拍摄类型。提供三个枚举类型，Image为提供拍照功能，Video为提供录像功能，None均无
captureFinishBack | 拍摄结束后是否结束MonkeyActivity直接返回拍摄数据
restrictOrientation | 拍摄手机方向限制。枚举值
spanCount | 每行显示多少item
thumbnailScale | 缩略图压缩值大小。0.0~1.0.建议不要低于0.8
imageEngine | 图片加载引擎。支持Glide和Picasso。强烈建议使用Glide，Picasso对Uri支持不是很友好
forResult | Activity返回结果码
groupByDate | 是否支持日期分组
selectedMediaItem | 从外部传入默认已勾选的MediaItem列表
checkListener | MediaItem被勾选或者反勾选监听器
singleResultModel | 是否启动单一结果模式。如果为true，那么当点击其中一个MediaItem,直接返回数据，适合在选择用户头像等操作。
toolbarLayoutId | 自定义Toolbar布局Id。注意！View需要设置特定的Id，参考sample
catchNewestCallback | 获取日期最新的一条数据MediaItem之后，回调相应的方法
inflateItemViewCallback | 生成item布局时候回调，可以对Item布局进行进一步自定义
autoScrollToDate | 第一次显示数据的时候，自动滚动到相应日期。如果没有匹配对应的值，那么会滚动到最近的日期值

## 一个人开发这个库不容易，可以请我喝杯咖啡吗？
![支付宝](file:///Users/hd/Documents/WechatIMG22.jpeg)
![微信](file:///Users/hd/Documents/WechatIMG23.jpeg)





