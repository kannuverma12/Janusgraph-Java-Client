package com.paytm.digital.education.admin.controller;

import com.paytm.digital.education.admin.request.EntitySourceMappingRequest;
import com.paytm.digital.education.admin.response.EntitySourceMappingResponse;
import com.paytm.digital.education.admin.service.impl.EntitySourceMappingServiceImpl;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static com.paytm.digital.education.constant.ExploreConstants.EDUCATION_BASE_URL;

@Validated
@RestController
@RequestMapping(EDUCATION_BASE_URL)
@AllArgsConstructor
public class EntitySourceMappingAdminController {

    private static final Logger log = LoggerFactory.getLogger(EntitySourceMappingAdminController.class);

    private EntitySourceMappingServiceImpl entitySourceMappingService;

    @PostMapping("/admin/entity/v1/sourceMapping")
    public @ResponseBody EntitySourceMappingResponse createEntitySourceMapping(
            @RequestBody @Valid EntitySourceMappingRequest entitySourceMappingRequest) {
        return entitySourceMappingService.saveEntitySourceMapping(entitySourceMappingRequest);
    }

    @GetMapping("/admin/entity/v1/sourceMapping/{entity}/{entity_id}")
    public @ResponseBody EntitySourceMappingResponse getEntitySourceMapping(
            @NotNull @PathVariable("entity") EducationEntity entity,
            @PathVariable("entity_id") Long entityId) {
        return entitySourceMappingService.getEntitySourceMapping(entity, entityId);
    }

    @DeleteMapping("/admin/entity/v1/sourceMapping")
    public @ResponseBody EntitySourceMappingResponse deleteEntitySourceMapping(
            @RequestBody @Valid EntitySourceMappingRequest entitySourceMappingRequest) {
        return entitySourceMappingService.deleteEntitySourceMapping(entitySourceMappingRequest);
    }
}
