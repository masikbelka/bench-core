package com.epam.bench.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A JobFunction.
 */
@Entity
@Table(name = "job_function")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "jobfunction")
public class JobFunction implements Serializable {

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

    @Column(name = "prefix")
    private String prefix;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUpsaId() {
        return upsaId;
    }

    public JobFunction upsaId(String upsaId) {
        this.upsaId = upsaId;
        return this;
    }

    public void setUpsaId(String upsaId) {
        this.upsaId = upsaId;
    }

    public String getName() {
        return name;
    }

    public JobFunction name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public JobFunction prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JobFunction jobFunction = (JobFunction) o;
        if (jobFunction.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), jobFunction.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "JobFunction{" +
            "id=" + getId() +
            ", upsaId='" + getUpsaId() + "'" +
            ", name='" + getName() + "'" +
            ", prefix='" + getPrefix() + "'" +
            "}";
    }
}
