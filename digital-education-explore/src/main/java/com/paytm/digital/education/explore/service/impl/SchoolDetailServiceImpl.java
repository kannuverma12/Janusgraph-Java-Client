package com.paytm.digital.education.explore.service.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.constants.SchoolConstants;
import com.paytm.digital.education.explore.database.entity.Board;
import com.paytm.digital.education.explore.database.entity.BoardData;
import com.paytm.digital.education.explore.database.entity.RelevantLink;
import com.paytm.digital.education.explore.database.entity.School;
import com.paytm.digital.education.explore.database.entity.SchoolPaytmKeys;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.enums.Client;
import com.paytm.digital.education.explore.response.dto.common.CTA;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.FacilityResponse;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.FacultyDetail;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.GeneralInformation;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.ImportantDate;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.SchoolDetail;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.SchoolGalleryResponse;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.ShiftDetailsResponse;
import com.paytm.digital.education.explore.service.SchoolService;
import com.paytm.digital.education.explore.service.helper.CTAHelper;
import com.paytm.digital.education.explore.service.helper.DerivedAttributesHelper;
import com.paytm.digital.education.explore.service.helper.FacilityDataHelper;
import com.paytm.digital.education.explore.service.helper.SchoolDetailsResponseHelper;
import com.paytm.digital.education.explore.utility.CommonUtil;

import com.paytm.digital.education.utility.CommonUtils;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.paytm.digital.education.explore.constants.ExploreConstants.CLIENT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.TENTATIVE;
import static com.paytm.digital.education.explore.constants.SchoolConstants.ACTUAL;
import static com.paytm.digital.education.explore.constants.SchoolConstants.OFFICIAL_WEBSITE_LINK;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_ID;
import static com.paytm.digital.education.explore.enums.EducationEntity.SCHOOL;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_FIELD_GROUP;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_SCHOOL_NAME;
import static com.paytm.digital.education.mapping.ErrorEnum.NO_ENTITY_FOUND;
import static com.paytm.digital.education.utility.CommonUtils.isNullOrZero;


@Service
@RequiredArgsConstructor
public class SchoolDetailServiceImpl implements SchoolService {

    private final CommonMongoRepository   commonMongoRepository;
    private final DerivedAttributesHelper derivedAttributesHelper;
    private final FacilityDataHelper      facilityDataHelper;
    private final CTAHelper               ctaHelper;

    public List<School> getSchools(List<Long> entityIds, List<String> groupFields) {
        if (CollectionUtils.isEmpty(groupFields)) {
            throw new BadRequestException(INVALID_FIELD_GROUP,
                    INVALID_FIELD_GROUP.getExternalMessage());
        }

        Set<Long> searchIds = new HashSet<>(entityIds);
        List<School> schools =
                commonMongoRepository
                        .getEntityFieldsByValuesIn(SCHOOL_ID, new ArrayList<>(searchIds),
                                School.class,
                                groupFields);

        return schools;
    }

