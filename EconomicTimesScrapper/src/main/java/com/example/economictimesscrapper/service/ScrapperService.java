package com.example.economictimesscrapper.service;

import com.example.economictimesscrapper.constants.Tag;
import com.example.economictimesscrapper.dao.Article;
import com.example.economictimesscrapper.model.WebsiteResponse;
import com.example.economictimesscrapper.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ScrapperService {


    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    UtilServices utilServices;


    @Scheduled(fixedRate = 15000)
    public void scrapeDataPeriodically() {

        String result = scrapeData();
        log.info(result);
    }
    public String scrapeData() {

        try {
            log.info("Cron Job Hit :");

            String url = "https://indianexpress.com/";
            Document articleDocument = Jsoup.connect(url).get();


            List<Article> articleList = new ArrayList<>();
            List<WebsiteResponse> websiteResponsesList = new ArrayList<>();

            List<String> categories = new ArrayList<>();
            categories.add("Business");
            categories.add("Education");
            categories.add("Sports");
            categories.add("Politics");
            categories.add("Lifestyle");

            Elements elements = articleDocument.select("div.mainnav a");

            Map<String, String> oneStep = new HashMap<>();

            for (Element element : elements) {
                // Get the text content of the <a> element
                String text = element.text();
                // Check if the text content matches any category in the list
                if (categories.contains(text)) {
                    // If there's a match, retrieve the href attribute
                    String href = element.attr("href");
                    oneStep.put(text, href);
                }
            }
            // Scrape data from each category page
            for (Map.Entry<String, String> entry : oneStep.entrySet()) {
                String category = entry.getKey();
                String categoryUrl = entry.getValue();
                websiteResponsesList = scrapeArticlesForCategory(category, categoryUrl);
                articleList = getArticleFormat(websiteResponsesList);
                saveArticleList(articleList);
            }

            return "DataSaved";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception Occured";
        }
    }

    public void saveArticleList(List<Article> articleList) {
        try {
            System.out.println("To be saved " + articleList);

            articleRepository.saveAll(articleList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<WebsiteResponse> scrapeArticlesForCategory(String category, String categoryUrl) {
        try {
            Document categoryDocument = Jsoup.connect(categoryUrl).get();
            Elements articleElements = categoryDocument.select("div.articles");

            List<WebsiteResponse> articles = new ArrayList<>();

            int articlesCount = 0;
            for (Element articleElement : articleElements) {
                if (articlesCount >= 5) {
                    break; // Stop scraping if 5 articles have been fetched
                }

                // TODO :Identify and Handel null pointer exceptions

                Element titleElement = articleElement.selectFirst("h2.title a");
                String title = titleElement.text();
                String articleUrl = titleElement.attr("href");

                Element imageElement = articleElement.selectFirst("div.snaps img");
                String imageUrl = imageElement.attr("src");

                // Element summaryElement = articleElement.selectFirst("div.img-context p");
                // String summary = summaryElement.text();

                // TODO : Remove Redundant data
                if(!utilServices.ifExist(articleUrl)) {
                    WebsiteResponse websiteResponse = new WebsiteResponse();
                    websiteResponse.setTitle(title);
                    websiteResponse.setSource(articleUrl); // Store websiteResponse URL as source
                    websiteResponse.setImageUrl(imageUrl);

                    String summary = scrapeSummaryFromResource(articleUrl);

                    websiteResponse.setSummary(summary);
                    websiteResponse.setWebsiteTag(category);

                    articles.add(websiteResponse);

                    articlesCount++;
                }
            }

            for (WebsiteResponse article : articles) {
                System.out.println("Category: " + category);
                System.out.println("Title: " + article.getTitle());
                System.out.println("Source: " + article.getSource());
                System.out.println("Image URL: " + article.getImageUrl());
                System.out.println("Summary: " + article.getSummary());
                System.out.println();
            }
            return articles;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String scrapeSummaryFromResource(String articleUrl) {
        try {
            Document articleDocument = Jsoup.connect(articleUrl).get();

            Element contentDiv = articleDocument.selectFirst("div#pcl-full-content.story_details");
            if (contentDiv != null) {

                Elements pTags = contentDiv.select("p");
                StringBuilder summaryBuilder = new StringBuilder();

                for (Element pTag : pTags) {
                    summaryBuilder.append(pTag.text()).append(" ");
                }
                return summaryBuilder.toString().trim();
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Article> getArticleFormat(List<WebsiteResponse> websiteResponsesList) {
        try {
            List<Article> response = new ArrayList<>();

            for (WebsiteResponse websiteResponse : websiteResponsesList) {
                Article article = new Article();

                article.setTitle(websiteResponse.getTitle());
                article.setSource(websiteResponse.getSource());
                article.setImageUrl(websiteResponse.getImageUrl());
                article.setSummary(websiteResponse.getSummary());

                switch (websiteResponse.getWebsiteTag()) {
                    case "Business":
                        article.setTag(Tag.BUSINESS);
                        break;

                    case "Sports":
                        article.setTag(Tag.SPORTS);
                        break;
                    case "Politics":
                        article.setTag(Tag.POLITICS);
                        break;
                    case "Lifestyle":
                        article.setTag(Tag.LIFESTYLE);
                        break;
                    case "Education":
                        article.setTag(Tag.EDUCATION);
                        break;
                    default:
                        article.setTag(Tag.HOT);
                }

                response.add(article);
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String scrapeDataByTag(String tag) {
        try {
            log.info("Cron Job Hit : ");
            log.info("Tag : "+ tag);

            String searchTag = convertToSearchTag(tag);
            String url = "https://indianexpress.com/";
            Document articleDocument = Jsoup.connect(url).get();

            List<Article> articleList = new ArrayList<>();
            List<WebsiteResponse> websiteResponsesList = new ArrayList<>();

            List<String> categories = new ArrayList<>();
            categories.add(searchTag);

            Elements elements = articleDocument.select("div.mainnav a");

            Map<String, String> oneStep = new HashMap<>();

            for (Element element : elements) {
                // Get the text content of the <a> element
                String text = element.text();
                // Check if the text content matches any category in the list
                if (categories.contains(text)) {
                    // If there's a match, retrieve the href attribute
                    String href = element.attr("href");
                    oneStep.put(text, href);
                }
            }

            // Scrape data from each category page
            for (Map.Entry<String, String> entry : oneStep.entrySet()) {
                String category = entry.getKey();
                String categoryUrl = entry.getValue();
                websiteResponsesList = scrapeArticlesForCategory(category, categoryUrl);
                articleList = getArticleFormat(websiteResponsesList);
                saveArticleList(articleList);
            }

            return "DataSaved";
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception Occured";
        }
    }

    private String convertToSearchTag(String tag) {

        String searchTag ="";

        switch (tag) {
            case "BUSINESS":
                searchTag = "Business";
                break;

            case "SPORTS":
                searchTag = "Sports";
                break;
            case "POLITICS":
                searchTag = "Politics";
                break;
            case "LIFESTYLE":
                searchTag ="Lifestyle";
                break;
            case "EDUCATION":
                searchTag ="Education";
                break;
            default:
                searchTag ="Hot";
        }
        return searchTag;
    }

}
