package studio.greeks.data.novel.runner;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import studio.greeks.data.novel.crawler.Crawler;
import studio.greeks.data.novel.model.Chapter;
import studio.greeks.data.novel.model.Index;
import studio.greeks.data.novel.util.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="http://greeks.studio">吴昭</a>
 * @version data-novel@2019/5/19 21:16
 */
@Component
@Order(1)
@Slf4j
public class FreadRunner implements ApplicationRunner {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(ApplicationArguments args) {
        String url = "http://www.fread.com/store/";
        Crawler.getIndex(url);

        int count = 0;
        for (Index index : mongoTemplate.find(new Query(Criteria.where("summary").is(null)), Index.class)) {
            if(count<=10){
                Crawler.getDetail(index);
                count ++;
            } else break;
        }
    }
}
