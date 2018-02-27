## Developing Now......

[Readme in Chinese](https://note.youdao.com/)

## Motivation

> Note: We call the crawler which crawl the whole site: A full-scale crawler.

Firstly, let’s summarize the various types of crawlers, their characteristics and the corresponding open source framework.

Crawler type | Crawl num | Forbidden risk | Open source framework
---|---|---|---
Search engine crawler | Unknown | Low | Nutch
Vertical crawler | Known | Moderate | WebMagic SpiderMan
Full-scale crawler | Unknown | High | None

- Page number to be crawled: If you can know in advance what content we want and how much data we need before we crawl it, then it is easy to determine the performance of the crawler; On the contrary, it would get difficult when we do not know that. So it is a big problem for a full-scale crawler which persist to crawl more pages.
- Forbidden risk: The search engine crawlers crawl the entire Internet, so it is easy to avoid frequent crawling of a site through choosing different site’s URL every time, thereby reducing the risk of being forbidden. For a full-scale crawler, the risk is high because it crawled too many pages in this site.
- Open source framework: In the field of search engine, there is a famous framework called Nutch, and there are more awesome open source frameworks for vertical crawlers, such as WebMagic, SpiderMan and so on. But I did not find any framework for full-scale crawler. Of course, the moderate deformation of the other framework can meet some needs, but the effect is not perfect.

In addition, the daily work of the laboratory need a full-scale crawler. During the work, some workmates who study for data mining always want my help to crawl more data, because the crawler system is not friendly to a novice. After learning about MapReduce in Hadoop, it reminds me that I can learn from its programming model, so I came up with the idea of WebHunger.

## What's WebHunger

> The name of WebHunger is a composition of words web and hunger, means to be hungry for web resources

WebHunger is an extensible, full-scale crawler framework that supports distributed crawling, aiming at getting users focused on parsing web page without concern for the crawling process. To fetch the result, the user only needs to submit the link to be crawled and the page-parsed Java Class to this framework. After the crawling is completed, the framework would promptly return the crawling result to the user. With this framework, the user can have no idea about distributed programming, neither the knowledge of working mechanism of the crawler, which is greatly getting the user easy to use.

In order to making the user easy to monitor, WebHunger provides a web console. Some screenshots as shown below.

![image](https://github.com/jerry-sc/webhunger/blob/master/doc/screenshot/hosts_control.png?raw=true)

In the page shown above, you can start crawling the site, pause, re-crawl and other operations.

![image](https://github.com/jerry-sc/webhunger/blob/master/doc/screenshot/arch.png?raw=true)

And here you can see the site crawled progress, page parsed progress and the wrong links which crawl failed.

## How WebHunger works

![image](https://github.com/jerry-sc/webhunger/blob/master/doc/screenshot/arch.png?raw=true)

> All of the components with a yellow background in the above figure can be independently deployed to run on any server; solid lines indicate local calls; dotted lines indicate remote calls.

WebHunger consists of the following major components:
1. Controller: Responsible for the management of the site
2. Crawler: According to the Controller specified URL scheduling strategy, fetch URL from Redis to crawl, and seed crawled results to message queue.
3. Page Consumer: Pull page message from message queue, and call the user-defined page parsed Java Class
4. ZooKeeper Cluster: Do site state storage, service discovery, distributed lock and other work
5. RocketMQ: Storage crawled page and page distribution
6. Redis Cluster: Responsible for storing the URLs to be crawled and filtering duplicate URLs
7. Web Console: A web-based monitor platform.
