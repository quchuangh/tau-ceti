package com.chuang.urras.rowquery.filters;

import java.util.Optional;

/**
 * Created by ath on 2018/4/29.
 */
public enum FilterType {
    TEXT("text"),
    SET("set"),
    NUMBER("number"),
    DATE("date");

    private String type;
    FilterType(String type) {
        this.type = type;
    }

    public static Optional<FilterType> parseOf(String type) {
        FilterType[] values = FilterType.values();

        for (FilterType value : values) {
            if (value.type.equals(type)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
