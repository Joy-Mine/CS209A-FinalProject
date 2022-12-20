package com.example.springproject.crawl;

import com.example.springproject.domain.Contributor;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ContributorProcessor implements PageProcessor {
    @Override
    public void process(Page page) {
        String str = page.getHtml().regex("\\[.*\\]").toString();
        System.out.println(str);
        List<String> id = new JsonPathSelector("$.*.id").selectList(str);

        List<String> login = new JsonPathSelector("$.*.login").selectList(str);
        List<String> contributions = new JsonPathSelector("$.*.contributions").selectList(str);
        List<String> avatar_url = new JsonPathSelector("$.*.avatar_url").selectList(str);
        List<String> html_url = new JsonPathSelector("$.*.html_url").selectList(str);

        List<Contributor> list = new ArrayList<>();
        for (int i = 0; i < login.size(); i++) {
            Contributor contributor = null;
            contributor = new Contributor(Long.parseLong(id.get(i)), login.get(i),
                    Long.parseLong(contributions.get(i)), avatar_url.get(i), html_url.get(i));
            list.add(contributor);
            System.out.println(contributor);

        }
        //System.out.println(name);
        page.putField("contributor", list);

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
        Spider.create(new ContributorProcessor())
                .addUrl("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/contributors?page=1&per_page=100")
                .addPipeline(new ContributorPipeline())
                .addPipeline(new ConsolePipeline())
                .thread(10)
                .run();
    }
}
