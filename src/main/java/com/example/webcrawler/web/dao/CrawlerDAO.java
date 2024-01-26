package com.example.webcrawler.web.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * crawler table
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class CrawlerDAO {
    /**
     * id of entity
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    /**
     * links that find
     */
    @Column(columnDefinition = "TEXT")
    private String link;
    /**
     * title of links
     */
    private String title;
    /**
     * add create entity time
     */
    @CreationTimestamp
    private LocalDateTime createTime;
}