    @Override
    public SchoolDetail getSchoolDetails(Long schoolId, Client client, String schoolName,
            List<String> fields, String fieldGroup) {
        List<String> fieldsToBeFetched =
                commonMongoRepository
                        .getFieldsByGroupAndCollectioName(SchoolConstants.SCHOOL, fields,
                                fieldGroup);
        School school =
                commonMongoRepository
                        .getEntityByFields(SCHOOL_ID, schoolId, School.class, fieldsToBeFetched);
        if (Objects.isNull(school)) {
            throw new BadRequestException(NO_ENTITY_FOUND,
                    new Object[] {SchoolConstants.SCHOOL, SCHOOL_ID, schoolId});
        }
        if (!schoolName.equals(CommonUtil.convertNameToUrlDisplayName(school.getOfficialName()))) {
            throw new BadRequestException(INVALID_SCHOOL_NAME,
                    INVALID_SCHOOL_NAME.getExternalMessage());
        }
        SchoolDetail schoolDetail = new SchoolDetail();
        schoolDetail.setSchoolId(school.getSchoolId());
        List<Board> boards = school.getBoardList();
        if (Objects.nonNull(boards) && boards.size() > 0) {
            BoardData boardData = boards.get(0).getData();
            schoolDetail.setShiftDetailsList(
                    boardData.getShifts().stream().map(ShiftDetailsResponse::new)
                            .collect(Collectors.toList()));
            schoolDetail.setFacultyDetail(fetchFacultyDetailsIfPresent(boardData));
            schoolDetail.setFeesDetails(boardData.getFeesDetails());
            List<FacilityResponse> facilityResponseList =
                    facilityDataHelper
                            .mapSchoolFacilitiesToDataObject(boardData.getSchoolFacilities());
            schoolDetail.setFacilities(facilityResponseList);
            List<ImportantDate> importantDates = Stream.concat(
                    boardData.getSchoolAdmissionList().stream()
                            .map(x -> new ImportantDate(x, ACTUAL)),
                    boardData.getSchoolAdmissionTentativeList().stream()
                            .map(x -> new ImportantDate(x, TENTATIVE))
            ).collect(Collectors.toList());
            schoolDetail.setImportantDateSections(importantDates);
            schoolDetail.setGallery(
                    Optional.ofNullable(school.getGallery()).map(SchoolGalleryResponse::new)
                            .orElse(null));
            String entityName = SCHOOL.name().toLowerCase();
            schoolDetail.setDerivedAttributes(
                    derivedAttributesHelper.getDerivedAttributes(
                            Maps.newHashMap(ImmutableMap.of(
                                    entityName, school,
                                    CLIENT, client
                            )),
                            entityName,
                            client));
            schoolDetail.setGeneralInformation(collectGeneralInformationFromSchool(school));
            schoolDetail.setStreams(boardData.getStreams());
            SchoolDetailsResponseHelper.pruneDuplicateDataInSchoolDetail(schoolDetail);
            Optional<SchoolPaytmKeys> schoolPaytmKeys = Optional.ofNullable(school.getPaytmKeys());
            schoolDetail.setPid(schoolPaytmKeys.map(SchoolPaytmKeys::getPid).orElse(null));
            schoolDetail.setFormId(schoolPaytmKeys.map(SchoolPaytmKeys::getFormId).orElse(null));
            schoolDetail.setBrochureUrl(boardData.getSchoolBrochureLink());
            List<CTA> ctaList = ctaHelper.buildCTA(schoolDetail, client);
            schoolDetail.setCtaList(ctaList);
        }
        return schoolDetail;
    }

    private FacultyDetail fetchFacultyDetailsIfPresent(BoardData boardData) {
        if (Objects.isNull(boardData)) {
            return null;
        }
        Integer numberOfTeachers = boardData.getNoOfTeachers();
        Integer numberOfTrainedTeachers = boardData.getNoOfTrainedTeachers();
        Integer numberOfUntrainedTeachers = boardData.getNoOfUntrainedTeachers();
        String studentRatio = boardData.getStudentRatio();
        if (isNullOrZero(numberOfTeachers)
            || StringUtils.isBlank(studentRatio)) {
            return null;
        }
        FacultyDetail facultyDetail = new FacultyDetail(numberOfTeachers, studentRatio);
        facultyDetail.setNoOfTrainedTeachers(numberOfTrainedTeachers);
        facultyDetail.setNoOfUntrainedTeachers(numberOfUntrainedTeachers);
        return facultyDetail;
    }

    private GeneralInformation collectGeneralInformationFromSchool(School school) {
        List<Board> boards = school.getBoardList();
        if (boards.size() > 0) {
            BoardData boardData = boards.get(0).getData();
            GeneralInformation generalInformation = new GeneralInformation();
            generalInformation.setEmail(boardData.getEmail());
            generalInformation.setPhone(school.getPhone());
            generalInformation.setStreetAddress(school.getOfficialAddress().getStreetAddress());
            generalInformation.setLatLon(school.getOfficialAddress().getLatLon());
            generalInformation.setOfficialWebsiteLink(getOfficialWebsiteLinkFromData(boardData));
            generalInformation.setOfficialName(school.getOfficialName());
            generalInformation.setShortName(school.getShortName());
            final String logoUrl = school.getGallery().getLogo();
            generalInformation.setLogo(
                    StringUtils.isNotBlank(logoUrl)
                            ?
                            CommonUtils.addCDNPrefixAndEncode(logoUrl) : ""
            );
            generalInformation.setCity(school.getOfficialAddress().getCity());
            generalInformation.setState(school.getOfficialAddress().getState());
            return generalInformation;
        }
        return null;
    }

    private String getOfficialWebsiteLinkFromData(BoardData boardData) {
        return boardData.getRelevantLinks()
                .stream()
                .filter(x -> OFFICIAL_WEBSITE_LINK.equals(x.getRelevantLinkType()))
                .map(RelevantLink::getRelevantLinkUrl)
                .findFirst()
                .orElse(null);
    }
}
