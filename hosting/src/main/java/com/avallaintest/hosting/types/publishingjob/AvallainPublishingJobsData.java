package com.avallaintest.hosting.types.publishingjob;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AvallainPublishingJobsData {
    private String id;
    private String type;
    private AvallainAttributes attributes;
    private AvallainRelationships relationships;

    private String status;
    private String downloadFolder;
}