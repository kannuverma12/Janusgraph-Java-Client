package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.config.AwsConfig;
import com.paytm.digital.education.database.entity.Alumni;
import com.paytm.digital.education.database.entity.Gallery;
import com.paytm.digital.education.database.entity.Institute;
import com.paytm.digital.education.database.entity.Ranking;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.explore.constants.AWSConstants;
import com.paytm.digital.education.explore.dto.InstituteDto;
import com.paytm.digital.education.explore.dto.RankingDto;
import com.paytm.digital.education.explore.service.helper.IncrementalDataHelper;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.digital.education.utility.CommonUtils;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.digital.education.utility.UploadUtil;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.INSTITUTE_FILE_VERSION;

@Service
@AllArgsConstructor
public class TransformInstituteService {

    private static Logger log = LoggerFactory.getLogger(TransformInstituteService.class);

    private UploadUtil            uploadUtil;
    private CommonMongoRepository commonMongoRepository;
    private IncrementalDataHelper incrementalDataHelper;

    public Integer transformAndSaveInstituteData(List<InstituteDto> dtos, Boolean versionUpdate)
            throws EducationException {
        List<Institute> institutes = transformInstituteDtos(dtos);

        try {
            Set<Long> ids =
                    institutes.stream().map(i -> i.getInstituteId()).collect(Collectors.toSet());

            List<Institute> dbInstitutes = new ArrayList<>();
            dbInstitutes = incrementalDataHelper
                    .getExistingData(Institute.class, INSTITUTE_ID, new ArrayList<>(ids));

            Map<Long, Institute> ojbIdToInstIdMap = dbInstitutes.stream()
                    .collect(Collectors.toMap(i -> i.getInstituteId(), i -> i));

            log.info("Saving to DB after transformation");
            for (Institute institute : institutes) {
                Long id = institute.getInstituteId();
                Institute existingInstitute = ojbIdToInstIdMap.get(id);
                if (Objects.nonNull(existingInstitute)) {
                    institute.setId(existingInstitute.getId());
                    institute.setPaytmKeys(existingInstitute.getPaytmKeys());
                }
                commonMongoRepository.saveOrUpdate(institute);
            }
            log.info("Saved in DB.");

            if (Objects.isNull(versionUpdate) || versionUpdate) {
                log.info("Updating version number for institute");
                incrementalDataHelper.incrementFileVersion(INSTITUTE_FILE_VERSION);
            }
            log.info("Institute data dump import done.");
            return institutes.size();
        } catch (Exception e) {
            log.info("Institute ingestion exceptions : " + e.getMessage());
            throw new BadRequestException(ErrorEnum.CORRUPTED_FILE,
                    ErrorEnum.CORRUPTED_FILE.getExternalMessage());
        }
    }

    public List<Institute> transformInstituteDtos(List<InstituteDto> dtos) {
        Stream.Builder<Institute> instituteStreamBuilder = Stream.builder();

        for (InstituteDto dto : dtos) {
            log.info("Institute Document from Dump for id : {}, {} ", dto.getInstituteId(),
                    JsonUtils.toJson(dto));
            Institute institute =
                    new Institute(dto.getCommonName(), Long.valueOf(dto.getInstituteId()));

            log.info("Transforming document to db entity for id : {}", dto.getInstituteId());
            BeanUtils.copyProperties(dto, institute);

            institute.setInstituteId(Long.valueOf(dto.getInstituteId()));
            institute.setStudentCount(dto.getTotalIntake());

            // rankings
            List<RankingDto> rankingDtos = dto.getRankings();
            List<Ranking> rankings = null;
            if (rankingDtos.size() > 0) {
                rankings = new ArrayList<>();
            }
            for (RankingDto rdto : rankingDtos) {
                Ranking ranking = new Ranking();
                BeanUtils.copyProperties(rdto, ranking);
                ranking.setStream(rdto.getRankingStream());
                rankings.add(ranking);
            }

            //notable alumni
            updateNotableAlumniDetails(institute);

            // S3 upload
            uploadImages(institute);

            institute.setRankings(rankings);
            log.info("Transformation done for id : {}", dto.getInstituteId());
            instituteStreamBuilder.accept(institute);
        }

        return instituteStreamBuilder.build()
                .filter(CommonUtils.distinctBy(Institute::getId))
                .collect(Collectors.toList());
    }

