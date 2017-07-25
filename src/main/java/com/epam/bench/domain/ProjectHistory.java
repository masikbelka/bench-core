package com.epam.bench.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A ProjectHistory.
 */
@Entity
@Table(name = "project_history")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "projecthistory")
public class ProjectHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "jhi_date")
    private LocalDate date;

    @NotNull
    @Column(name = "workload", nullable = false)
    private Integer workload;

    @ManyToOne
    private Employee employee;

    @OneToOne
    @JoinColumn(unique = true)
    private ProjectRole role;

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

    public ProjectHistory date(LocalDate date) {
        this.date = date;
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getWorkload() {
        return workload;
    }

    public ProjectHistory workload(Integer workload) {
        this.workload = workload;
        return this;
    }

    public void setWorkload(Integer workload) {
        this.workload = workload;
    }

    public Employee getEmployee() {
        return employee;
    }

    public ProjectHistory employee(Employee employee) {
        this.employee = employee;
        return this;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public ProjectRole getRole() {
        return role;
    }

    public ProjectHistory role(ProjectRole projectRole) {
        this.role = projectRole;
        return this;
    }

    public void setRole(ProjectRole projectRole) {
        this.role = projectRole;
    }

    public Project getProject() {
        return project;
    }

    public ProjectHistory project(Project project) {
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
        ProjectHistory projectHistory = (ProjectHistory) o;
        if (projectHistory.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), projectHistory.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ProjectHistory{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", workload='" + getWorkload() + "'" +
            "}";
    }
}
