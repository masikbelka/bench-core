package com.epam.bench.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import com.epam.bench.domain.enumeration.Gender;

/**
 * A Employee.
 */
@Entity
@Table(name = "employee")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "employee")
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "upsa_id", nullable = false)
    private String upsaId;

    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "full_name")
    private String fullName;

    @Size(max = 255)
    @Column(name = "jhi_comment", length = 255)
    private String comment;

    @Column(name = "manager_full_name")
    private String managerFullName;

    @Column(name = "manager_id")
    private String managerId;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "hire_date")
    private ZonedDateTime hireDate;

    @Column(name = "available_from")
    private ZonedDateTime availableFrom;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @OneToOne
    @JoinColumn(unique = true)
    private ProbationStatus probation;

    @OneToOne
    @JoinColumn(unique = true)
    private Location location;

    @OneToOne
    @JoinColumn(unique = true)
    private PrimarySkill primarySkill;

    @OneToOne
    @JoinColumn(unique = true)
    private Title title;

    @OneToOne
    @JoinColumn(unique = true)
    private LanguageLevel englishLevel;

    @OneToOne
    @JoinColumn(unique = true)
    private ProductionStatus productionStatus;

    @OneToOne
    @JoinColumn(unique = true)
    private JobFunction jobFunction;

    @OneToOne
    @JoinColumn(unique = true)
    private Unit unit;

    @OneToMany(mappedBy = "employee")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<BenchHistory> benchHistories = new HashSet<>();

    @OneToMany(mappedBy = "employee")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ProjectHistory> projectsWorkloads = new HashSet<>();

    @OneToMany(mappedBy = "employee")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<OpportunityPosition> opportunityPositions = new HashSet<>();

    @OneToMany(mappedBy = "employee")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<BenchPredictions> predictions = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUpsaId() {
        return upsaId;
    }

    public Employee upsaId(String upsaId) {
        this.upsaId = upsaId;
        return this;
    }

    public void setUpsaId(String upsaId) {
        this.upsaId = upsaId;
    }

    public String getEmail() {
        return email;
    }

    public Employee email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public Employee fullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getComment() {
        return comment;
    }

    public Employee comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getManagerFullName() {
        return managerFullName;
    }

    public Employee managerFullName(String managerFullName) {
        this.managerFullName = managerFullName;
        return this;
    }

    public void setManagerFullName(String managerFullName) {
        this.managerFullName = managerFullName;
    }

    public String getManagerId() {
        return managerId;
    }

    public Employee managerId(String managerId) {
        this.managerId = managerId;
        return this;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public Boolean isActive() {
        return active;
    }

    public Employee active(Boolean active) {
        this.active = active;
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public ZonedDateTime getHireDate() {
        return hireDate;
    }

    public Employee hireDate(ZonedDateTime hireDate) {
        this.hireDate = hireDate;
        return this;
    }

    public void setHireDate(ZonedDateTime hireDate) {
        this.hireDate = hireDate;
    }

    public ZonedDateTime getAvailableFrom() {
        return availableFrom;
    }

    public Employee availableFrom(ZonedDateTime availableFrom) {
        this.availableFrom = availableFrom;
        return this;
    }

    public void setAvailableFrom(ZonedDateTime availableFrom) {
        this.availableFrom = availableFrom;
    }

    public Gender getGender() {
        return gender;
    }

    public Employee gender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public ProbationStatus getProbation() {
        return probation;
    }

    public Employee probation(ProbationStatus probationStatus) {
        this.probation = probationStatus;
        return this;
    }

    public void setProbation(ProbationStatus probationStatus) {
        this.probation = probationStatus;
    }

    public Location getLocation() {
        return location;
    }

    public Employee location(Location location) {
        this.location = location;
        return this;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public PrimarySkill getPrimarySkill() {
        return primarySkill;
    }

    public Employee primarySkill(PrimarySkill primarySkill) {
        this.primarySkill = primarySkill;
        return this;
    }

    public void setPrimarySkill(PrimarySkill primarySkill) {
        this.primarySkill = primarySkill;
    }

    public Title getTitle() {
        return title;
    }

    public Employee title(Title title) {
        this.title = title;
        return this;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public LanguageLevel getEnglishLevel() {
        return englishLevel;
    }

    public Employee englishLevel(LanguageLevel languageLevel) {
        this.englishLevel = languageLevel;
        return this;
    }

    public void setEnglishLevel(LanguageLevel languageLevel) {
        this.englishLevel = languageLevel;
    }

    public ProductionStatus getProductionStatus() {
        return productionStatus;
    }

    public Employee productionStatus(ProductionStatus productionStatus) {
        this.productionStatus = productionStatus;
        return this;
    }

    public void setProductionStatus(ProductionStatus productionStatus) {
        this.productionStatus = productionStatus;
    }

    public JobFunction getJobFunction() {
        return jobFunction;
    }

    public Employee jobFunction(JobFunction jobFunction) {
        this.jobFunction = jobFunction;
        return this;
    }

    public void setJobFunction(JobFunction jobFunction) {
        this.jobFunction = jobFunction;
    }

    public Unit getUnit() {
        return unit;
    }

    public Employee unit(Unit unit) {
        this.unit = unit;
        return this;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Set<BenchHistory> getBenchHistories() {
        return benchHistories;
    }

    public Employee benchHistories(Set<BenchHistory> benchHistories) {
        this.benchHistories = benchHistories;
        return this;
    }

    public Employee addBenchHistory(BenchHistory benchHistory) {
        this.benchHistories.add(benchHistory);
        benchHistory.setEmployee(this);
        return this;
    }

    public Employee removeBenchHistory(BenchHistory benchHistory) {
        this.benchHistories.remove(benchHistory);
        benchHistory.setEmployee(null);
        return this;
    }

    public void setBenchHistories(Set<BenchHistory> benchHistories) {
        this.benchHistories = benchHistories;
    }

    public Set<ProjectHistory> getProjectsWorkloads() {
        return projectsWorkloads;
    }

    public Employee projectsWorkloads(Set<ProjectHistory> projectHistories) {
        this.projectsWorkloads = projectHistories;
        return this;
    }

    public Employee addProjectsWorkload(ProjectHistory projectHistory) {
        this.projectsWorkloads.add(projectHistory);
        projectHistory.setEmployee(this);
        return this;
    }

    public Employee removeProjectsWorkload(ProjectHistory projectHistory) {
        this.projectsWorkloads.remove(projectHistory);
        projectHistory.setEmployee(null);
        return this;
    }

    public void setProjectsWorkloads(Set<ProjectHistory> projectHistories) {
        this.projectsWorkloads = projectHistories;
    }

    public Set<OpportunityPosition> getOpportunityPositions() {
        return opportunityPositions;
    }

    public Employee opportunityPositions(Set<OpportunityPosition> opportunityPositions) {
        this.opportunityPositions = opportunityPositions;
        return this;
    }

    public Employee addOpportunityPosition(OpportunityPosition opportunityPosition) {
        this.opportunityPositions.add(opportunityPosition);
        opportunityPosition.setEmployee(this);
        return this;
    }

    public Employee removeOpportunityPosition(OpportunityPosition opportunityPosition) {
        this.opportunityPositions.remove(opportunityPosition);
        opportunityPosition.setEmployee(null);
        return this;
    }

    public void setOpportunityPositions(Set<OpportunityPosition> opportunityPositions) {
        this.opportunityPositions = opportunityPositions;
    }

    public Set<BenchPredictions> getPredictions() {
        return predictions;
    }

    public Employee predictions(Set<BenchPredictions> benchPredictions) {
        this.predictions = benchPredictions;
        return this;
    }

    public Employee addPredictions(BenchPredictions benchPredictions) {
        this.predictions.add(benchPredictions);
        benchPredictions.setEmployee(this);
        return this;
    }

    public Employee removePredictions(BenchPredictions benchPredictions) {
        this.predictions.remove(benchPredictions);
        benchPredictions.setEmployee(null);
        return this;
    }

    public void setPredictions(Set<BenchPredictions> benchPredictions) {
        this.predictions = benchPredictions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Employee employee = (Employee) o;
        if (employee.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), employee.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Employee{" +
            "id=" + getId() +
            ", upsaId='" + getUpsaId() + "'" +
            ", email='" + getEmail() + "'" +
            ", fullName='" + getFullName() + "'" +
            ", comment='" + getComment() + "'" +
            ", managerFullName='" + getManagerFullName() + "'" +
            ", managerId='" + getManagerId() + "'" +
            ", active='" + isActive() + "'" +
            ", hireDate='" + getHireDate() + "'" +
            ", availableFrom='" + getAvailableFrom() + "'" +
            ", gender='" + getGender() + "'" +
            "}";
    }
}
