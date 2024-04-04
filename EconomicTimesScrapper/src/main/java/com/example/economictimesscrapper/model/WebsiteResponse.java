package com.example.economictimesscrapper.model;

import com.example.economictimesscrapper.constants.Tag;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WebsiteResponse {
    private String imageUrl ;

    private String title ;

    private String summary;

    private String source;

    private String websiteTag;
}
