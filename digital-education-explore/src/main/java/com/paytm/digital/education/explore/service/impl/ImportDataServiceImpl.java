package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.database.entity.CampusAmbassador;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.service.ImportDataService;
import com.paytm.digital.education.explore.utility.CommonUtil;
import com.paytm.digital.education.explore.utility.GoogleDrive;
import com.paytm.digital.education.explore.xcel.model.XcelCampusAmbassador;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.mongodb.QueryOperators.OR;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.CAMPUS_AMBASSADORS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;

@Slf4j
@Service
@AllArgsConstructor
public class ImportDataServiceImpl implements ImportDataService {

    private CommonMongoRepository commonMongoRepository;

    public Map<String, Object> importCampusEngagementData(MultipartFile file)
            throws IOException, GeneralSecurityException {
        Map<String, Object> campusEngagementData = CommonUtil.readDataFromExcel(file);
        GoogleDrive.downloadFile(true);
        //        List<XcelCampusAmbassador> xcelAmbassadorData =
        //                (List<XcelCampusAmbassador>) campusEngagementData.get(CAMPUS_AMBASSADORS);
        //        if (!xcelAmbassadorData.isEmpty()) {
        //            Map<Long, List<CampusAmbassador>> campusAmbassadorInstituteMap =
        //                    getCampusAmbassadorInstituteMap(xcelAmbassadorData);
        //        }
        return campusEngagementData;
    }

    public List<Institute> getInstituteDetails(List<Long> instituteIdList) {
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(INSTITUTE_ID, instituteIdList);
        List<String> instituteFields = Arrays.asList(INSTITUTE_ID, "campus_ambassadors");
        List<Institute> institutes = commonMongoRepository.findAll(queryObject, Institute.class,
                instituteFields, OR);
        return institutes;
    }

    public Map<Long, List<CampusAmbassador>> getCampusAmbassadorInstituteMap(
            List<XcelCampusAmbassador> xcelAmbassadorData) {
        Map<Long, List<CampusAmbassador>> campusAmbassadorsInstituteMap = new HashMap<>();
        for (XcelCampusAmbassador ambassador : xcelAmbassadorData) {
            CampusAmbassador campusAmbassador = new CampusAmbassador();
            campusAmbassador.setName(ambassador.getName());
            campusAmbassador.setCourse(ambassador.getCourse());
            campusAmbassador.setImageUrl(ambassador.getImage());
            Long instituteId = Long.parseLong(ambassador.getInstituteId());
            campusAmbassador.setInstituteId(instituteId);
            campusAmbassador.setPaytmMobileNumber(ambassador.getPaytmMobileNumber());
            campusAmbassador.setYearAndBatch(ambassador.getYearAndBatch());
            campusAmbassador.setCreatedAt(ambassador.getTimestamp());
            List<CampusAmbassador> instituteCampusAmbassasor =
                    campusAmbassadorsInstituteMap.get(campusAmbassador.getInstituteId());
            if (Objects.isNull(instituteCampusAmbassasor)) {
                instituteCampusAmbassasor = new ArrayList<>();
            }
            instituteCampusAmbassasor.add(campusAmbassador);
            campusAmbassadorsInstituteMap.put(instituteId, instituteCampusAmbassasor);
        }
        return campusAmbassadorsInstituteMap;
    }
}
