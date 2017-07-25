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
 * A BenchHistory.
 */
@Entity
@Table(name = "bench_history")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "benchhistory")
public class BenchHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "created_time", nullable = false)
    private ZonedDateTime createdTime;

    @NotNull
    @Column(name = "bench", nullable = false)
    private Boolean bench;

    @Column(name = "manager_id")
    private String managerId;

    @Column(name = "valid_to")
    private ZonedDateTime validTo;

    @Column(name = "created_by_upsa_id")
    private String createdByUpsaId;

    @Column(name = "changed_by_upsa_id")
    private String changedByUpsaId;

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

    public BenchHistory createdTime(ZonedDateTime createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public void setCreatedTime(ZonedDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public Boolean isBench() {
        return bench;
    }

    public BenchHistory bench(Boolean bench) {
        this.bench = bench;
        return this;
    }

    public void setBench(Boolean bench) {
        this.bench = bench;
    }

    public String getManagerId() {
        return managerId;
    }

    public BenchHistory managerId(String managerId) {
        this.managerId = managerId;
        return this;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public ZonedDateTime getValidTo() {
        return validTo;
    }

    public BenchHistory validTo(ZonedDateTime validTo) {
        this.validTo = validTo;
        return this;
    }

    public void setValidTo(ZonedDateTime validTo) {
        this.validTo = validTo;
    }

    public String getCreatedByUpsaId() {
        return createdByUpsaId;
    }

    public BenchHistory createdByUpsaId(String createdByUpsaId) {
        this.createdByUpsaId = createdByUpsaId;
        return this;
    }

    public void setCreatedByUpsaId(String createdByUpsaId) {
        this.createdByUpsaId = createdByUpsaId;
    }

    public String getChangedByUpsaId() {
        return changedByUpsaId;
    }

    public BenchHistory changedByUpsaId(String changedByUpsaId) {
        this.changedByUpsaId = changedByUpsaId;
        return this;
    }

    public void setChangedByUpsaId(String changedByUpsaId) {
        this.changedByUpsaId = changedByUpsaId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public BenchHistory employee(Employee employee) {
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
        BenchHistory benchHistory = (BenchHistory) o;
        if (benchHistory.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), benchHistory.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "BenchHistory{" +
            "id=" + getId() +
            ", createdTime='" + getCreatedTime() + "'" +
            ", bench='" + isBench() + "'" +
            ", managerId='" + getManagerId() + "'" +
            ", validTo='" + getValidTo() + "'" +
            ", createdByUpsaId='" + getCreatedByUpsaId() + "'" +
            ", changedByUpsaId='" + getChangedByUpsaId() + "'" +
            "}";
    }
}
