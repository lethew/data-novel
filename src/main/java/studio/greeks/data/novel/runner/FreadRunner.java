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
        Document firstDocument = Request.get(url);
        String text = firstDocument.selectFirst(".totalPage").text();
        int maxPage = Integer.parseInt(text.substring(1, text.length() - 1));

        ExecutorService service = Executors.newFixedThreadPool(20);
        for (int i = 1; i <= maxPage; i++) {
            final int page = i;
            service.submit(() -> {
                String pageUrl = String.format("http://www.fread.com/store/0_0_now_total_2_%d", page);
                Document document = Request.get(pageUrl);
                if (null != document) {
                    Elements select = document.select("#book_list tbody tr");
                    for (Element element : select) {
                        String type = element.selectFirst(".col-type").text();
                        type = type.substring(1, type.length() - 1);
                        String name = element.selectFirst(".col-name a").attr("title");
                        String absUrl = element.selectFirst(".col-name a").absUrl("href");
                        String author = element.selectFirst(".col-author").text();
                        String status = element.selectFirst(".col-state").text();

                        Index index = new Index(new ObjectId(), type, name, absUrl, author, status, null, null, null, null, null);
                        boolean exists = mongoTemplate.exists(new Query(Criteria.where("abstractUrl").is(index.getAbstractUrl())), Index.class);
                        if (!exists) {
                            index = mongoTemplate.insert(index);
                            log.info(index.toString());
                        }
                    }
                }
            });
        }

        for (Index index : mongoTemplate.find(new Query(Criteria.where("summary").is(null)), Index.class)) {
            service.submit(() -> {
                String indexUrl = index.getAbstractUrl();

                Document document = index.getTags() == null ? Request.get(indexUrl) : null;
                if(null != document) {
                    Elements select = document.select(".keywords a");
                    if (null != select && !select.isEmpty()) {
                        String[] tags = new String[select.size()];
                        for (int i = 0; i < select.size(); i++) {
                            tags[i] = select.get(i).text();
                        }
                        index.setTags(tags);
                    }
                }
                document = Request.get(indexUrl + "toc/");
                if (null != document) {
                    Element author = document.selectFirst(".author .intro p");
                    if (null != author) {
                        index.setAuthorDesc(author.text());
                    }
                    Element descP = document.getElementById("desc_p");
                    if (null != descP) {
                        index.setSummary(descP.text());
                    }

                    Elements elements = document.select(".bookright dd");
                    if (null != elements && elements.size() == 4) {
                        try {
                            index.setTotalClick(Long.parseLong(elements.get(1).text()));
                            index.setTotalWord(Long.parseLong(elements.get(3).text()));
                        } catch (NumberFormatException nfe) {
                            log.error(nfe.getLocalizedMessage());
                        }
                    }
                    mongoTemplate.save(index);

                    elements = document.select(".chapter");
                    for (int i = 0; i < elements.size(); i++) {
                        Element a = elements.get(i).selectFirst("a");
                        String name = a.text();
                        String cUrl = a.absUrl("href");
                        Element free = elements.get(i).selectFirst(".free");
                        Chapter chapter = new Chapter(new ObjectId(), index.get_id(), i, name, cUrl, null, null, null, free != null);
                        boolean exists = mongoTemplate.exists(new Query(Criteria.where("url").is(chapter.getUrl())), Chapter.class);
                        if (!exists) {
                            chapter = mongoTemplate.insert(chapter);
                            log.info(chapter.toString());
                        }
                    }
                }
            });
        }



        service.shutdown();
    }
}
