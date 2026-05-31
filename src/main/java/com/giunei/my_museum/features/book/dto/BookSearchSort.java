package com.giunei.my_museum.features.book.dto;

public enum BookSearchSort {
    RELEVANCE("relevance", false),
    NEWEST("newest", false),
    POPULAR("relevance", true);

    private final String apiOrderBy;
    private final boolean localPopularitySort;

    BookSearchSort(String apiOrderBy, boolean localPopularitySort) {
        this.apiOrderBy = apiOrderBy;
        this.localPopularitySort = localPopularitySort;
    }

    public String apiOrderBy() {
        return apiOrderBy;
    }

    public boolean isLocalPopularitySort() {
        return localPopularitySort;
    }
}
