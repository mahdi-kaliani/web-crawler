package com.example.webcrawler;

import com.example.webcrawler.web.service.CrawlerService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebCrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebCrawlerApplication.class, args);
        // make instance from crawler service
        CrawlerService crawlerService = CrawlerService.getCrawler();
        // crawl the site
        crawlerService.crawlWebSite();
    }

}
