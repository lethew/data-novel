package studio.greeks.data.novel.util;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * @author <a href="http://greeks.studio">吴昭</a>
 * @version data-novel@2019/5/19 21:50
 */
@Slf4j
public final class Request {

    public static int maxTryTimes = 10;

    public static Document get(String url){
        int count = 0;
        IOException ioe = null;
        while (count < maxTryTimes){
            try {
                return Jsoup.connect(url).get();
            } catch (IOException e) {
                count ++;
                ioe = e;
            }
        }
        log.error(null != ioe ? ioe.getLocalizedMessage()+":"+url : "tried times more than max times("+maxTryTimes+")");
        return null;
    }
}
