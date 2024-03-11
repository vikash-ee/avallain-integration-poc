package com.avallaintest.hosting.types.structure;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AvallainStructureNode {

    private String id;

    @JsonProperty("parent-id")
    private String parentId;

    private String label;

    @JsonProperty("learning-object-ids")
    private ArrayList<Integer> learningObjectIds;

    @JsonProperty("children-ids")
    private ArrayList<String> childrenIds;
}
