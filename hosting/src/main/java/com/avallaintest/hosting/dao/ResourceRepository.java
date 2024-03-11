package com.avallaintest.hosting.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.avallaintest.hosting.model.Resource;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Integer> {
    public Resource findByName(String name);
    public Resource findByResourceId(Integer resourceId);
}
