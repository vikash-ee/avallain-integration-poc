package com.avallaintest.hosting.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@Table(name = "modules")
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String moduleId;

    private String label;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    private Module parent;

    @OneToOne(mappedBy = "rootModule")
    @JsonIgnore
    @ToString.Exclude
    private Course course;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private Set<Module> childModules = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE,
            CascadeType.PERSIST })
    @JoinTable(name = "module_resources", joinColumns = { @JoinColumn(name = "module_id") }, inverseJoinColumns = {
            @JoinColumn(name = "resource_id") })
    private Set<Resource> resources = new HashSet<>();

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        Module module = (Module) obj;
        return (module.label.equals(this.label) && module.moduleId.equals(this.moduleId));
    }

    @Override
    public int hashCode() {
        return this.moduleId.hashCode();
    }
}
