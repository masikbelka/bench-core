package com.epam.bench.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import com.epam.bench.domain.enumeration.PositionStatus;

/**
 * A OpportunityPosition.
 */
@Entity
@Table(name = "opportunity_position")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "opportunityposition")
public class OpportunityPosition implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "created_time", nullable = false)
    private LocalDate createdTime;

    @Column(name = "employee_upsa_id")
    private String employeeUpsaId;

    @Column(name = "employee_full_name")
    private String employeeFullName;

    @NotNull
    @Column(name = "owner_upsa_id", nullable = false)
    private String ownerUpsaId;

    @NotNull
    @Column(name = "owner_full_name", nullable = false)
    private String ownerFullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PositionStatus status;

    @ManyToOne
    private Opportunity opportunity;

    @OneToOne
    @JoinColumn(unique = true)
    private ProjectRole role;

    @ManyToOne
    private Employee employee;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreatedTime() {
        return createdTime;
    }

    public OpportunityPosition createdTime(LocalDate createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public void setCreatedTime(LocalDate createdTime) {
        this.createdTime = createdTime;
    }

    public String getEmployeeUpsaId() {
        return employeeUpsaId;
    }

    public OpportunityPosition employeeUpsaId(String employeeUpsaId) {
        this.employeeUpsaId = employeeUpsaId;
        return this;
    }

    public void setEmployeeUpsaId(String employeeUpsaId) {
        this.employeeUpsaId = employeeUpsaId;
    }

    public String getEmployeeFullName() {
        return employeeFullName;
    }

    public OpportunityPosition employeeFullName(String employeeFullName) {
        this.employeeFullName = employeeFullName;
        return this;
    }

    public void setEmployeeFullName(String employeeFullName) {
        this.employeeFullName = employeeFullName;
    }

    public String getOwnerUpsaId() {
        return ownerUpsaId;
    }

    public OpportunityPosition ownerUpsaId(String ownerUpsaId) {
        this.ownerUpsaId = ownerUpsaId;
        return this;
    }

    public void setOwnerUpsaId(String ownerUpsaId) {
        this.ownerUpsaId = ownerUpsaId;
    }

    public String getOwnerFullName() {
        return ownerFullName;
    }

    public OpportunityPosition ownerFullName(String ownerFullName) {
        this.ownerFullName = ownerFullName;
        return this;
    }

    public void setOwnerFullName(String ownerFullName) {
        this.ownerFullName = ownerFullName;
    }

    public PositionStatus getStatus() {
        return status;
    }

    public OpportunityPosition status(PositionStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(PositionStatus status) {
        this.status = status;
    }

    public Opportunity getOpportunity() {
        return opportunity;
    }

    public OpportunityPosition opportunity(Opportunity opportunity) {
        this.opportunity = opportunity;
        return this;
    }

    public void setOpportunity(Opportunity opportunity) {
        this.opportunity = opportunity;
    }

    public ProjectRole getRole() {
        return role;
    }

    public OpportunityPosition role(ProjectRole projectRole) {
        this.role = projectRole;
        return this;
    }

    public void setRole(ProjectRole projectRole) {
        this.role = projectRole;
    }

    public Employee getEmployee() {
        return employee;
    }

    public OpportunityPosition employee(Employee employee) {
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
        OpportunityPosition opportunityPosition = (OpportunityPosition) o;
        if (opportunityPosition.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), opportunityPosition.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "OpportunityPosition{" +
            "id=" + getId() +
            ", createdTime='" + getCreatedTime() + "'" +
            ", employeeUpsaId='" + getEmployeeUpsaId() + "'" +
            ", employeeFullName='" + getEmployeeFullName() + "'" +
            ", ownerUpsaId='" + getOwnerUpsaId() + "'" +
            ", ownerFullName='" + getOwnerFullName() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
