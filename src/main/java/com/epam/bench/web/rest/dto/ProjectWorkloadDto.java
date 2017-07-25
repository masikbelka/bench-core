package com.epam.bench.web.rest.dto;

public class ProjectWorkloadDto {
    private String upsaId;
    private String name;
    private String type;
    private int workloadPercentage;

    public ProjectWorkloadDto() {
    }

    public String getUpsaId() {
        return upsaId;
    }

    public void setUpsaId(String upsaId) {
        this.upsaId = upsaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getWorkloadPercentage() {
        return workloadPercentage;
    }

    public void setWorkloadPercentage(int workloadPercentage) {
        this.workloadPercentage = workloadPercentage;
    }
}
