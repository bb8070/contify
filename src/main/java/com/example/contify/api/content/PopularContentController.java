package com.example.contify.api.content;

import com.example.contify.domain.content.dto.PopularContentsResponse;
import com.example.contify.domain.content.dto.PopularMetric;
import com.example.contify.domain.content.dto.PopularPeriod;
import com.example.contify.domain.content.service.PopularContentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contents")
public class PopularContentController {
    private final PopularContentsService popularContentService;

    /*
    * 인기 콘텐츠 조회 API
    * - period : DAY | WEEK | MONTH
    * - metric : VIEW | LIKE | BOOKMARK
    * - limit : Top N
    * - category : optional
    * */
    @GetMapping("/popular")
    public PopularContentsResponse popular(
            @RequestParam(defaultValue="WEEK") PopularPeriod period,
            @RequestParam(defaultValue = "VIEW") PopularMetric metric,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String category
    ){
        int safeLimit = Math.min(Math.max(limit, 1), 100);
        return popularContentService.getPopular(period, metric, limit, category);
    }

}
