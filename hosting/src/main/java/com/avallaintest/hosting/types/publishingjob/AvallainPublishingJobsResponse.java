package com.avallaintest.hosting.types.publishingjob;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AvallainPublishingJobsResponse {

    private List<AvallainPublishingJobsData> data;
    private AvallainMeta meta;
    
}

