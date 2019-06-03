package studio.greeks.data.novel.util;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author <a href="http://greeks.studio">吴昭</a>
 * @version data-novel@2019/5/26 23:19
 */
public class MyThreadPool {
    private LinkedBlockingQueue<GrabTarget> targets = new LinkedBlockingQueue<>();
    private boolean stop;
    public MyThreadPool(int len) {
        Thread[] threads = new Thread[len];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new WorkerThread(this));
            threads[i].start();
        }
        for (Thread worker : threads) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < len; i++) {
            Thread worker = new Thread(new WorkerThread(this));
            worker.start();
            try {
                worker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void submit(GrabTarget target){
        if(null != target) {
            targets.offer(target);
        }
    }
    public void stop() {
        this.stop = true;
    }

    private static class WorkerThread implements Runnable{
        private MyThreadPool pool = null;
        private WebClient webClient = null;
        private boolean stop;
        public WorkerThread(MyThreadPool pool){
            this.pool = pool;
            webClient = new WebClient();
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false); // 禁用css支持
            webClient.getOptions().setThrowExceptionOnScriptError(false); // js运行错误时，是否抛出异常
            webClient.getOptions().setTimeout(100000); // 设置连接超时时间 ，这里是10S。如果为0，则无限期等待
            webClient.getOptions().setDoNotTrackEnabled(false);
        }

        @Override
        public void run() {
            while (!stop) {
                if (!pool.targets.isEmpty() || !pool.stop) {
                    try {
                        GrabTarget target = pool.targets.poll();
                        if(null != target) {
                            HtmlPage htmlPage = webClient.getPage(target.getUrl());
                            Document parse = Jsoup.parse(htmlPage.asXml());
                            parse.setBaseUri(htmlPage.getBaseURI());
                            target.doParse(parse);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    stop = true;
                }
            }
        }
    }
}
