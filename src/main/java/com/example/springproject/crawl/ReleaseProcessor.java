package com.example.springproject.crawl;

import com.example.springproject.common.DateUtil;
import com.example.springproject.domain.Contributor;
//import com.example.springproject.domain.OpenIssue;
import com.example.springproject.domain.Release;
import org.springframework.data.web.JsonPath;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReleaseProcessor implements PageProcessor {
    @Override
    public void process(Page page) {
        String str = page.getHtml().regex("\\[.*\\]").toString();
        List<String> assets_url = new JsonPathSelector("$.*.assets_url").selectList(str);
        List<String> created_at = new JsonPathSelector("$.*.created_at").selectList(str);
        List<String> published_at = new JsonPathSelector("$.*.published_at").selectList(str);
        List<String> name = new JsonPathSelector("$.*.name").selectList(str);


        List<Release> list = new ArrayList<>();
        for (int i = 0; i < name.size(); i++) {
            Release release = null;
            try {
                release = new Release( assets_url.get(i) , DateUtil.StrConvertDate(created_at.get(i)
                ), DateUtil.StrConvertDate(published_at.get(i)), name.get(i));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            list.add(release);


        }
        //System.out.println(name);
        page.putField("releases", list);

    }

    private Site site = Site.me()
            .setCharset("UTF-8")
            .setTimeOut(100000)
            .setRetrySleepTime(1000)
            .setRetryTimes(3);

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new ReleaseProcessor())
                .addUrl("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/releases?page=1&per_page=100")
                .addPipeline(new ReleasePipeline())
                .addPipeline(new ConsolePipeline())
                .thread(10)
                .run();
    }
}
