package com.paytm.digital.education.coaching.service.helper;

import com.paytm.digital.education.coaching.database.entity.CoachingInstitute;
import com.paytm.digital.education.coaching.database.repository.CoachingInstituteRepository;
import com.paytm.digital.education.coaching.database.repository.CommonCoachingMongoRepository;
import com.paytm.digital.education.config.AwsConfig;
import com.paytm.digital.education.config.GoogleConfig;
import com.paytm.digital.education.database.entity.FailedData;
import com.paytm.digital.education.database.entity.Properties;
import com.paytm.digital.education.database.repository.FailedDataRepository;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.utility.DateUtil;
import com.paytm.digital.education.utility.UploadUtil;
import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.ATTRIBUTES;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COMPONENT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COMPONENT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DATA_INGEST;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.FAILED_MEDIA;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.GOOGLE_SHEETS_INFO;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.HAS_IMPORTED;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.IMAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TYPE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.VIDEO;
import static com.paytm.digital.education.constant.DBConstants.KEY;
import static com.paytm.digital.education.constant.DBConstants.NAMESPACE;

@Slf4j
@Service
@AllArgsConstructor
public class IngestDataHelper {

    private PropertyReader                propertyReader;
    private FailedDataRepository          failedDataRepository;
    private CoachingInstituteRepository   coachingInstituteRepository;
    private UploadUtil                    uploadUtil;
    private CommonCoachingMongoRepository commonCoachingMongoRepository;


    public Map<String, Object> getDataIngestionProperties() {
        return propertyReader
                .getPropertiesAsMapByKey(COACHING_COMPONENT, GOOGLE_SHEETS_INFO, DATA_INGEST);
    }

    public void updateReimportStatus(String type, String component) {
        Update update = new Update();
        update.set(HAS_IMPORTED, true);
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(HAS_IMPORTED, false);
        queryObject.put(COMPONENT, component);
        queryObject.put(TYPE, type);
        List<String> projectionFields = Arrays.asList(HAS_IMPORTED);
        failedDataRepository.updateMulti(queryObject, projectionFields, update);
    }

    public void saveCoachingInstitutes(Map<Long, CoachingInstitute> instituteMap) {
        for (Map.Entry<Long, CoachingInstitute> entry : instituteMap.entrySet()) {
            coachingInstituteRepository.upsertCoaching(entry.getValue());
        }
    }

    public Map<String, List<String>> getMediaUrl(List<String> mediaUrlList, Long instituteId,
            String s3RelativePath) {
        Map<String, List<String>> mediaMap = new HashMap<>();
        List<String> imageUrlList = new ArrayList<>();
        List<String> videoUrlList = new ArrayList<>();
        List<String> failedUrlList = new ArrayList<>();
        for (String url : mediaUrlList) {
            // need to think about the relative path
            Pair<String, String> mediaInfo = uploadUtil
                    .uploadFile(url, null, instituteId, s3RelativePath,
                            AwsConfig.getS3CoachingBucketName(),
                            GoogleConfig.getCoachingCredentialFileName(),
                            GoogleConfig.getCoachingCredentialFolderPath());
            if (Objects.nonNull(mediaInfo.getKey())) {
                if (mediaInfo.getValue().startsWith(IMAGE)) {
                    imageUrlList.add(mediaInfo.getKey());
                } else {
                    videoUrlList.add(mediaInfo.getKey());
                }
            } else {
                failedUrlList.add(url);
            }
        }
        if (!imageUrlList.isEmpty()) {
            mediaMap.put(IMAGE, imageUrlList);
        }
        if (!videoUrlList.isEmpty()) {
            mediaMap.put(VIDEO, videoUrlList);
        }
        if (!failedUrlList.isEmpty()) {
            mediaMap.put(FAILED_MEDIA, failedUrlList);
        }
        return mediaMap;
    }

    public void addToFailedList(Object object, String message,
            boolean isImportable, List<Object> failedDataList, String component, String type) {
        FailedData failedData = new FailedData();
        failedData.setComponent(component);
        failedData.setHasImported(false);
        failedData.setType(type);
        failedData.setMessage(message);
        failedData.setIsImportable(isImportable);
        failedData.setFailedDate(DateUtil.getCurrentDate());
        failedData.setData(object);
        failedDataList.add(failedData);
    }

    public void updatePropertyMap(String key, List<Object> sheetData, double startRow) {
        if (Objects.nonNull(sheetData)) {
            double totalNumberOfData = sheetData.size();
            double updatedCount = startRow;
            if (totalNumberOfData > 0) {
                updatedCount += totalNumberOfData;
                Map<String, Object> queryObject = new HashMap<>();
                queryObject.put(COMPONENT, COACHING_COMPONENT);
                queryObject.put(NAMESPACE, GOOGLE_SHEETS_INFO);
                queryObject.put(KEY, DATA_INGEST);
                List<String> fields = Arrays.asList(ATTRIBUTES);
                Update update = new Update();
                update.set(ATTRIBUTES + "." + key, updatedCount);
                commonCoachingMongoRepository
                        .updateFirst(queryObject, fields, update, Properties.class);
            }
        }
    }
}
