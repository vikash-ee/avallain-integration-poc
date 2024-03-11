package com.avallaintest.hosting.types.publishingjob;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AvallainRelationships {
    private AvallainPublisher publisher;

    @JsonProperty("publishing-type")
    private AvallainPublishingType publishingType;

    @JsonProperty("learning-object")
    private AvallainLearningObject learningObject;
}