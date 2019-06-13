package studio.greeks.data.novel.crawler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import studio.greeks.data.novel.model.Chapter;
import studio.greeks.data.novel.model.Index;
import studio.greeks.data.novel.util.SpringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j()
public abstract class Crawler {
    static ExecutorService service = Executors.newFixedThreadPool(20);
    protected abstract void dealIndex(String startUrl);
    protected abstract void dealDetail(Index index);
    protected abstract void dealContent(Chapter chapter);
    private static Map<String, Class<? extends Crawler>> crawlerMap = new HashMap<>();
    static void putCrawler(String key, Class<? extends Crawler> clazz){
        crawlerMap.put(key, clazz);
    }
    private static Crawler getInstance(String url){
        for (Map.Entry<String, Class<? extends Crawler>> crawlerKey : crawlerMap.entrySet()) {
            if(url.contains(crawlerKey.getKey())){
               return SpringUtil.getBean(crawlerKey.getValue());
            }
        }
        return null;
    }
    public static void getIndex(String startUrl){
        Crawler instance = getInstance(startUrl);
        if(null != instance){
            instance.dealIndex(startUrl);
        }
    }
    public static void getDetail(Index index){
        Crawler instance = getInstance(index.getAbstractUrl());
        if(null != instance){
            instance.dealDetail(index);
        }
    }
    public static void getContent(Chapter chapter){
        Crawler instance = getInstance(chapter.getUrl());
        if(null != instance){
            instance.dealContent(chapter);
        }
    }
}
