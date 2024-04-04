package com.example.economictimesscrapper.service;

import com.example.economictimesscrapper.dao.Article;
import com.example.economictimesscrapper.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UtilServices {
    @Autowired
    ArticleRepository articleRepository;

    public boolean ifExist(String articleUrl){
        try{
            List<Article> urlInDb = articleRepository.findBySource(articleUrl).orElse(new ArrayList<>());
            if(ObjectUtils.isEmpty(urlInDb)){
                return false;
            }
            else return true;
        }
        catch (Exception e){
            log.info("Exception in UtilService , ifExist");
            e.printStackTrace();
            return true;
        }
    }
}
