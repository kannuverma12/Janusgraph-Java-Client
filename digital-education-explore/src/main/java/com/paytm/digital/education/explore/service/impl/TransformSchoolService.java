package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.S3_IMAGE_PATH_SUFFIX;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.SCHOOL_ENTITY;

import com.paytm.digital.education.config.AwsConfig;
import com.paytm.digital.education.explore.constants.AWSConstants;
import com.paytm.digital.education.explore.database.entity.Board;
import com.paytm.digital.education.explore.database.entity.BoardData;
import com.paytm.digital.education.explore.database.entity.School;
import com.paytm.digital.education.explore.database.entity.SchoolGallery;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.dto.SchoolDto;
import com.paytm.digital.education.explore.enums.ClassType;
import com.paytm.digital.education.explore.enums.SchoolEducationLevelType;
import com.paytm.digital.education.explore.enums.SchoolEntityType;
import com.paytm.digital.education.explore.service.helper.IncrementalDataHelper;
import com.paytm.digital.education.utility.UploadUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.paytm.digital.education.explore.constants.ExploreConstants.ID;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_ID;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.SCHOOL_FILE_VERSION;

@Service
@AllArgsConstructor
@Slf4j
public class TransformSchoolService {

    private SchoolDetailServiceImpl schoolDetailService;
    private CommonMongoRepository   commonMongoRepository;
    private IncrementalDataHelper   incrementalDataHelper;
    private UploadUtil              uploadUtil;

    public Integer transformAndSaveSchoolsData(List<SchoolDto> schoolDtos) {

        List<School> schoolEntities = transformSchoolDtoToMongoEntity(schoolDtos);

        Set<Long> schoolIds =
                schoolEntities.stream().map(s -> s.getSchoolId()).collect(Collectors.toSet());

        List<String> fields = new ArrayList<>();
        fields.add(ID);
        fields.add(SCHOOL_ID);
        List<School> schoolsFromDb =
                schoolDetailService.getSchools(new ArrayList<>(schoolIds), fields);

        if (Objects.isNull(schoolsFromDb)) {
            schoolsFromDb = new ArrayList<>();
        }

        Map<Long, String> objectIdAndSchoolIdMap = schoolsFromDb.stream()
                .collect(Collectors.toMap(s -> s.getSchoolId(), s -> s.getId()));

        for (School school : schoolEntities) {
            Long id = school.getSchoolId();
            if (objectIdAndSchoolIdMap.containsKey(id)) {
                school.setId(objectIdAndSchoolIdMap.get(id));
            }

            uploadImagestoS3(school.getGallery(), school.getSchoolId());

            commonMongoRepository.saveOrUpdate(school);
        }
        incrementalDataHelper.incrementFileVersion(SCHOOL_FILE_VERSION);

        return schoolsFromDb.size();
    }

    private List<School> transformSchoolDtoToMongoEntity(List<SchoolDto> schoolDtos) {
        List<School> schoolEntitiesList = new ArrayList<>();
        log.info("Found {} no. of schools", schoolDtos.size());

        for (SchoolDto schoolDto : schoolDtos) {

            School schoolEntity = new School(schoolDto.getName(), schoolDto.getId());

            BeanUtils.copyProperties(schoolDto, schoolEntity);

            schoolEntity.setSchoolId(Long.valueOf(schoolDto.getId()));

            List<Board> boardsSupportedBySchoolList = schoolDto.getBoardList();

            if (CollectionUtils.isEmpty(boardsSupportedBySchoolList)) {
                log.error("Board information is missing for school with entityId : {} , skipping",
                        schoolDto.getId());
                continue;
            }

            for (Board boardAffiliatedBySchool : boardsSupportedBySchoolList) {
                BoardData boardAffiliatedBySchoolData = boardAffiliatedBySchool.getData();
                SchoolEducationLevelType schoolEducationLevel =
                        boardAffiliatedBySchoolData.getEducationLevel();

                if (Objects.nonNull(boardAffiliatedBySchoolData)
                        && ClassType.NOT_PROVIDED.equals(boardAffiliatedBySchoolData.getClassTo())
                        && Objects.nonNull(schoolEducationLevel)) {
                    boardAffiliatedBySchoolData.setClassFrom(ClassType.NURSERY);
                    switch (schoolEducationLevel) {
                        case SECONDARY:
                            boardAffiliatedBySchoolData.setClassTo(ClassType.TEN);
                            break;
                        case SENIOR_SECONDARY:
                            boardAffiliatedBySchoolData.setClassTo(ClassType.TWELVE);
                            break;
                        case MIDDLE:
                            boardAffiliatedBySchoolData.setClassTo(ClassType.EIGHT);
                            break;
                        case PRIMARY:
                            boardAffiliatedBySchoolData.setClassTo(ClassType.FIVE);
                            break;
                        default:
                            log.error("Invalid education level type : {}", schoolEducationLevel);
                            break;
                    }
                }
            }

            if (boardsSupportedBySchoolList.size() > 1) {
                schoolEntity.setSchoolEntityType(SchoolEntityType.MULTI_BOARD);
            }



            schoolEntitiesList.add(schoolEntity);

        }
        return schoolEntitiesList;
    }

    private void uploadImagestoS3(SchoolGallery gallery, Long entityId) {
        log.info("Uploading images for school id : {}", entityId);

        if (Objects.nonNull(gallery)) {
            List<String> images = gallery.getImages();
            String s3BucketName = AwsConfig.getS3ExploreBucketNameWithoutSuffix()
                    + "/"
                    + SCHOOL_ENTITY
                    + S3_IMAGE_PATH_SUFFIX;
            List<String> newUrls = new ArrayList<>();

            if (Objects.nonNull(images)) {
                for (String url : images) {
                    String imageName = getImageName(url);
                    String imageUrl = uploadUtil.uploadImage(url, imageName,
                            entityId, s3BucketName,
                            AWSConstants.S3_RELATIVE_PATH_FOR_EXPLORE);
                    if (Objects.nonNull(imageUrl)) {
                        newUrls.add("/" + imageUrl);
                    }
                }
            }
            gallery.setImages(newUrls);
            String logoUrl = gallery.getLogo();
            String logoName = getImageName(logoUrl);
            String logoS3Url = uploadUtil.uploadImage(logoUrl, logoName,
                    entityId, s3BucketName,
                    AWSConstants.S3_RELATIVE_PATH_FOR_EXPLORE);

            if (Objects.nonNull(logoS3Url)) {
                gallery.setLogo("/" + logoS3Url);
            }
        }
        log.info("Images uploaded successfully for school id {}", entityId);
    }

    private String getImageName(String url) {
        String[] arr = url.split("/");
        return arr[arr.length - 1];
    }

}