    private void updateNotableAlumniDetails(Institute institute) {
        List<Alumni> alumniList = institute.getNotableAlumni();

        if (Objects.nonNull(alumniList)) {
            log.info("Uploading images for Notable alumni.");
            for (Alumni alumni : alumniList) {
                if (StringUtils.isNotBlank(alumni.getAlumniPhoto())) {
                    String imageName = getImageName(alumni.getAlumniPhoto());
                    String imageUrl = uploadUtil.uploadImage(alumni.getAlumniPhoto(), imageName,
                            institute.getInstituteId(), AwsConfig.getS3ExploreBucketName(),
                            AWSConstants.S3_RELATIVE_PATH_FOR_EXPLORE);
                    if (Objects.nonNull(imageUrl)) {
                        alumni.setAlumniPhoto("/" + imageUrl);
                        log.info("Notable Alumni photo upload successful : {}",imageUrl);
                    } else {
                        log.info("Some issue with alumni picture");
                    }
                }
            }
        }
    }

    private void uploadImages(Institute institute) {
        log.info("Uploading images for institute id : {}", institute.getInstituteId());
        Gallery g = institute.getGallery();

        if (Objects.nonNull(g)) {
            Map<String, List<String>> images = g.getImages();
            if (Objects.nonNull(images)) {
                for (Map.Entry<String, List<String>> en : images.entrySet()) {
                    String key = en.getKey();
                    List<String> urls = en.getValue();
                    List<String> newUrls = new ArrayList<>();

                    for (String url : urls) {
                        String imageName = getImageName(url);
                        if (Objects.nonNull(imageName)) {
                            String imageUrl = uploadUtil.uploadImage(url, imageName,
                                    institute.getInstituteId(), AwsConfig.getS3ExploreBucketName(),
                                    AWSConstants.S3_RELATIVE_PATH_FOR_EXPLORE);
                            if (Objects.nonNull(imageUrl)) {
                                newUrls.add("/" + imageUrl);
                                log.info("Image upload successful : {}",imageName);
                            } else {
                                // TODO add fail over strategy
                                log.info("Image upload failed : {}",imageName);
                            }
                        } else {
                            log.info("Invalid/Null image for institute : {}",institute.getInstituteId());
                        }
                    }
                    images.put(key, newUrls);
                }
            }

            Map<String, List<String>> videos = g.getVideos();
            // TODO upload videos

            String logoUrl = g.getLogo();
            String logoName = getImageName(logoUrl);
            if (Objects.nonNull(logoName)) {
                String logoS3Url = uploadUtil.uploadImage(logoUrl, logoName,
                        institute.getInstituteId(), AwsConfig.getS3ExploreBucketName(),
                        AWSConstants.S3_RELATIVE_PATH_FOR_EXPLORE);

                if (Objects.nonNull(logoS3Url)) {
                    g.setLogo("/" + logoS3Url);
                    log.info("Image upload successful : {}",logoName);
                } else {
                    // TODO add fail over strategy
                    log.info("Image upload failed : {}",logoName);
                }
            } else {
                log.info("Invalid/Null logo for institute : {}",institute.getInstituteId());
            }
        }
        log.info("Images uploaded successfully for institute id {}", institute.getInstituteId());
    }

    private String getImageName(String url) {
        if (Objects.nonNull(url)) {
            String[] arr = url.split("/");
            return arr[arr.length - 1];
        }
        return null;
    }

}
