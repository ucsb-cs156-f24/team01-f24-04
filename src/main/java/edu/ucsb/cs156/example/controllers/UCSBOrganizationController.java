package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBOrganizations;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationsRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.validation.Valid;

@Tag(name = "UCSBOrganizations")
@RequestMapping("/api/ucsborganizations")
@RestController
@Slf4j
public class UCSBOrganizationController extends ApiController{
    @Autowired
    UCSBOrganizationsRepository ucsbOrganizationsRepository;
    /**
     * This method returns a list of all ucsborganizations.
     * @return a list of all ucsborganizations
     */
    @Operation(summary= "List all ucsb organizations")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBOrganizations> allOrganizations() {
        Iterable<UCSBOrganizations> organizations = ucsbOrganizationsRepository.findAll();
        return organizations;
    }

    /**
     * This method returns a single organization.
     * @param orgCode code of the organization
     * @return a single organization
     */
    @Operation(summary= "Get a single organization")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UCSBOrganizations getById(
            @Parameter(name="orgCode") @RequestParam String orgCode) {
        UCSBOrganizations org = ucsbOrganizationsRepository.findById(orgCode)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganizations.class, orgCode));
        return org;
    }

    /**
     * This method creates a new organiation. Accessible only to users with the role "ROLE_ADMIN".
     * @param orgCode code of the organization
     * @param orgTranslationShort short translation of the organization
     * @param orgTranslation translation of the organization
     * @param inactive whether the organization is inactive
     * @return the newly created organization
     */
    @Operation(summary= "Create a new organization")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBOrganizations postOrgs(
        @Parameter(name="orgCode") @RequestParam String orgCode,
        @Parameter(name="orgTranslationShort") @RequestParam String orgTranslationShort,
        @Parameter(name="orgTranslation") @RequestParam String orgTranslation,
        @Parameter(name="inactive") @RequestParam boolean inactive
        )
        {
        UCSBOrganizations org = new UCSBOrganizations();
        org.setOrgCode(orgCode);
        org.setOrgTranslationShort(orgTranslationShort);
        org.setOrgTranslation(orgTranslation);
        org.setInactive(inactive);
        UCSBOrganizations savedOrg = ucsbOrganizationsRepository.save(org);
        return savedOrg;
    }

    /**
     * Update a single org. Accessible only to users with the role "ROLE_ADMIN".
     * @param orgCode the code of the org to update
     * @param incoming the new org contents
     * @return the updated org object
     */
    @Operation(summary= "Update a single organization")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public UCSBOrganizations updateOrgs(
            @Parameter(name="orgCode") @RequestParam String orgCode,
            @RequestBody @Valid UCSBOrganizations incoming) {
        UCSBOrganizations org = ucsbOrganizationsRepository.findById(orgCode)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganizations.class, orgCode));
        org.setOrgCode(incoming.getOrgCode());
        org.setOrgTranslationShort(incoming.getOrgTranslationShort());
        org.setOrgTranslation(incoming.getOrgTranslation());
        org.setInactive(incoming.getInactive());
        ucsbOrganizationsRepository.save(org);
        return org;
    }

    /**
     * Delete an organization. Accessible only to users with the role "ROLE_ADMIN".
     * @param orgCode code of the organization
     * @return a message indiciating the organization was deleted
     */
    @Operation(summary= "Delete a UCSBOrganization")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteOrgs(
            @Parameter(name="orgCode") @RequestParam String orgCode) {
        UCSBOrganizations org = ucsbOrganizationsRepository.findById(orgCode)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganizations.class, orgCode));
        ucsbOrganizationsRepository.delete(org);
        return genericMessage("UCSBOrganizations with id %s deleted".formatted(orgCode));
    }
}