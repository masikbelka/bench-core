package com.epam.bench.domain;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A PredictionDetails.
 */
@Entity
@Table(name = "prediction_details")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "predictiondetails")
public class PredictionDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "jhi_date")
    private LocalDate date;

    @NotNull
    @Column(name = "assigned_to_project", nullable = false)
    private Boolean assignedToProject;

    /**
     * Employeed or not
     */
    @NotNull
    @ApiModelProperty(value = "Employeed or not", required = true)
    @Column(name = "active", nullable = false)
    private Boolean active;

    @NotNull
    @Column(name = "removed_from_project", nullable = false)
    private Boolean removedFromProject;

    @NotNull
    @Column(name = "maternity_leave", nullable = false)
    private Boolean maternityLeave;

    @OneToOne
    @JoinColumn(unique = true)
    private Project project;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public PredictionDetails date(LocalDate date) {
        this.date = date;
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Boolean isAssignedToProject() {
        return assignedToProject;
    }

    public PredictionDetails assignedToProject(Boolean assignedToProject) {
        this.assignedToProject = assignedToProject;
        return this;
    }

    public void setAssignedToProject(Boolean assignedToProject) {
        this.assignedToProject = assignedToProject;
    }

    public Boolean isActive() {
        return active;
    }

    public PredictionDetails active(Boolean active) {
        this.active = active;
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean isRemovedFromProject() {
        return removedFromProject;
    }

    public PredictionDetails removedFromProject(Boolean removedFromProject) {
        this.removedFromProject = removedFromProject;
        return this;
    }

    public void setRemovedFromProject(Boolean removedFromProject) {
        this.removedFromProject = removedFromProject;
    }

    public Boolean isMaternityLeave() {
        return maternityLeave;
    }

    public PredictionDetails maternityLeave(Boolean maternityLeave) {
        this.maternityLeave = maternityLeave;
        return this;
    }

    public void setMaternityLeave(Boolean maternityLeave) {
        this.maternityLeave = maternityLeave;
    }

    public Project getProject() {
        return project;
    }

    public PredictionDetails project(Project project) {
        this.project = project;
        return this;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PredictionDetails predictionDetails = (PredictionDetails) o;
        if (predictionDetails.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), predictionDetails.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PredictionDetails{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", assignedToProject='" + isAssignedToProject() + "'" +
            ", active='" + isActive() + "'" +
            ", removedFromProject='" + isRemovedFromProject() + "'" +
            ", maternityLeave='" + isMaternityLeave() + "'" +
            "}";
    }
}
