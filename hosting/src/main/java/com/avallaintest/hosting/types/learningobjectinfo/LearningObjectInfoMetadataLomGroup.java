package com.avallaintest.hosting.types.learningobjectinfo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LearningObjectInfoMetadataLomGroup {
    private String name;
    private int order;
    private LearningObjectInfoMetadataLomGroupItem item;
}
