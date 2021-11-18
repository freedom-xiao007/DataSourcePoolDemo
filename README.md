# 数据库连接池Demo实现
***
## 1.单线程下的简单实现
注：完整代码可切换到 Tag V0.0.1 进行查看

实现思路文章：[数据库连接池Demo（1）单线程初步](https://juejin.cn/post/7030807425905066014/)

测试运行入口：src/main/java/SingleThreadTest.java

注意：不要一起运行测试，感觉有缓存，导致在后面运行的查询速度很快，需要运行那个单独放开，其他注释掉

测试结果：

```text
ID: 1, name: 0;ID: 2, name: 1;ID: 3, name: 2;ID: 4, name: 3;ID: 5, name: 4;ID: 6, name: 5;ID: 7, name: 6;ID: 8, name: 7;ID: 9, name: 8;ID: 10, name: 9;
ID: 1, name: 0;ID: 2, name: 1;ID: 3, name: 2;ID: 4, name: 3;ID: 5, name: 4;ID: 6, name: 5;ID: 7, name: 6;ID: 8, name: 7;ID: 9, name: 8;ID: 10, name: 9;
ID: 1, name: 0;ID: 2, name: 1;ID: 3, name: 2;ID: 4, name: 3;ID: 5, name: 4;ID: 6, name: 5;ID: 7, name: 6;ID: 8, name: 7;ID: 9, name: 8;ID: 10, name: 9;
ID: 1, name: 0;ID: 2, name: 1;ID: 3, name: 2;ID: 4, name: 3;ID: 5, name: 4;ID: 6, name: 5;ID: 7, name: 6;ID: 8, name: 7;ID: 9, name: 8;ID: 10, name: 9;
ID: 1, name: 0;ID: 2, name: 1;ID: 3, name: 2;ID: 4, name: 3;ID: 5, name: 4;ID: 6, name: 5;ID: 7, name: 6;ID: 8, name: 7;ID: 9, name: 8;ID: 10, name: 9;
原生查询耗时：1715 毫秒

ID: 1, name: 0;ID: 2, name: 1;ID: 3, name: 2;ID: 4, name: 3;ID: 5, name: 4;ID: 6, name: 5;ID: 7, name: 6;ID: 8, name: 7;ID: 9, name: 8;ID: 10, name: 9;
ID: 1, name: 0;ID: 2, name: 1;ID: 3, name: 2;ID: 4, name: 3;ID: 5, name: 4;ID: 6, name: 5;ID: 7, name: 6;ID: 8, name: 7;ID: 9, name: 8;ID: 10, name: 9;
ID: 1, name: 0;ID: 2, name: 1;ID: 3, name: 2;ID: 4, name: 3;ID: 5, name: 4;ID: 6, name: 5;ID: 7, name: 6;ID: 8, name: 7;ID: 9, name: 8;ID: 10, name: 9;
ID: 1, name: 0;ID: 2, name: 1;ID: 3, name: 2;ID: 4, name: 3;ID: 5, name: 4;ID: 6, name: 5;ID: 7, name: 6;ID: 8, name: 7;ID: 9, name: 8;ID: 10, name: 9;
ID: 1, name: 0;ID: 2, name: 1;ID: 3, name: 2;ID: 4, name: 3;ID: 5, name: 4;ID: 6, name: 5;ID: 7, name: 6;ID: 8, name: 7;ID: 9, name: 8;ID: 10, name: 9;
原生Jdbc单连接查询耗时：770 毫秒

ID: 1, name: 0;ID: 2, name: 1;ID: 3, name: 2;ID: 4, name: 3;ID: 5, name: 4;ID: 6, name: 5;ID: 7, name: 6;ID: 8, name: 7;ID: 9, name: 8;ID: 10, name: 9;
ID: 1, name: 0;ID: 2, name: 1;ID: 3, name: 2;ID: 4, name: 3;ID: 5, name: 4;ID: 6, name: 5;ID: 7, name: 6;ID: 8, name: 7;ID: 9, name: 8;ID: 10, name: 9;
ID: 1, name: 0;ID: 2, name: 1;ID: 3, name: 2;ID: 4, name: 3;ID: 5, name: 4;ID: 6, name: 5;ID: 7, name: 6;ID: 8, name: 7;ID: 9, name: 8;ID: 10, name: 9;
ID: 1, name: 0;ID: 2, name: 1;ID: 3, name: 2;ID: 4, name: 3;ID: 5, name: 4;ID: 6, name: 5;ID: 7, name: 6;ID: 8, name: 7;ID: 9, name: 8;ID: 10, name: 9;
ID: 1, name: 0;ID: 2, name: 1;ID: 3, name: 2;ID: 4, name: 3;ID: 5, name: 4;ID: 6, name: 5;ID: 7, name: 6;ID: 8, name: 7;ID: 9, name: 8;ID: 10, name: 9;
Druid连接池查询耗时：588 毫秒

生成新物理连接
初始化物理连接
ID: 1, name: 0;ID: 2, name: 1;ID: 3, name: 2;ID: 4, name: 3;ID: 5, name: 4;ID: 6, name: 5;ID: 7, name: 6;ID: 8, name: 7;ID: 9, name: 8;ID: 10, name: 9;
回收连接
ID: 1, name: 0;ID: 2, name: 1;ID: 3, name: 2;ID: 4, name: 3;ID: 5, name: 4;ID: 6, name: 5;ID: 7, name: 6;ID: 8, name: 7;ID: 9, name: 8;ID: 10, name: 9;
回收连接
ID: 1, name: 0;ID: 2, name: 1;ID: 3, name: 2;ID: 4, name: 3;ID: 5, name: 4;ID: 6, name: 5;ID: 7, name: 6;ID: 8, name: 7;ID: 9, name: 8;ID: 10, name: 9;
回收连接
ID: 1, name: 0;ID: 2, name: 1;ID: 3, name: 2;ID: 4, name: 3;ID: 5, name: 4;ID: 6, name: 5;ID: 7, name: 6;ID: 8, name: 7;ID: 9, name: 8;ID: 10, name: 9;
回收连接
ID: 1, name: 0;ID: 2, name: 1;ID: 3, name: 2;ID: 4, name: 3;ID: 5, name: 4;ID: 6, name: 5;ID: 7, name: 6;ID: 8, name: 7;ID: 9, name: 8;ID: 10, name: 9;
回收连接
自写连接池查询耗时：473 毫秒
```

## 2.多线程下的初步实现
注：完整代码可切换到 Tag V0.0.2 进行查看

实现参考文章：[Alibaba Druid 源码阅读（六）数据库连接使用流程初探](https://juejin.cn/post/7031409660552986632/)
实现思路文章：