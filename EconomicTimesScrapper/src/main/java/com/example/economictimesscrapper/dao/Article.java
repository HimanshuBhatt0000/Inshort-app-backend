package com.example.economictimesscrapper.dao;

import com.example.economictimesscrapper.constants.Tag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Entity
@Getter
@Setter
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl ;

    private String title ;

    @Column(columnDefinition = "LONGTEXT")
    private String summary;

    private String source;

    @Enumerated(EnumType.STRING)
    private Tag tag;
}
