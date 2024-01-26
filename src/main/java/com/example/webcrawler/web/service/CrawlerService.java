package com.example.webcrawler.web.service;

import com.example.webcrawler.web.dao.CrawlerDAO;
import com.example.webcrawler.web.dto.CrawlerDTO;
import com.example.webcrawler.web.repository.CrawlerRepository;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CrawlerService {
    /**
     * ThreadPoolTaskExecutor: for run crawler in thread
     */
    private ThreadPoolTaskExecutor executor;
    /**
     * ApplicationContext: for create this service
     */
    private static ApplicationContext applicationContext;

    public static CrawlerService getCrawler() {
        return applicationContext.getBean(CrawlerService.class);
    }

    /**
     * url witch want to crawler work on it
     */
    @Value("${url}")
    private String url;

    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    /**
     * config ThreadPoolTaskExecutor
     *
     * @param executor : ThreadPoolTaskExecutor
     */
    @Autowired
    public CrawlerService(@Qualifier("taskExecutorConfig") ThreadPoolTaskExecutor executor) {
        this.executor = executor;
    }

    /**
     * CrawlerRepository: the crawler repository
     */
    @Autowired
    private CrawlerRepository crawlerRepository;

    /**
     * crawlWebSite: the main service for crawler
     */
    public void crawlWebSite() {
        /**
         * crawler run in new thread
         */
        executor.execute(
                () -> {
                    // id of entity's
                    long id = 1L;

                    // to get first entity from database
                    Optional<CrawlerDAO> firstDao = crawlerRepository.findById(id);
                    crawl(firstDao.get().getLink());
                    id++;

                    // number of entity's in database
                    long size = crawlerRepository.count();

                    // if id reach to size crawler will stop
                    while (size >= id) {
                        // get entity
                        Optional<CrawlerDAO> dao = crawlerRepository.findById(id);
                        log.warn("links: {}, crawled: {}", size, id);

                        if (dao.isPresent()) crawl(dao.get().getLink());
                        else log.error("method: {} , url doesn't exist", "crawlOtherWebSite");
                        id++;
                        size = crawlerRepository.count();
                    }
                }
        );
    }

    /**
     * start to crawl the page and get <a> tags then save it in database
     *
     * @param url: page url that want to crawl
     */
    @SneakyThrows
    private void crawl(String url) {
        // get page
        Document doc = Jsoup.connect(url).get();

        // get <a> tags
        Elements links = doc.select("a");

        // loop on links to analyse them
        links.stream().forEach(
                link -> {
                    // extract the href of <a> tags
                    String attr = link.attr("abs:href");
                    // extract name of links
                    String title = trim(link.text(), 35);

                    // create dto
                    CrawlerDTO dto = new CrawlerDTO(attr, title);
                    ModelMapper modelMapper = new ModelMapper();

                    // map it to dao
                    CrawlerDAO dao = modelMapper.map(dto, CrawlerDAO.class);

                    // find all entity that have same link in database
                    List<CrawlerDAO> allByLink = crawlerRepository.findAllByLink(dao.getLink());

                    // if allByLink list is empty it means we don't have duplicated link
                    // then we can add it database
                    if (allByLink.isEmpty()) {
                        // save dao in database
                        crawlerRepository.save(dao);
                        // log the urls and there's name
                        log.info("link: {} , title: {}", attr, title);
                    }
                }
        );
    }

    /**
     * this method add first url to database for crawl
     */
    @SneakyThrows
    @PostConstruct
    public void init() {
        // make its dao
        CrawlerDAO crawlerDAO = CrawlerDAO.builder()
                .link(this.url)
                .title(this.url)
                .build();
        // add dao to database
        crawlerRepository.save(crawlerDAO);
    }

    /**
     * this method make long string to short
     * it's for make short names of links
     *
     * @param string: the string that we want to make it short
     * @param width:  how much you want to resize the string
     * @return resized string
     */
    private String trim(String string, int width) {
        if (string.length() > width)
            return string.substring(0, width - 1) + ".";
        else
            return string;
    }

}
