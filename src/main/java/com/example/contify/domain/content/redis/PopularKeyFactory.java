package com.example.contify.domain.content.redis;

import com.example.contify.domain.content.dto.PopularMetric;
import com.example.contify.domain.content.dto.PopularPeriod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;

public class PopularKeyFactory {
    private PopularKeyFactory(){}

    public static String rankingKey(PopularPeriod period , PopularMetric metric , LocalDateTime now){
        return "rank:"+metricName(metric)+":"+periodSuffix(period, now);
    }

    public static String deltaKey(PopularPeriod period , PopularMetric metric, LocalDateTime now){
        return "delta:"+metricName(metric)+":"+periodSuffix(period, now);
    }

    private static String metricName(PopularMetric metric){
        return switch(metric){
            case VIEW ->"view";
            case LIKE ->"like";
            case BOOKMARK -> "bookmark";
        };
    }

    private static String periodSuffix(PopularPeriod period, LocalDateTime now) {
        return switch (period){
            case DAY -> now.format(DateTimeFormatter.ISO_DATE);
            case WEEK ->{
                WeekFields wf = WeekFields.ISO;
                int week = now.get(wf.weekOfWeekBasedYear());
                int year = now.get(wf.weekBasedYear());
                yield String.format("%d-W%02d", year, week);
            }
            case MONTH -> now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        };
    }
}
