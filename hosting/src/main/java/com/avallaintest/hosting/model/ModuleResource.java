package com.avallaintest.hosting.model;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "module_resources")
@Data
public class ModuleResource implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @MapsId("moduleId")
    @JoinColumn(name = "module_id")
    private Module module;

    @ManyToOne
    @MapsId("resourceId")
    @JoinColumn(name = "resource_id")
    private Resource resource;

}