package com.example.economictimesscrapper.repository;

import com.example.economictimesscrapper.dao.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article,Long> {
    Optional<List<Article>> findBySource(String articleUrl);
}
