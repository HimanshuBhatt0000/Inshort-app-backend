package com.example.economictimesscrapper.controller;

import com.example.economictimesscrapper.service.ScrapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    ScrapperService scrapperService;

    @GetMapping("/fetch-by-category")
    public String fetchByCategory(@RequestParam String tag) {
        return scrapperService.scrapeDataByTag(tag);
    }
    }

