package com.avallaintest.hosting.model;

import java.util.HashSet;
import java.util.Set;

import com.avallaintest.hosting.types.ResourceType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@Table(name = "resources")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private Integer resourceId;

    private String name;

    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    private String resourceData;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST }, mappedBy = "resources")
    @JsonIgnore
    @ToString.Exclude
    private Set<Module> modules = new HashSet<>();

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        Resource resource = (Resource) obj;
        return (resource.name.equals(this.name) && resource.resourceId.equals(this.resourceId));
    }

    @Override
    public int hashCode() {
        return this.resourceId.hashCode();
    }
}
