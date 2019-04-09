#### 官网下载protobuf

 https://github.com/protocolbuffers/protobuf/releases/tag/v3.6.1



![image.png](https://upload-images.jianshu.io/upload_images/15181329-e12fee1869d0a0e5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

例如我下载到:  `/Users/haoxiaoyong/protobuf36`

然后解压 protobuf36文件夹下的`protobuf-all-3.6.1.tar.gz`文件;

解压完之后有一个`protobuf-3.6.1`文件夹,进入这个文件夹下执行:

`sudo ./configure --prefix=/Users/haoxiaoyong/protobuf36`

之后执行:

#### 安装

`sudo make install`

在这里可能会稍慢些....等执行完成;

#### 设置环境变量

`sudo vim ~/.bash_profile`

在下面添加

`PATH=$PATH:/Users/haoxiaoyong/protobuf36/bin`

使用命令生效环境变量
`source .bash_profile`

#### 验证安装是否成功

`protoc --version`

显示： 
`libprotoc 3.6.1`