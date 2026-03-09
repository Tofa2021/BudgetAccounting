package org.example.client.controller.filter;

import java.time.Instant;

public interface FilterListener {
    void onFiltersCleared();

    void onFiltersApplied(String type, String category, Instant dateFrom, Instant dateTo, Integer minAmount, Integer maxAmount);
}
