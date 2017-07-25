package com.epam.bench.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import com.epam.bench.domain.enumeration.JobResultStatus;

/**
 * A Job.
 */
@Entity
@Table(name = "job")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "job")
public class Job implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "send_email")
    private Boolean sendEmail;

    @Column(name = "email")
    private String email;

    @Column(name = "last_start_time")
    private LocalDate lastStartTime;

    @Column(name = "last_end_time")
    private LocalDate lastEndTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "last_result")
    private JobResultStatus lastResult;

    @Column(name = "last_user")
    private String lastUser;

    @OneToMany(mappedBy = "job")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<JobExecution> executions = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public Job code(String code) {
        this.code = code;
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public Job enabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean isSendEmail() {
        return sendEmail;
    }

    public Job sendEmail(Boolean sendEmail) {
        this.sendEmail = sendEmail;
        return this;
    }

    public void setSendEmail(Boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public String getEmail() {
        return email;
    }

    public Job email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getLastStartTime() {
        return lastStartTime;
    }

    public Job lastStartTime(LocalDate lastStartTime) {
        this.lastStartTime = lastStartTime;
        return this;
    }

    public void setLastStartTime(LocalDate lastStartTime) {
        this.lastStartTime = lastStartTime;
    }

    public LocalDate getLastEndTime() {
        return lastEndTime;
    }

    public Job lastEndTime(LocalDate lastEndTime) {
        this.lastEndTime = lastEndTime;
        return this;
    }

    public void setLastEndTime(LocalDate lastEndTime) {
        this.lastEndTime = lastEndTime;
    }

    public JobResultStatus getLastResult() {
        return lastResult;
    }

    public Job lastResult(JobResultStatus lastResult) {
        this.lastResult = lastResult;
        return this;
    }

    public void setLastResult(JobResultStatus lastResult) {
        this.lastResult = lastResult;
    }

    public String getLastUser() {
        return lastUser;
    }

    public Job lastUser(String lastUser) {
        this.lastUser = lastUser;
        return this;
    }

    public void setLastUser(String lastUser) {
        this.lastUser = lastUser;
    }

    public Set<JobExecution> getExecutions() {
        return executions;
    }

    public Job executions(Set<JobExecution> jobExecutions) {
        this.executions = jobExecutions;
        return this;
    }

    public Job addExecutions(JobExecution jobExecution) {
        this.executions.add(jobExecution);
        jobExecution.setJob(this);
        return this;
    }

    public Job removeExecutions(JobExecution jobExecution) {
        this.executions.remove(jobExecution);
        jobExecution.setJob(null);
        return this;
    }

    public void setExecutions(Set<JobExecution> jobExecutions) {
        this.executions = jobExecutions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Job job = (Job) o;
        if (job.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), job.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Job{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", enabled='" + isEnabled() + "'" +
            ", sendEmail='" + isSendEmail() + "'" +
            ", email='" + getEmail() + "'" +
            ", lastStartTime='" + getLastStartTime() + "'" +
            ", lastEndTime='" + getLastEndTime() + "'" +
            ", lastResult='" + getLastResult() + "'" +
            ", lastUser='" + getLastUser() + "'" +
            "}";
    }
}
