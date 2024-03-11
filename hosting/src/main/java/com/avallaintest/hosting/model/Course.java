package com.avallaintest.hosting.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String structureId;

    private String rootNodeId;

    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    private Module rootModule;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        Course course = (Course) obj;
        return (course.name.equals(this.name) && course.structureId.equals(this.structureId));
    }

    @Override
    public int hashCode() {
        return this.structureId.hashCode();
    }
}
