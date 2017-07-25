package com.epam.bench.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A PrimarySkill.
 */
@Entity
@Table(name = "primary_skill")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "primaryskill")
public class PrimarySkill implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "upsa_id", nullable = false)
    private String upsaId;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    private SkillCategory skillCategory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUpsaId() {
        return upsaId;
    }

    public PrimarySkill upsaId(String upsaId) {
        this.upsaId = upsaId;
        return this;
    }

    public void setUpsaId(String upsaId) {
        this.upsaId = upsaId;
    }

    public String getName() {
        return name;
    }

    public PrimarySkill name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SkillCategory getSkillCategory() {
        return skillCategory;
    }

    public PrimarySkill skillCategory(SkillCategory skillCategory) {
        this.skillCategory = skillCategory;
        return this;
    }

    public void setSkillCategory(SkillCategory skillCategory) {
        this.skillCategory = skillCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PrimarySkill primarySkill = (PrimarySkill) o;
        if (primarySkill.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), primarySkill.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PrimarySkill{" +
            "id=" + getId() +
            ", upsaId='" + getUpsaId() + "'" +
            ", name='" + getName() + "'" +
            "}";
    }
}
