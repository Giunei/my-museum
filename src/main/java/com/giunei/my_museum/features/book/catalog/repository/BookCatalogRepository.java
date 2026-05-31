package com.giunei.my_museum.features.book.catalog.repository;

import com.giunei.my_museum.features.book.catalog.entity.BookCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCatalogRepository extends JpaRepository<BookCatalog, Long> {
}
