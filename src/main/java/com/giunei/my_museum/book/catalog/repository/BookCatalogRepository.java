package com.giunei.my_museum.book.catalog.repository;

import com.giunei.my_museum.book.catalog.entity.BookCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCatalogRepository extends JpaRepository<BookCatalog, Long> {
}
