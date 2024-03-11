package com.avallaintest.hosting.types.structure;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AvallainStructureAttributeData {

    @JsonProperty("root-structure-node-id")
    private String rootStructureNodeId;

    private AvallainStructureAttributeEntity entities;
}
