package com.example.Service;

import com.example.Model.Article;
import com.example.Repository.ArticleRepository;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class ArticleService {
    private final Map<String, String> categoryMap = new HashMap<>();
    private final List<String> allowedCategories = Arrays.asList("Entertainment", "World", "Education", "Latest News", "Lifestyle"); // Add your allowed category names here


    @Autowired
    private ArticleRepository articleRepository;

    public void scrapeAndSaveArticles() {
        try {
            Document document = Jsoup.connect("https://www.hindustantimes.com/").get();
            Elements articles = document.getElementsByClass("dataLayerL1").select("a");

            for (Element articleElement : articles) {
                String categoryName = articleElement.text();
                String categoryUrl = articleElement.attr("href");
                if (allowedCategories.contains(categoryName)) {
                    categoryMap.put(categoryName, categoryUrl);
                }
            }

            for (Map.Entry<String, String> entry : categoryMap.entrySet()) {
                String categoryName = entry.getKey();
                String categoryUrl = entry.getValue();

                Document categoryDocument = Jsoup.connect(categoryUrl).get();
                Elements dataHolder = categoryDocument.getElementsByClass("listingPage").select("a");

                int innerLoop = 0;
                for (Element mainListingData : dataHolder) {
                    if (innerLoop >= 5) break;

                    String href2 = mainListingData.attr("href");
                    Document linkedDocument2 = Jsoup.connect("https://www.hindustantimes.com" + href2).get();

                    String title = linkedDocument2.getElementsByClass("hdg1").text();
                    String imageUrl = linkedDocument2.getElementsByClass("storyParagraphFigure")
                            .select("img")
                            .attr("src");
                    String summary = linkedDocument2.getElementsByClass("storyParagraphFigure").select("p").text();

                    if (title.isEmpty() || imageUrl.isEmpty() || summary.isEmpty()) {
                        continue;
                    }

                    Article article = new Article();
                    article.setTitle(title);
                    article.setImageUrl(imageUrl);
                    article.setSummary(summary);
                    article.setTag(categoryName);
                    articleRepository.save(article);
                    innerLoop++;


                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


