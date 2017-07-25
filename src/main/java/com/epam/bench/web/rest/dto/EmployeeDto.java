package com.epam.bench.web.rest.dto;

import java.util.ArrayList;
import java.util.List;

public class EmployeeDto {

    private String id;
    private String upsaId;
    private String fullName;
    private String managerUpsaId;
    private String managerFullName;

    private String skillName;
    private String skillUpsaId;

    private TitleDto title;
    private LanguageLevelDto languageLevel;
    private String availableFrom;
    private String availableTill;
    private List<ProjectWorkloadDto> workload = new ArrayList<>();

    private List<ProposedPositionsDto> proposedPositions = new ArrayList<>();
    private String comment;
    private int daysOnBench;
    private String onBenchSince;
    private String probability;

    public EmployeeDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUpsaId() {
        return upsaId;
    }

    public void setUpsaId(String upsaId) {
        this.upsaId = upsaId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getManagerUpsaId() {
        return managerUpsaId;
    }

    public void setManagerUpsaId(String managerUpsaId) {
        this.managerUpsaId = managerUpsaId;
    }

    public String getManagerFullName() {
        return managerFullName;
    }

    public void setManagerFullName(String managerFullName) {
        this.managerFullName = managerFullName;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getSkillUpsaId() {
        return skillUpsaId;
    }

    public void setSkillUpsaId(String skillUpsaId) {
        this.skillUpsaId = skillUpsaId;
    }

    public TitleDto getTitle() {
        return title;
    }

    public void setTitle(TitleDto title) {
        this.title = title;
    }

    public LanguageLevelDto getLanguageLevel() {
        return languageLevel;
    }

    public void setLanguageLevel(LanguageLevelDto languageLevel) {
        this.languageLevel = languageLevel;
    }

    public String getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(String availableFrom) {
        this.availableFrom = availableFrom;
    }

    public String getAvailableTill() {
        return availableTill;
    }

    public void setAvailableTill(String availableTill) {
        this.availableTill = availableTill;
    }

    public List<ProjectWorkloadDto> getWorkload() {
        return workload;
    }

    public void setWorkload(List<ProjectWorkloadDto> workload) {
        this.workload = workload;
    }

    public List<ProposedPositionsDto> getProposedPositions() {
        return proposedPositions;
    }

    public void setProposedPositions(List<ProposedPositionsDto> proposedPositions) {
        this.proposedPositions = proposedPositions;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getDaysOnBench() {
        return daysOnBench;
    }

    public void setDaysOnBench(int daysOnBench) {
        this.daysOnBench = daysOnBench;
    }

    public String getOnBenchSince() {
        return onBenchSince;
    }

    public void setOnBenchSince(String onBenchSince) {
        this.onBenchSince = onBenchSince;
    }

    public String getProbability() {
        return probability;
    }

    public void setProbability(String probability) {
        this.probability = probability;
    }
}
