package com.avallaintest.hosting.types.structure;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AvallainStructureAttributeEntity {

    @JsonProperty("structure-nodes")
    private Map<String, AvallainStructureNode> structureNodes;

    @JsonProperty("learning-objects")
    private Map<String, AvallainStructureLO> learningObjects;
}
