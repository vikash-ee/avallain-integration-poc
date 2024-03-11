package com.avallaintest.hosting.types.publishingjob;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AvallainPublisher {
    private AvallainData data;
    private AvallainLinks links;
}
