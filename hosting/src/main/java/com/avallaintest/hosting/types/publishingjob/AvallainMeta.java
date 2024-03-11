package com.avallaintest.hosting.types.publishingjob;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AvallainMeta {
    private int page;

    @JsonProperty("total-pages")
    private int totalPages;

    @JsonProperty("total-count")
    private int totalCount;
}
