package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.entities.UCSBDiningCommonMenuItem;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBDateRepository;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.time.LocalDateTime;

@Tag(name = "UCSBDiningCommonsMenuItem")
@RequestMapping("/api/ucsbdiningcommonsmenuitem")
@RestController
@Slf4j

public class UCSBDiningCommonsMenuItemController extends ApiController {
    @Autowired
    UCSBDiningCommonsMenuItemRepository ucsbDiningCommonsMenuItemRepository;

    @GetMapping("/all")
    public Iterable<UCSBDiningCommonMenuItem> allUCSBDiningCommonsMenuItems() {
        Iterable<UCSBDiningCommonMenuItem> menuItem = ucsbDiningCommonsMenuItemRepository.findAll();
        return menuItem;
    }

    @PostMapping("/post")
    public UCSBDiningCommonMenuItem postUCSBDiningCommonsMenuItem(
        @Parameter(name="diningCommonsCode") @RequestParam String diningCommonsCode,
        @Parameter(name="name") @RequestParam String name,
        @Parameter(name="station") @RequestParam String station)
        throws JsonProcessingException {

    // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    // See: https://www.baeldung.com/spring-date-parameters

    //log.info("localDateTime={}", localDateTime);

    UCSBDiningCommonMenuItem ucsbDiningCommonMenuItem = new UCSBDiningCommonMenuItem();
    ucsbDiningCommonMenuItem.setDiningCommonsCode(diningCommonsCode);
    ucsbDiningCommonMenuItem.setName(name);
    ucsbDiningCommonMenuItem.setStation(station);

    UCSBDiningCommonMenuItem savedUcsbDiningCommonMenuItem = ucsbDiningCommonsMenuItemRepository.save(ucsbDiningCommonMenuItem);

    return savedUcsbDiningCommonMenuItem;
}

}
