package com.paytm.digital.education.explore.controller;


import static com.paytm.digital.education.explore.constants.ExploreConstants.EDUCATION_BASE_URL;

import com.paytm.digital.education.explore.response.dto.detail.ExamDetail;
import com.paytm.digital.education.explore.response.dto.detail.InstituteComparison;
import com.paytm.digital.education.explore.service.CompareService;
import com.paytm.digital.education.explore.validators.ExploreValidator;
import java.util.List;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@Validated
@RequestMapping(EDUCATION_BASE_URL)
public class CompareController {

  @Autowired
  private CompareService compareService;
  private ExploreValidator exploreValidator;

  @RequestMapping(method = RequestMethod.GET, path = "/compare/v1/inst/{inst1Id}/{inst2Id}")
  public @ResponseBody
  InstituteComparison compareInstitutes(@PathVariable("inst1Id") long inst1Id,
      @PathVariable("inst2Id") long inst2Id,
      @RequestParam(name = "field_group", required = false) String fieldGroup,
      @RequestParam(name = "fields", required = false) List<String> fields,
      @RequestHeader(value = "x-user-id", required = false) Long userId) throws Exception {


    exploreValidator.validateFieldAndFieldGroup(fields, fieldGroup);

    return compareService.compareInstitutes(inst1Id, inst2Id, fieldGroup, fields);
  }

}
