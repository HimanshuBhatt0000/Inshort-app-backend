package com.example.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document(collection = "Article")
public class Article {

    @Id
    private String id;

    private String imageUrl;

    private String title;

    private String summary;

    private String tag;

    // getters and setters
}