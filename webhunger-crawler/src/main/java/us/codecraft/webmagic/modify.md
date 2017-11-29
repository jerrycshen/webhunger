### 为支持同时爬取不同站点页面，对WebMagic如下几个类做了修改

#### Task 
1. 现改名为LifeCycle 消除与 `me.shenchao.webhunger.entity.Task` 歧义
2. getSite() 方法更改为 `getSites()` 记录正在爬取的所有站点集合

#### PageProcessor
1. 移除 `getSite` 方法

#### Spider
1. 增加 `SiteDominate` 字段，管理当前正在爬取的所有站点
2. 增加 `addSeed` 方法，表示爬取新站点

#### Request
1. 增加 站点ID 字段

#### Site
1. 新增 `host` 字段，封装站点详细信息
2. 修改所有get方法，改为从host中获取站点信息
