package com.avallaintest.hosting.types.learningobjectinfo;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LearningObjectInfoMetadataLom {

    private List<LearningObjectInfoMetadataLomGroup> group;

}
