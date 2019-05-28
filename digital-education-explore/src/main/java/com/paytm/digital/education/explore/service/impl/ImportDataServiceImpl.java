package com.paytm.digital.education.explore.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paytm.digital.education.explore.database.entity.CampusAmbassador;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.service.ImportDataService;
import com.paytm.digital.education.explore.utility.GoogleDriveUtil;
import com.paytm.digital.education.explore.utility.S3Util;
import com.paytm.digital.education.explore.xcel.model.XcelCampusAmbassador;
import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.mongodb.QueryOperators.OR;
import static com.paytm.digital.education.explore.constants.AWSConstants.S3_BUCKET_PATH;
import static com.paytm.digital.education.explore.constants.AWSConstants.S3_PATH_FOR_AMBASSADOR;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.CAMPUS_AMBASSADOR_EXCEL_RANGE;
import static com.paytm.digital.education.explore.constants.CampusEngagementConstants.GOOGLE_DRIVE_BASE_URL;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;

@Slf4j
@Service
@AllArgsConstructor
public class ImportDataServiceImpl implements ImportDataService {

    private CommonMongoRepository commonMongoRepository;

    public Map<Long, List<CampusAmbassador>> importCampusEngagementData(MultipartFile file)
            throws IOException, GeneralSecurityException {
        String range = MessageFormat.format(CAMPUS_AMBASSADOR_EXCEL_RANGE, 1);
        String sheetId = "1bMCBAOkG2WAHytI376q1_QgQh1Hpq1iPB4oxjeDrH3c";
        List<Object> sheetAmbassadorData = GoogleDriveUtil.readGoogleSheet(sheetId, range);
        if (!sheetAmbassadorData.isEmpty()) {
            Map<Long, List<CampusAmbassador>> campusAmbassadorInstituteMap =
                    getCampusAmbassadorInstituteMap(sheetAmbassadorData);
            return campusAmbassadorInstituteMap;
        }
        return null;
    }

    public List<Institute> getInstituteDetails(List<Long> instituteIdList) {
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(INSTITUTE_ID, instituteIdList);
        List<String> instituteFields = Arrays.asList(INSTITUTE_ID, "campus_ambassadors");
        List<Institute> institutes = commonMongoRepository.findAll(queryObject, Institute.class,
                instituteFields, OR);
        return institutes;
    }

    public Map<Long, List<CampusAmbassador>> getCampusAmbassadorInstituteMap (
            List<Object> xcelAmbassadorData) throws IOException,
            GeneralSecurityException{
        Map<Long, List<CampusAmbassador>> campusAmbassadorsInstituteMap = new HashMap<>();
        for (Object object : xcelAmbassadorData) {
            ObjectMapper mapper = new ObjectMapper();
            XcelCampusAmbassador ambassador = mapper.convertValue(object, XcelCampusAmbassador.class);
            CampusAmbassador campusAmbassador = new CampusAmbassador();
            campusAmbassador.setName(ambassador.getName());
            campusAmbassador.setCourse(ambassador.getCourse());
            Long instituteId = Long.parseLong(ambassador.getInstituteId());
            campusAmbassador.setInstituteId(instituteId);
            if (ambassador.getImage() != null) {
                campusAmbassador.setImageUrl(uploadToS3(ambassador.getImage(), null, instituteId,
                        S3_BUCKET_PATH, S3_PATH_FOR_AMBASSADOR));
            }
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

    private String uploadToS3(String fileUrl, String fileName, Long instituteId,
            String s3bucketPath, String s3ImagePath) throws IOException,
            GeneralSecurityException {
        if (fileUrl.startsWith(GOOGLE_DRIVE_BASE_URL)) {
            Pair<String, InputStream> fileData = GoogleDriveUtil.downloadFile(true, fileUrl);
            return S3Util.uploadFile(null, fileData.getValue(), fileData.getKey(), s3bucketPath,
                    instituteId, s3ImagePath);
        } else {
            return S3Util.uploadFile(fileUrl, null, fileName, s3bucketPath, (long) 2, s3ImagePath);
        }
    }
}
