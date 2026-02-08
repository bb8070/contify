package com.example.contify.domain.content.dto;

import java.util.List;

public record PopularContentsResponse(
        PopularPeriod period,
        PopularMetric metric,
        List<Item> items
) {
    public record Item(
            Long id,
            String title,
            String category,
            long score
    ){

    }

}
