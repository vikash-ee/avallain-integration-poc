package com.avallaintest.hosting.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.avallaintest.hosting.dao.CourseRepository;
import com.avallaintest.hosting.model.Course;
import com.avallaintest.hosting.service.CourseService;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("/sync")
    public String sync() {
        courseService.syncCourses();
        return "Done";
    }

    @GetMapping("")
    public List<Course> allCourses() {
        return courseRepository.findAll();
    }

}
