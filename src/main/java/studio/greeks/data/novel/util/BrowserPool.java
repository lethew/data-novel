package studio.greeks.data.novel.util;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="http://greeks.studio">吴昭</a>
 * @version data-novel@2019/5/26 21:21
 */
public class BrowserPool {
    private static final int DEFAULT_POOL_SIZE = 8;
    private int size = 0;
    private Queue<WebClient> webClients = new LinkedBlockingQueue<>();
    private Queue<GrabTarget> targets = new LinkedBlockingQueue<>();
    private AtomicLong atomicLong = new AtomicLong(0);
    private boolean stop;

    public BrowserPool() {
        this(DEFAULT_POOL_SIZE);
    }

    public BrowserPool(int size) {
        this.size = size;
        for (int i = 0; i < size; i++) {
            WebClient webClient = new WebClient();
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false); // 禁用css支持
            webClient.getOptions().setThrowExceptionOnScriptError(false); // js运行错误时，是否抛出异常
            webClient.getOptions().setTimeout(100000); // 设置连接超时时间 ，这里是10S。如果为0，则无限期等待
            webClient.getOptions().setDoNotTrackEnabled(false);
            webClients.add(webClient);
        }

        new Thread(()->{
            boolean close = false;
            while (!close) {
                Thread.currentThread().setName("Browser-"+atomicLong.addAndGet(1)/this.size);
                WebClient client = webClients.poll();
                if(null != client) {
                    new Thread(() -> {
                        try {
                            GrabTarget target = targets.poll();
                            if(null != target) {
                                HtmlPage htmlPage = client.getPage(target.getUrl());
                                Document parse = Jsoup.parse(htmlPage.asXml());
                                parse.setBaseUri(htmlPage.getBaseURI());
                                target.doParse(parse);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            webClients.offer(client);
                        }

                    }).start();
                }

                if(!stop && targets.isEmpty()){
                    close = true;
                }
            }
        }).start();
    }


    public void stop(){
        this.stop = true;
    }

    public void submit(GrabTarget target){
        if(null != target) {
            targets.offer(target);
        }
    }

}
