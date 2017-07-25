package com.epam.bench.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import com.epam.bench.domain.enumeration.JobResultStatus;

/**
 * A JobExecution.
 */
@Entity
@Table(name = "job_execution")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "jobexecution")
public class JobExecution implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time")
    private LocalDate startTime;

    @Column(name = "end_time")
    private LocalDate endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "result")
    private JobResultStatus result;

    @Column(name = "jhi_user")
    private String user;

    @ManyToOne
    private Job job;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartTime() {
        return startTime;
    }

    public JobExecution startTime(LocalDate startTime) {
        this.startTime = startTime;
        return this;
    }

    public void setStartTime(LocalDate startTime) {
        this.startTime = startTime;
    }

    public LocalDate getEndTime() {
        return endTime;
    }

    public JobExecution endTime(LocalDate endTime) {
        this.endTime = endTime;
        return this;
    }

    public void setEndTime(LocalDate endTime) {
        this.endTime = endTime;
    }

    public JobResultStatus getResult() {
        return result;
    }

    public JobExecution result(JobResultStatus result) {
        this.result = result;
        return this;
    }

    public void setResult(JobResultStatus result) {
        this.result = result;
    }

    public String getUser() {
        return user;
    }

    public JobExecution user(String user) {
        this.user = user;
        return this;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Job getJob() {
        return job;
    }

    public JobExecution job(Job job) {
        this.job = job;
        return this;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JobExecution jobExecution = (JobExecution) o;
        if (jobExecution.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), jobExecution.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "JobExecution{" +
            "id=" + getId() +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", result='" + getResult() + "'" +
            ", user='" + getUser() + "'" +
            "}";
    }
}
