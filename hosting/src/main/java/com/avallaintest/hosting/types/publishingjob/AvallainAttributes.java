package com.avallaintest.hosting.types.publishingjob;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AvallainAttributes {
    private String status;
    private AvallainMetadata metadata;

    @JsonProperty("group-id")
    private Object groupId;

    @JsonProperty("design-package-id")
    private int designPackageId;

    @JsonProperty("created-at")
    private Date createdAt;

    @JsonProperty("processed-at")
    private Date processedAt;
    
    private boolean downloadable;

    @JsonProperty("download-url")
    private String downloadUrl;
}