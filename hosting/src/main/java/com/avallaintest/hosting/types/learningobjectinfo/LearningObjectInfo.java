package com.avallaintest.hosting.types.learningobjectinfo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties
public class LearningObjectInfo {
    @JsonProperty("UID")
    private String UID;

    private String uuid;

    @JsonProperty("ID")
    private int ID;

    private String name;
    private LearningObjectInfoMetadata metadata;
}
