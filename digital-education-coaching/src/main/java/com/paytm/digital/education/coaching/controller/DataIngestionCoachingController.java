package com.paytm.digital.education.coaching.controller;

import com.paytm.digital.education.coaching.data.service.CoachingCenterService;
import com.paytm.digital.education.coaching.data.service.CoachingCourseService;
import com.paytm.digital.education.coaching.data.service.CoachingExamService;
import com.paytm.digital.education.coaching.data.service.InstituteService;
import com.paytm.digital.education.coaching.response.dto.ResponseDto;
import com.paytm.digital.education.coaching.service.impl.ImportCoachingCenterService;
import com.paytm.digital.education.coaching.service.impl.ImportCoachingCourseService;
import com.paytm.digital.education.coaching.service.impl.ImportExamService;
import com.paytm.digital.education.coaching.service.impl.ImportFacilitiesService;
import com.paytm.digital.education.coaching.service.impl.ImportGalleryService;
import com.paytm.digital.education.coaching.service.impl.ImportInstituteService;
import com.paytm.digital.education.coaching.service.impl.ImportStudentSelectedService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;

@Validated
@RestController
@RequestMapping("/v1/coaching")
@AllArgsConstructor
public class DataIngestionCoachingController {

    private InstituteService             instituteService;
    private CoachingCourseService        coachingCourseService;
    private CoachingExamService          coachingExamService;
    private ImportInstituteService       importInstituteService;
    private ImportCoachingCenterService  importCoachingCenterService;
    private ImportStudentSelectedService importStudentSelectedService;
    private ImportGalleryService         importGalleryService;
    private ImportExamService            importExamService;
    private ImportCoachingCourseService  importCoachingCourseService;
    private ImportFacilitiesService      importFacilitiesService;
    private CoachingCenterService        coachingCenterService;
    /*
    @PostMapping("/institute")
    public InstituteResponseDto createInstitute(
            @RequestBody @Valid CoachingInstituteEntity coachingInstitute) {
        return instituteCRUDService.createInstitute(coachingInstitute);
    }

    @PutMapping("/institute")
    public InstituteResponseDto updateInstitute(
            @RequestBody @Valid CoachingInstituteEntity coachingInstitute) {
        return instituteCRUDService.updateInstitute(coachingInstitute);
    }

    @PutMapping("/institute/{instituteId}")
    public InstituteResponseDto enableDisableInstitute(@PathVariable @NotNull @Min(1) Long instituteId,
            @NotNull @RequestParam("activate") Boolean activate) {
        return instituteCRUDService.updateInstituteStatus(instituteId, activate);
    }

    @PutMapping("/institute/{instituteId}/{centerId}")
    public InstituteResponseDto enableDisableCoachingCenter(@PathVariable @NotNull @Min(1) Long instituteId,
            @PathVariable @NotNull @Min(1) Long centerId,
            @NotNull @RequestParam("activate") Boolean activate) {
        return instituteCRUDService.updateCoachingCenterStatus(instituteId, centerId, activate);
    }*/

    @GetMapping("/institute/{instituteId}")
    public ResponseDto getInstituteById(@PathVariable @NotNull @Min(1) Long instituteId,
            @RequestParam(value = "active", required = false) Boolean active) {
        return instituteService.getInstituteById(instituteId, active);
    }

    /*
    @PostMapping("/course")
    public ResponseDto createCoachingCourse(@RequestBody @Valid CoachingCourse coachingCourse) {
        return coachingCourseCRUDService.createCourse(coachingCourse);
    }

    @PutMapping("/course")
    public ResponseDto updateCoachingCourse(@RequestBody @Valid CoachingCourse coachingCourse) {
        return coachingCourseCRUDService.updateCourse(coachingCourse);
    }

    @PutMapping("/course/{courseId}")
    public ResponseDto activateCoachingCourse(@PathVariable @NotNull @Min(1) Long courseId,
            @NotNull @RequestParam("activate") Boolean activate) {
        return coachingCourseCRUDService.updateCourseStatus(courseId, activate);
    }*/

    @GetMapping("/course/{courseId}")
    public ResponseDto getCoachingCourseById(@PathVariable @NotNull @Min(1) Long courseId,
            @RequestParam(value = "active", required = false) Boolean active) {
        return coachingCourseService.getCourseById(courseId, active);
    }

    /*
    @PostMapping("/exam")
    public ExamResponseDto createCoachingExam(@RequestBody @Valid CoachingExam coachingExam) {
        return coachingExamCRUDService.createCoachingExam(coachingExam);
    }

    @PutMapping("/exam")
    public ExamResponseDto updateCoachingExam(@RequestBody @Valid CoachingExam coachingExam) {
        return coachingExamCRUDService.updateCoachingExam(coachingExam);
    }

    @PutMapping("/exam/{examId}")
    public ExamResponseDto enableDisableCoachingExam(@PathVariable @NotNull @Min(1) Long examId,
            @NotNull @RequestParam("activate") Boolean activate) {
        return coachingExamCRUDService.updateCoachingExamStatus(examId, activate);
    }
    */
    @GetMapping("/exam/{examId}")
    public ResponseDto getCoachingExamById(@PathVariable @NotNull @Min(1) Long examId,
            @RequestParam(value = "active", required = false) Boolean active) {
        return coachingExamService.getCoachingExamById(examId, active);
    }

    @GetMapping("/coaching-center/{centerId}")
    public ResponseDto getCoachingCenterById(@PathVariable @NotNull @Min(1) Long centerId,
            @RequestParam(value = "active", required = false) Boolean active) {
        return coachingCenterService.getCoachingCenterById(centerId, active);
    }

    /*
    @GetMapping("/coaching-center/{coachingCenterId}")
    public ExamResponseDto getCoachingExamById(@PathVariable @NotNull @Min(1) Long coachingCenterId,
            @RequestParam(value = "active", required = false) Boolean active) {
        return coachingExamCRUDService.getCoachingExamById(examId, active);
    }*/

    @RequestMapping(method = RequestMethod.GET, path = "/v1/import/coaching-institute")
    public @ResponseBody boolean ingestInstituteData()
            throws IOException, GeneralSecurityException, ParseException {
        return importInstituteService.importData();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/v1/import/coaching-center")
    public @ResponseBody boolean ingestCoachingCenters()
            throws IOException, GeneralSecurityException, ParseException {
        return importCoachingCenterService.importData();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/v1/import/student-selected")
    public @ResponseBody boolean ingestStudentSelected()
            throws IOException, GeneralSecurityException, ParseException {
        return importStudentSelectedService.importData();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/v1/import/coaching-institute-galleries")
    public @ResponseBody boolean ingestGallery()
            throws IOException, GeneralSecurityException, ParseException {
        return importGalleryService.importData();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/v1/import/coaching-exams")
    public @ResponseBody boolean ingestCoachingExams()
            throws IOException, GeneralSecurityException, ParseException {
        return importExamService.importData();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/v1/import/coaching-courses")
    public @ResponseBody boolean ingestCoachingCourses()
            throws IOException, GeneralSecurityException {
        return importCoachingCourseService.importData();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/v1/import/coaching-facilities")
    public @ResponseBody boolean importCoachingFacilities()
            throws IOException, GeneralSecurityException {
        return importFacilitiesService.importData();
    }
}
