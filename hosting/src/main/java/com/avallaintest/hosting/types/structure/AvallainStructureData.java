package com.avallaintest.hosting.types.structure;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AvallainStructureData {
    private String id;
    private String type;
    private AvallainStructureAttributes attributes;

}
