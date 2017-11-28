### 为支持同时爬取不同站点页面，对WebMagic如下几个类做了修改

#### Task 
1. 现改名为LifeCycle 消除与 `me.shenchao.webhunger.entity.Task` 歧义
2. getSite() 方法更改为 `getSites()` 记录正在爬取的所有站点集合
