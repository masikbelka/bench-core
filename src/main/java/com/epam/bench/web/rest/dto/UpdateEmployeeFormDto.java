package com.epam.bench.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Size;

import static com.epam.bench.config.Constants.INCOMING_DATE_PATTERN;

@ApiModel
public class UpdateEmployeeFormDto {
    @ApiModelProperty(required = true)
    private String upsaId;
    private String managerName;
    private String fullName;
    private String skill;
    private String skillId;
    private String title;
    private LanguageLevelDto language;
    private String status;
    @JsonFormat(pattern = INCOMING_DATE_PATTERN)
    private String availableFrom;
    @JsonFormat(pattern = INCOMING_DATE_PATTERN)
    private String availableTill;
    @ApiModelProperty(allowableValues = "LOW,MEDIUM,HIGH")
    private String probability;
    private String pastProjects;
    @Size(max=256)
    private String comment;

    public UpdateEmployeeFormDto() {
    }

    public String getUpsaId() {
        return upsaId;
    }

    public void setUpsaId(String upsaId) {
        this.upsaId = upsaId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LanguageLevelDto getLanguage() {
        return language;
    }

    public void setLanguage(LanguageLevelDto language) {
        this.language = language;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getProbability() {
        return probability;
    }

    public void setProbability(String probability) {
        this.probability = probability;
    }

    public String getPastProjects() {
        return pastProjects;
    }

    public void setPastProjects(String pastProjects) {
        this.pastProjects = pastProjects;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
