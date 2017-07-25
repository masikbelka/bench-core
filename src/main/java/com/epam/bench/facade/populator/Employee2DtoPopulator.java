package com.epam.bench.facade.populator;

import com.epam.bench.domain.Employee;
import com.epam.bench.domain.JobFunction;
import com.epam.bench.domain.LanguageLevel;
import com.epam.bench.domain.PrimarySkill;
import com.epam.bench.domain.enumeration.Probability;
import com.epam.bench.service.ServiceUtils;
import com.epam.bench.web.rest.dto.*;
import org.apache.commons.lang3.StringUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Employee2DtoPopulator implements Populator<Employee, EmployeeDto>{

    @Override
    public void populate(final Employee source, final EmployeeDto target) {
        target.setUpsaId(StringUtils.defaultString(source.getUpsaId()));
        target.setManagerUpsaId(StringUtils.defaultString(source.getManagerId()));
        target.setManagerFullName(StringUtils.defaultString(source.getManagerFullName()));

        target.setFullName(StringUtils.defaultString(source.getFullName()));

        populateLanguage(source, target);

        target.setAvailableFrom(dateOrDefault(source.getAvailableFrom()));
        target.setAvailableTill(dateOrDefault(source.getAvailableFrom()));

        populateSkill(source, target);
        populateTitle(source, target);

        target.setComment(StringUtils.defaultString(source.getComment()));

        populateProposalPositions(source, target);

        populateWorkload(source, target);

        target.setDaysOnBench(22);
        target.setOnBenchSince(dateOrDefault(ZonedDateTime.now()));

        populateProbability(source, target);
    }

    private String dateOrDefault(final ZonedDateTime availableFrom) {
        ZonedDateTime dateToFormat = ZonedDateTime.now();
        if (Objects.nonNull(availableFrom)) {
            dateToFormat = availableFrom;
        }
        return ServiceUtils.getFormattedDate(dateToFormat);
    }

    private void populateWorkload(Employee employee, EmployeeDto employeeDto) {
        List<ProjectWorkloadDto> workloadDto = new ArrayList<>();
        ProjectWorkloadDto projectWorkloadDto = new ProjectWorkloadDto();
        projectWorkloadDto.setType("Internal Project");
        projectWorkloadDto.setName("EPM-TEST");
        projectWorkloadDto.setWorkloadPercentage(80);
        projectWorkloadDto.setUpsaId("456432154651324621");

        workloadDto.add(projectWorkloadDto);
        employeeDto.setWorkload(workloadDto);
    }


    private void populateLanguage(Employee employee, EmployeeDto employeeDto) {
        LanguageLevel languageLevel = employee.getEnglishLevel();
        if (Objects.nonNull(languageLevel)) {
            LanguageLevelDto languageLevelDto = new LanguageLevelDto();
            languageLevelDto.setLanguage(languageLevel.getLanguage());
            languageLevelDto.setSpeaking(languageLevel.getSpeaking());
            languageLevelDto.setWriting(languageLevel.getWriting());
            employeeDto.setLanguageLevel(languageLevelDto);
        }

    }

    private void populateProposalPositions(Employee source, EmployeeDto target) {

        List<ProposedPositionsDto> targetPositions = new ArrayList<>();
        ProposedPositionsDto proposedPositionsDto = new ProposedPositionsDto();
        proposedPositionsDto.setName("EPM-OPP");
        proposedPositionsDto.setType("opportunities");
        proposedPositionsDto.setId("12325469897432");
        proposedPositionsDto.setStatus("PROPOSED");
        targetPositions.add(proposedPositionsDto);
        target.setProposedPositions(targetPositions);
    }

    private void populateProbability(Employee employee, EmployeeDto employeeDto) {
        Probability probability = Probability.HIGH;

        employeeDto.setProbability(probability.toString());
    }

    private void populateTitle(Employee employee, EmployeeDto employeeDto) {
        TitleDto titleDto = new TitleDto();
        JobFunction jobFunction = employee.getJobFunction();
        if (Objects.nonNull(jobFunction)) {
            titleDto.setFullTitle(jobFunction.getName());
            titleDto.setShortTitle(jobFunction.getPrefix());
        }
        employeeDto.setTitle(titleDto);
    }

    private void populateSkill(Employee employee, EmployeeDto employeeDto) {
        PrimarySkill skill = employee.getPrimarySkill();
        if (Objects.nonNull(skill)) {
            employeeDto.setSkillName(StringUtils.defaultString(skill.getName()));
            employeeDto.setSkillUpsaId(StringUtils.defaultString(skill.getUpsaId()));
        }
    }
}
