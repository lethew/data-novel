package studio.greeks.data.novel;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import studio.greeks.data.novel.model.Index;
import studio.greeks.data.novel.util.Request;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

/**
 * @author <a href="http://greeks.studio">吴昭</a>
 * @version data-novel@2019/5/19 21:20
 */
public class FreadTest {
    public static void main(String[] args) throws IOException {
        WebClient webClient = new WebClient();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false); // 禁用css支持
        webClient.getOptions().setThrowExceptionOnScriptError(false); // js运行错误时，是否抛出异常
        webClient.getOptions().setTimeout(100000); // 设置连接超时时间 ，这里是10S。如果为0，则无限期等待
        webClient.getOptions().setDoNotTrackEnabled(false);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        HtmlPage page = webClient.getPage("http://www.fread.com/book/3161437/2/");


        List<Object> byXPath = page.getByXPath("//*[@id=\"partContent\"]/text()");
//        for (Object o : byXPath) {
//            System.out.println(o);
//        }
//        System.out.println();
        System.out.println(page.);
        webClient.waitForBackgroundJavaScript(10000);
//        URL baseURL = page.getBaseURL();
//        String s = page.asText();
//        System.out.println(s);
////        System.out.println(page.asXml());
//        Document parse = Jsoup.parse(s);
////        System.out.println(s);
//        parse.setBaseUri(baseURL.toString());
////        System.out.println(parse.text());
////        System.out.println(parse.selectFirst("#partContent").text());
//        Document document = Request.get("http://www.fread.com/book/3161437/2/");
//        if(document != null) {
//            Element select = document.selectFirst("#control > div.title > table > tbody > tr > td:nth-child(3)");
//            if (select != null && select.text().startsWith("本章字数：")) {
//                System.out.println(Integer.parseInt(select.text().substring(5)));
//            }
//            select = document.selectFirst("#control > div.title > table > tbody > tr > td:nth-child(4)");
//            if (select != null && select.text().startsWith("创建时间：")) {
//                try {
//                    System.out.println(dateFormat.parse(select.text().substring(5)).getTime());
//                } catch (ParseException e) {
//
//                }
//            }
////            Element partContent1 = document.getElementById("partContent");
//            Element partContent = document.selectFirst("#partContent");
////            partContent.getElementsByTag("br").remove();
//            System.out.println(partContent.text());
//
//            System.out.println(document.html());
//
//
//
////            System.out.println(document.text());
//        }
    }
}
