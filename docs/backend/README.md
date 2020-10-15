# 介绍

后端分local和cloud版，对应 `java/local` 和 `java/cloud` 目录，可以根据自行选择。

local版基于SpringBoot、SpringMVC、Mybatis-Plus开发。

cloud版基于SpringBoot、SpringCloud、SpringCloudAlibaba、SpringMVC、Mybatis-Plus开发
，服务间调用使用dubbo，未使用分布式事务。

目前两者在功能上的差别就是local版支持动态修改资源的限流设置，而cloud版由于还在研究sentinel中，
所以还未实现，暂时以gateway的 `default-filters` 作为替代。

具体文档不写啦，代码写得太垃圾了。。。
