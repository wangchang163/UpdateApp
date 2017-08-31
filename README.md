# UpdateApp
Android 封装版本更新module，适配7.0系统

## 使用：

将version_update以module形式导入项目，在点击进行版本更新时，传入VersionModel即可

## 功能：
本库会自动进行是否更新判断，是否强制更新判断，权限检测，网络检测，7.0兼容安装
，支持通知栏进度显示，通知栏点击安装等功能。


### 调用：

    UpdateActivity.launch(MainActivity.this, model);



### VersionModel属性（可扩展）

|              属性名称             |          属性介绍            |
| ------------------------------- |:-----------------------------|
|              versionCode             |          版本号            |
|              versionName             |          版本名称            |
|              desc             |          更新介绍            |
|              url             |          下载链接            |
|              forced             |          是否强制更新（1为true）            |
## 效果：


### 首页

![image](https://github.com/wangchang163/UpdateApp/blob/master/image/icon.png)

### 强制更新

![image](https://github.com/wangchang163/UpdateApp/blob/master/image/icon1.png)

### 版本更新

![image](https://github.com/wangchang163/UpdateApp/blob/master/image/icon2.png)

### 网络检测

![image](https://github.com/wangchang163/UpdateApp/blob/master/image/icon3.png)

### 下载进度

![image](https://github.com/wangchang163/UpdateApp/blob/master/image/icon4.png)

### 通知栏进度

![image](https://github.com/wangchang163/UpdateApp/blob/master/image/icon5.png)

### 通知栏安装

![image](https://github.com/wangchang163/UpdateApp/blob/master/image/icon6.png)
