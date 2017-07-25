package com.epam.bench.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import com.epam.bench.domain.enumeration.OpportunityStatus;

/**
 * A Opportunity.
 */
@Entity
@Table(name = "opportunity")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "opportunity")
public class Opportunity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "staffing_id")
    private String staffingId;

    @Column(name = "name")
    private String name;

    @Column(name = "owner_upsa_id")
    private String ownerUpsaId;

    @Column(name = "owner_full_name")
    private String ownerFullName;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OpportunityStatus status;

    @Size(max = 1000)
    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "staffing_coordinatior_upsa_id")
    private String staffingCoordinatiorUpsaId;

    @Column(name = "staffing_coordinatior_full_name")
    private String staffingCoordinatiorFullName;

    @Column(name = "responsible_manager_upsa_id")
    private String responsibleManagerUpsaId;

    @Column(name = "responsible_manager_full_name")
    private String responsibleManagerFullName;

    @Column(name = "supervisor_upsa_id")
    private String supervisorUpsaId;

    @Column(name = "supervisor_full_name")
    private String supervisorFullName;

    @Column(name = "delivery_manager_upsa_id")
    private String deliveryManagerUpsaId;

    @Column(name = "delivery_manager_full_name")
    private String deliveryManagerFullName;

    @Column(name = "account_manager_upsa_id")
    private String accountManagerUpsaId;

    @Column(name = "account_manager_full_name")
    private String accountManagerFullName;

    @OneToOne
    @JoinColumn(unique = true)
    private OpportunityType type;

    @OneToMany(mappedBy = "opportunity")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<OpportunityPosition> assignedPositions = new HashSet<>();

    @OneToMany(mappedBy = "opportunity")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Location> locations = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStaffingId() {
        return staffingId;
    }

    public Opportunity staffingId(String staffingId) {
        this.staffingId = staffingId;
        return this;
    }

    public void setStaffingId(String staffingId) {
        this.staffingId = staffingId;
    }

    public String getName() {
        return name;
    }

    public Opportunity name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerUpsaId() {
        return ownerUpsaId;
    }

    public Opportunity ownerUpsaId(String ownerUpsaId) {
        this.ownerUpsaId = ownerUpsaId;
        return this;
    }

    public void setOwnerUpsaId(String ownerUpsaId) {
        this.ownerUpsaId = ownerUpsaId;
    }

    public String getOwnerFullName() {
        return ownerFullName;
    }

    public Opportunity ownerFullName(String ownerFullName) {
        this.ownerFullName = ownerFullName;
        return this;
    }

    public void setOwnerFullName(String ownerFullName) {
        this.ownerFullName = ownerFullName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public Opportunity startDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Opportunity endDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public OpportunityStatus getStatus() {
        return status;
    }

    public Opportunity status(OpportunityStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(OpportunityStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public Opportunity description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStaffingCoordinatiorUpsaId() {
        return staffingCoordinatiorUpsaId;
    }

    public Opportunity staffingCoordinatiorUpsaId(String staffingCoordinatiorUpsaId) {
        this.staffingCoordinatiorUpsaId = staffingCoordinatiorUpsaId;
        return this;
    }

    public void setStaffingCoordinatiorUpsaId(String staffingCoordinatiorUpsaId) {
        this.staffingCoordinatiorUpsaId = staffingCoordinatiorUpsaId;
    }

    public String getStaffingCoordinatiorFullName() {
        return staffingCoordinatiorFullName;
    }

    public Opportunity staffingCoordinatiorFullName(String staffingCoordinatiorFullName) {
        this.staffingCoordinatiorFullName = staffingCoordinatiorFullName;
        return this;
    }

    public void setStaffingCoordinatiorFullName(String staffingCoordinatiorFullName) {
        this.staffingCoordinatiorFullName = staffingCoordinatiorFullName;
    }

    public String getResponsibleManagerUpsaId() {
        return responsibleManagerUpsaId;
    }

    public Opportunity responsibleManagerUpsaId(String responsibleManagerUpsaId) {
        this.responsibleManagerUpsaId = responsibleManagerUpsaId;
        return this;
    }

    public void setResponsibleManagerUpsaId(String responsibleManagerUpsaId) {
        this.responsibleManagerUpsaId = responsibleManagerUpsaId;
    }

    public String getResponsibleManagerFullName() {
        return responsibleManagerFullName;
    }

    public Opportunity responsibleManagerFullName(String responsibleManagerFullName) {
        this.responsibleManagerFullName = responsibleManagerFullName;
        return this;
    }

    public void setResponsibleManagerFullName(String responsibleManagerFullName) {
        this.responsibleManagerFullName = responsibleManagerFullName;
    }

    public String getSupervisorUpsaId() {
        return supervisorUpsaId;
    }

    public Opportunity supervisorUpsaId(String supervisorUpsaId) {
        this.supervisorUpsaId = supervisorUpsaId;
        return this;
    }

    public void setSupervisorUpsaId(String supervisorUpsaId) {
        this.supervisorUpsaId = supervisorUpsaId;
    }

    public String getSupervisorFullName() {
        return supervisorFullName;
    }

    public Opportunity supervisorFullName(String supervisorFullName) {
        this.supervisorFullName = supervisorFullName;
        return this;
    }

    public void setSupervisorFullName(String supervisorFullName) {
        this.supervisorFullName = supervisorFullName;
    }

    public String getDeliveryManagerUpsaId() {
        return deliveryManagerUpsaId;
    }

    public Opportunity deliveryManagerUpsaId(String deliveryManagerUpsaId) {
        this.deliveryManagerUpsaId = deliveryManagerUpsaId;
        return this;
    }

    public void setDeliveryManagerUpsaId(String deliveryManagerUpsaId) {
        this.deliveryManagerUpsaId = deliveryManagerUpsaId;
    }

    public String getDeliveryManagerFullName() {
        return deliveryManagerFullName;
    }

    public Opportunity deliveryManagerFullName(String deliveryManagerFullName) {
        this.deliveryManagerFullName = deliveryManagerFullName;
        return this;
    }

    public void setDeliveryManagerFullName(String deliveryManagerFullName) {
        this.deliveryManagerFullName = deliveryManagerFullName;
    }

    public String getAccountManagerUpsaId() {
        return accountManagerUpsaId;
    }

    public Opportunity accountManagerUpsaId(String accountManagerUpsaId) {
        this.accountManagerUpsaId = accountManagerUpsaId;
        return this;
    }

    public void setAccountManagerUpsaId(String accountManagerUpsaId) {
        this.accountManagerUpsaId = accountManagerUpsaId;
    }

    public String getAccountManagerFullName() {
        return accountManagerFullName;
    }

    public Opportunity accountManagerFullName(String accountManagerFullName) {
        this.accountManagerFullName = accountManagerFullName;
        return this;
    }

    public void setAccountManagerFullName(String accountManagerFullName) {
        this.accountManagerFullName = accountManagerFullName;
    }

    public OpportunityType getType() {
        return type;
    }

    public Opportunity type(OpportunityType opportunityType) {
        this.type = opportunityType;
        return this;
    }

    public void setType(OpportunityType opportunityType) {
        this.type = opportunityType;
    }

    public Set<OpportunityPosition> getAssignedPositions() {
        return assignedPositions;
    }

    public Opportunity assignedPositions(Set<OpportunityPosition> opportunityPositions) {
        this.assignedPositions = opportunityPositions;
        return this;
    }

    public Opportunity addAssignedPositions(OpportunityPosition opportunityPosition) {
        this.assignedPositions.add(opportunityPosition);
        opportunityPosition.setOpportunity(this);
        return this;
    }

    public Opportunity removeAssignedPositions(OpportunityPosition opportunityPosition) {
        this.assignedPositions.remove(opportunityPosition);
        opportunityPosition.setOpportunity(null);
        return this;
    }

    public void setAssignedPositions(Set<OpportunityPosition> opportunityPositions) {
        this.assignedPositions = opportunityPositions;
    }

    public Set<Location> getLocations() {
        return locations;
    }

    public Opportunity locations(Set<Location> locations) {
        this.locations = locations;
        return this;
    }

    public Opportunity addLocation(Location location) {
        this.locations.add(location);
        location.setOpportunity(this);
        return this;
    }

    public Opportunity removeLocation(Location location) {
        this.locations.remove(location);
        location.setOpportunity(null);
        return this;
    }

    public void setLocations(Set<Location> locations) {
        this.locations = locations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Opportunity opportunity = (Opportunity) o;
        if (opportunity.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), opportunity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Opportunity{" +
            "id=" + getId() +
            ", staffingId='" + getStaffingId() + "'" +
            ", name='" + getName() + "'" +
            ", ownerUpsaId='" + getOwnerUpsaId() + "'" +
            ", ownerFullName='" + getOwnerFullName() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", description='" + getDescription() + "'" +
            ", staffingCoordinatiorUpsaId='" + getStaffingCoordinatiorUpsaId() + "'" +
            ", staffingCoordinatiorFullName='" + getStaffingCoordinatiorFullName() + "'" +
            ", responsibleManagerUpsaId='" + getResponsibleManagerUpsaId() + "'" +
            ", responsibleManagerFullName='" + getResponsibleManagerFullName() + "'" +
            ", supervisorUpsaId='" + getSupervisorUpsaId() + "'" +
            ", supervisorFullName='" + getSupervisorFullName() + "'" +
            ", deliveryManagerUpsaId='" + getDeliveryManagerUpsaId() + "'" +
            ", deliveryManagerFullName='" + getDeliveryManagerFullName() + "'" +
            ", accountManagerUpsaId='" + getAccountManagerUpsaId() + "'" +
            ", accountManagerFullName='" + getAccountManagerFullName() + "'" +
            "}";
    }
}
