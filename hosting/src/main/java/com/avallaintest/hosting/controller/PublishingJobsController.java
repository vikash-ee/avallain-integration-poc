package com.avallaintest.hosting.controller;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avallaintest.hosting.service.PublishingJobService;

@RestController
@RequestMapping("/publishingjobs")
public class PublishingJobsController {

    private static final Logger logger = LoggerFactory.getLogger(PublishingJobsController.class);

    @Autowired
    private PublishingJobService publishingJobService;

    @GetMapping("/sync")
    public String test(
            @RequestParam(value = "lastRun", required = false, defaultValue = "14.06.2023") @DateTimeFormat(pattern = "dd.MM.yyyy") Date lastRun) {
        logger.info("LastRun: {}", lastRun);
        publishingJobService.getPublishingJobs(lastRun);
        return "Done";
    }

}
