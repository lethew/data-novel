package studio.greeks.data.novel.util;

import lombok.Data;
import org.jsoup.nodes.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author <a href="http://greeks.studio">吴昭</a>
 * @version data-novel@2019/5/26 21:31
 */
@Data
public abstract class GrabTarget {
    private String url;
    private MongoTemplate mongoTemplate;

    public GrabTarget(String url, MongoTemplate mongoTemplate) {
        this.url = url;
        this.mongoTemplate = mongoTemplate;
    }

    public abstract void doParse(Document document);
}
