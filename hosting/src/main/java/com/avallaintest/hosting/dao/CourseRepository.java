package com.avallaintest.hosting.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.avallaintest.hosting.model.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    public Course findByName(String name);
    public Course findByStructureId(String structureId);
}
