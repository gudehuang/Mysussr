# MySussr
一个方便使用脚本Sussr的软件。
***
## Sussr是什么
 介绍Sussr之前，需要先了解SShadowsocks和ShadowsocksR
### Shadowsocks
>Shadowsocks（中文名称：影梭）是使用Python、C++、C#以及Go等语言开发、基于Apache许可证的开放源代码软件，用于保护网络流量、加密数据传输以及突破中国网络审查。

### ShadowsocksR
>ShadowsocksR基于Shadowsocks衍生出来的第三方版本，兼容原版协议，比原版多了一些伪装功能（协议和混淆）。



Sussr则是由supppig基于ShadowsocksR开发的android脚本启动版

Sussr的目录结构：
![根目录](http://i4.buimg.com/567571/8d0d4449508f990f.png)

tools目录：
![toolsmul](http://i1.piimg.com/567571/b877e9f31be9d1eb.png)

使用Sussr需要root和安装busybox，把文件解压到/data/sussr下，配置权限777。      
作为使用者需要用到下表的4个文件  

文件名 | 作用
---|---
setting.ini |  配置文件 
start.sh   |  启动脚本
stop.sh    |  停止脚本
check.sh   |  查询脚本

而setting.ini文件的内容很多，修改十分不方便

![setting1](http://i4.buimg.com/567571/9692f1200d355330.png)
![settting2](http://i1.piimg.com/567571/2fc83f1113fab288.png)

所以就有了MySussr这个辅助使用Sussr脚本的软件。

****
## MySussr
 ### 软件功能
-  一键配置/卸载Sussr环境
-  图形化编辑配置文件setting.ini
-  多配置文件共存，执行脚本时动态写入配置文件
-  ip查询
-  软件UID查看
-  命令封装，按下对应按钮即可执行相应操作
-  自助更新

