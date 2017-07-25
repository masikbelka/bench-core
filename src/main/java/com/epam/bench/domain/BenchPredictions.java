package com.epam.bench.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A BenchPredictions.
 */
@Entity
@Table(name = "bench_predictions")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "benchpredictions")
public class BenchPredictions implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "created_time", nullable = false)
    private ZonedDateTime createdTime;

    @NotNull
    @Column(name = "ignored", nullable = false)
    private Boolean ignored;

    @Column(name = "ignored_days")
    private Boolean ignoredDays;

    @NotNull
    @Column(name = "ready_to_bench", nullable = false)
    private Boolean readyToBench;

    @NotNull
    @Column(name = "ready_to_production", nullable = false)
    private Boolean readyToProduction;

    @OneToOne
    @JoinColumn(unique = true)
    private PredictionDetails details;

    @ManyToOne
    private Employee employee;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getCreatedTime() {
        return createdTime;
    }

    public BenchPredictions createdTime(ZonedDateTime createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public void setCreatedTime(ZonedDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public Boolean isIgnored() {
        return ignored;
    }

    public BenchPredictions ignored(Boolean ignored) {
        this.ignored = ignored;
        return this;
    }

    public void setIgnored(Boolean ignored) {
        this.ignored = ignored;
    }

    public Boolean isIgnoredDays() {
        return ignoredDays;
    }

    public BenchPredictions ignoredDays(Boolean ignoredDays) {
        this.ignoredDays = ignoredDays;
        return this;
    }

    public void setIgnoredDays(Boolean ignoredDays) {
        this.ignoredDays = ignoredDays;
    }

    public Boolean isReadyToBench() {
        return readyToBench;
    }

    public BenchPredictions readyToBench(Boolean readyToBench) {
        this.readyToBench = readyToBench;
        return this;
    }

    public void setReadyToBench(Boolean readyToBench) {
        this.readyToBench = readyToBench;
    }

    public Boolean isReadyToProduction() {
        return readyToProduction;
    }

    public BenchPredictions readyToProduction(Boolean readyToProduction) {
        this.readyToProduction = readyToProduction;
        return this;
    }

    public void setReadyToProduction(Boolean readyToProduction) {
        this.readyToProduction = readyToProduction;
    }

    public PredictionDetails getDetails() {
        return details;
    }

    public BenchPredictions details(PredictionDetails predictionDetails) {
        this.details = predictionDetails;
        return this;
    }

    public void setDetails(PredictionDetails predictionDetails) {
        this.details = predictionDetails;
    }

    public Employee getEmployee() {
        return employee;
    }

    public BenchPredictions employee(Employee employee) {
        this.employee = employee;
        return this;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BenchPredictions benchPredictions = (BenchPredictions) o;
        if (benchPredictions.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), benchPredictions.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "BenchPredictions{" +
            "id=" + getId() +
            ", createdTime='" + getCreatedTime() + "'" +
            ", ignored='" + isIgnored() + "'" +
            ", ignoredDays='" + isIgnoredDays() + "'" +
            ", readyToBench='" + isReadyToBench() + "'" +
            ", readyToProduction='" + isReadyToProduction() + "'" +
            "}";
    }
}
