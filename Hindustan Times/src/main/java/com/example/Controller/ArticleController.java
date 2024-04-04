package com.example.Controller;

import com.example.Service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping("/scrape")
    public String scrapeArticles() {
        articleService.scrapeAndSaveArticles();
        return "Scraping completed";
    }
}

