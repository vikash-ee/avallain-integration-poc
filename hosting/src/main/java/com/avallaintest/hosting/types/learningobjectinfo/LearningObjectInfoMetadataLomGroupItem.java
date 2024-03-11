package com.avallaintest.hosting.types.learningobjectinfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties
public class LearningObjectInfoMetadataLomGroupItem {
    private LearningObjectInfoMetadataLomGroupItemValue value;
    private String description;
    private int order;
    private String title;
}
