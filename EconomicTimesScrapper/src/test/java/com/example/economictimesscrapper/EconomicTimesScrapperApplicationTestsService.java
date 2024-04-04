package com.example.economictimesscrapper;

import com.example.economictimesscrapper.dao.Article;
import com.example.economictimesscrapper.model.WebsiteResponse;
import com.example.economictimesscrapper.service.ScrapperService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class EconomicTimesScrapperApplicationTestsService {

    @Test
    void contextLoads() {
    }

    @Test
    void testScrapeData() {

        Document mockDocument = Mockito.mock(Document.class);
        try {
            Mockito.when(Jsoup.connect("https://indianexpress.com/").get()).thenReturn(mockDocument);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<WebsiteResponse> websiteResponsesList = new ArrayList<>();

        ScrapperService scraper = Mockito.spy(new ScrapperService());
        Mockito.doReturn(websiteResponsesList)
                .when(scraper)
                .scrapeArticlesForCategory(Mockito.anyString(), Mockito.anyString());

        Mockito.doNothing().when(scraper).saveArticleList(Mockito.anyList());

        String result = scraper.scrapeData();

        assertEquals("DataSaved", result);
    }
}
