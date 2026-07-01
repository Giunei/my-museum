package com.giunei.my_museum.features.book.catalog.service;

import com.giunei.my_museum.features.book.catalog.entity.BookCatalog;
import com.giunei.my_museum.features.book.catalog.repository.BookCatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookCatalogService {

    private final BookCatalogRepository repository;

    @Cacheable("book-catalog")
    public List<BookCatalog> findAll() {
        return repository.findAll();
    }
}
