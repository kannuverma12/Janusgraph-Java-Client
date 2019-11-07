package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.config.AwsConfig;
import com.paytm.digital.education.database.entity.Board;
import com.paytm.digital.education.database.entity.BoardData;
import com.paytm.digital.education.database.entity.School;
import com.paytm.digital.education.database.entity.SchoolGallery;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.enums.ClassType;
import com.paytm.digital.education.enums.SchoolEducationLevelType;
import com.paytm.digital.education.enums.SchoolEntityType;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.explore.constants.AWSConstants;
import com.paytm.digital.education.explore.dto.SchoolDto;
import com.paytm.digital.education.explore.service.helper.IncrementalDataHelper;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.digital.education.utility.UploadUtil;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.paytm.digital.education.database.entity.PaytmKeys.Constants.PAYTM_KEYS;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.S3_IMAGE_PATH_SUFFIX;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.SCHOOL_ENTITY;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.SCHOOL_FILE_VERSION;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_ID;

@Service
@AllArgsConstructor

public class TransformSchoolService {

    private static Logger log = LoggerFactory.getLogger(CommonMongoRepository.class);

    private CommonMongoRepository commonMongoRepository;
    private IncrementalDataHelper incrementalDataHelper;
    private UploadUtil            uploadUtil;

    public Integer transformAndSaveSchoolsData(List<SchoolDto> schoolDtos) throws
            EducationException {
        log.info("Transforming Schools.");
        try {
            List<Long> schoolIds =
                    schoolDtos.stream().map(SchoolDto::getId).collect(Collectors.toList());
            List<School> schoolsFromDb =
                    commonMongoRepository.getEntityFieldsByValuesIn(
                            SCHOOL_ID, schoolIds, School.class, null);
            Map<Long, School> schoolIdToSchoolMap = schoolsFromDb.stream()
                    .collect(Collectors.toMap(School::getSchoolId, Function.identity()));

            String[] ignorableKeys = {PAYTM_KEYS};
            log.info("Transforming Schools.");
            List<School> schoolEntities = schoolDtos
                    .stream()
                    .map(schoolDto ->
                            convertRequestDtoToDbEntity(
                                    schoolDto,
                                    schoolIdToSchoolMap.getOrDefault(schoolDto.getId(), new School()),
                                    ignorableKeys))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());

            log.info("Saving schools to db.");
            for (School school : schoolEntities) {

                uploadImagestoS3(school.getGallery(), school.getSchoolId());

                commonMongoRepository.saveOrUpdate(school);
            }
            log.info("Saved schools to db.");
            log.info("Updating version number for schools");
            incrementalDataHelper.incrementFileVersion(SCHOOL_FILE_VERSION);

            return schoolsFromDb.size();
        } catch (Exception e) {
            log.info("Schools ingestion exception : " + e.getMessage());
            throw new BadRequestException(ErrorEnum.CORRUPTED_FILE,
                    ErrorEnum.CORRUPTED_FILE.getExternalMessage());

        }

    }

    private Optional<School> convertRequestDtoToDbEntity(
            SchoolDto schoolDto, School schoolEntity, String[] ignorableKeys) {
        log.info("Found school document from dump for id : {}, {}", schoolDto.getId(),
                JsonUtils.toJson(schoolDto));
        List<Board> boardsSupportedBySchoolList = schoolDto.getBoardList();
        if (CollectionUtils.isEmpty(boardsSupportedBySchoolList)) {
            log.error("Board information is missing for school with entityId : {} , skipping",
                    schoolDto.getId());
            return Optional.empty();
        }

        schoolEntity.setOfficialName(schoolDto.getName());
        schoolEntity.setSchoolId(schoolDto.getId());
        return Optional.of(convert(schoolDto, schoolEntity, ignorableKeys));
    }

    private School convert(SchoolDto schoolDto, School schoolEntity, String[] ignorableKeys) {
        List<Board> boardsSupportedBySchoolList = schoolDto.getBoardList();
        BeanUtils.copyProperties(schoolDto, schoolEntity, ignorableKeys);

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

        return schoolEntity;
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
            if (Objects.nonNull(logoName)) {
                String logoS3Url = uploadUtil.uploadImage(logoUrl, logoName,
                        entityId, s3BucketName,
                        AWSConstants.S3_RELATIVE_PATH_FOR_EXPLORE);

                if (Objects.nonNull(logoS3Url)) {
                    gallery.setLogo("/" + logoS3Url);
                }
            }
        }
        log.info("Images uploaded successfully for school id {}", entityId);
    }

    private String getImageName(String url) {
        if (Objects.nonNull(url)) {
            String[] arr = url.split("/");
            return arr[arr.length - 1];
        }
        return null;
    }

}
