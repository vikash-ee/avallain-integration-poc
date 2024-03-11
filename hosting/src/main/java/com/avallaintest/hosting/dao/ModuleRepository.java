package com.avallaintest.hosting.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.avallaintest.hosting.model.Module;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Integer> {
    public Module findByLabel(String label);
    public Module findByModuleId(String moduleId);
}
