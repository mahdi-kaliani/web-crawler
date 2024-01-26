package com.example.webcrawler.web.repository;

import com.example.webcrawler.web.dao.CrawlerDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrawlerRepository extends JpaRepository<CrawlerDAO, Long> {

    List<CrawlerDAO> findAllByLink(String link);

    boolean existsByLink(String link);
}
