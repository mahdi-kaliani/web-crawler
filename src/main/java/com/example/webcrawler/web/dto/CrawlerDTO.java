package com.example.webcrawler.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * crawler dto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrawlerDTO {
    /**
     * links that find
     */
    private String link;
    /**
     * title of links
     */
    private String title;
}
