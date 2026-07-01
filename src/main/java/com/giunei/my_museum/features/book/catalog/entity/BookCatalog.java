package com.giunei.my_museum.features.book.catalog.entity;

import com.giunei.my_museum.core.EntityAbstract;
import com.giunei.my_museum.features.recommendation.entity.EditorialCategory;
import com.giunei.my_museum.features.recommendation.model.RecommendationCatalogItem;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class BookCatalog extends EntityAbstract implements RecommendationCatalogItem {

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String author;

	@Column(length = 20)
	private String language;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EditorialCategory editorialCategory;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "book_catalog_genre", joinColumns = @JoinColumn(name = "book_catalog_id"))
	@Column(name = "genre", nullable = false)
	@Builder.Default
	private Set<String> genres = new LinkedHashSet<>();

	@Override
	public String getCreator() {
		return author;
	}
}




