package com.epam.bench.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.epam.bench.domain.Employee;
import com.epam.bench.facade.BenchFacade;
import com.epam.bench.web.rest.dto.EmployeeDto;
import com.epam.bench.web.rest.dto.UpdateEmployeeFormDto;
import com.epam.bench.web.rest.util.HeaderUtil;
import com.epam.bench.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing Employee.
 */
@RestController
@RequestMapping("/api")
public class BenchResource {

    private final Logger log = LoggerFactory.getLogger(BenchResource.class);

    private static final String ENTITY_NAME = "employee";

    private final BenchFacade benchFacade;

    public BenchResource(BenchFacade benchFacade) {
        this.benchFacade = benchFacade;
    }

    /**
     * POST  /bench/employees : Create a new employee.
     *
     * @param upsaId the employee upsaId to create
     * @return the ResponseEntity with status 201 (Created) and with body the new employee, or with status 400 (Bad Request) if the employee has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/bench/employees")
    @Timed
    public ResponseEntity<EmployeeDto> createEmployee(@NotNull @RequestBody String upsaId) throws URISyntaxException {
        log.debug("REST request to save Employee to bench : {}", upsaId);
        if (StringUtils.isBlank((upsaId))) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new employee cannot be created without upsaID")).body(null);
        }
        if (benchFacade.isEmployeeOnBench(upsaId)) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "An employee with upsaID {" + upsaId + "} already on bench")).body(null);
        }
        final EmployeeDto result = benchFacade.addToBench(upsaId);
        return ResponseEntity.created(new URI("/api/bench/employees/" + result.getUpsaId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getUpsaId().toString()))
            .body(result);
    }

    /**
     * PUT  /employees : Updates an existing employee.
     *
     * @param employee the employee to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated employee,
     * or with status 400 (Bad Request) if the employee is not valid,
     * or with status 500 (Internal Server Error) if the employee couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/bench/employees")
    @Timed
    public ResponseEntity<EmployeeDto> updateEmployee(@Valid @RequestBody UpdateEmployeeFormDto employee) throws URISyntaxException {
        log.debug("REST request to update Employee : {}", employee);
        if (Objects.isNull(employee) || StringUtils.isBlank(employee.getUpsaId())) {
            return ResponseEntity.notFound().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A employee cannot be updated without upsaID")).build();
        }
        EmployeeDto result = benchFacade.updateEmployee(employee);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, employee.getUpsaId().toString()))
            .body(result);
    }

    /**
     * GET  /employees : get all the employees.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of employees in body
     */
    @GetMapping("/bench/employees")
    @Timed
    public ResponseEntity<List<EmployeeDto>> getAllEmployees(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Employees");
        Page<EmployeeDto> page = benchFacade.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/employees");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /employees/:upsaId : get the "id" employee.
     *
     * @param upsaId the id of the employee to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the employee, or with status 404 (Not Found)
     */
    @GetMapping("/bench/employees/{upsaId}")
    @Timed
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable String upsaId) {
        log.debug("REST request to get Employee : {}", upsaId);
        EmployeeDto employee = benchFacade.findOne(upsaId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(employee));
    }

    /**
     * DELETE  /bench/employees/:upsaId : delete the "upsaId" employee from bench.
     *
     * @param upsaId the id of the employee to delete from bench
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/bench/employees/{upsaId}")
    @Timed
    public ResponseEntity<Void> deleteEmployee(@PathVariable String upsaId) {
        log.debug("REST request to delete Employee : {}", upsaId);
        benchFacade.removeFromBench(upsaId);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, upsaId)).build();
    }

    /**
     * SEARCH  /_search/bench/employees?query=:query : search for the employee corresponding
     * to the query.
     *
     * @param query the query of the employee search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/bench/employees")
    @Timed
    public ResponseEntity<List<EmployeeDto>> searchEmployees(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Employees for query {}", query);
        Page<EmployeeDto> page = benchFacade.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/bench/employees");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
