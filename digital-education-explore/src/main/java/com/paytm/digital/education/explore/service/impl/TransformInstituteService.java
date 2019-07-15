package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.config.AwsConfig;
import com.paytm.digital.education.explore.constants.AWSConstants;
import com.paytm.digital.education.explore.database.entity.Gallery;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.entity.Ranking;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.dto.InstituteDto;
import com.paytm.digital.education.explore.dto.RankingDto;
import com.paytm.digital.education.explore.service.helper.IncrementalDataHelper;
import com.paytm.digital.education.utility.UploadUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.Objects;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.paytm.digital.education.explore.constants.ExploreConstants.ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.EXAM_FILE_VERSION;
import static com.paytm.digital.education.explore.constants.IncrementalDataIngestionConstants.INSTITUTE_FILE_VERSION;

@Service
@AllArgsConstructor
@Slf4j
public class TransformInstituteService {
    private UploadUtil                 uploadUtil;
    private InstituteDetailServiceImpl instituteDetailService;
    private CommonMongoRepository      commonMongoRepository;
    private IncrementalDataHelper      incrementalDataHelper;

    public Integer transformAndSaveInstituteData(List<InstituteDto> dtos) {
        List<Institute> institutes = transformInstituteDtos(dtos);

        Set<Long> ids =
                institutes.stream().map(i -> i.getInstituteId()).collect(Collectors.toSet());

        List<String> fields = new ArrayList<>();
        fields.add(ID);
        fields.add(INSTITUTE_ID);
        List<Institute> dbIstitutes = new ArrayList<>();
        try {
            dbIstitutes = instituteDetailService.getInstitutes(new ArrayList<>(ids),fields);
        } catch (Exception e) {
            log.error("Error getting data : " + e.getMessage());
        }

        Map<Long, String> ojbIdToInstIdMap = dbIstitutes.stream()
                .collect(Collectors.toMap(i -> i.getInstituteId(), i -> i.getId()));

        for (Institute institute : institutes) {
            Long id = institute.getInstituteId();
            if (ojbIdToInstIdMap.containsKey(id)) {
                institute.setId(ojbIdToInstIdMap.get(id));
            }
            commonMongoRepository.saveOrUpdate(institute);
        }
        incrementalDataHelper.incrementFileVersion(INSTITUTE_FILE_VERSION);

        return institutes.size();
    }

    public List<Institute> transformInstituteDtos(List<InstituteDto> dtos) {
        List<Institute> institutes = new ArrayList<>();

        for (InstituteDto dto : dtos) {
            Institute institute = new Institute(dto.getCommonName(), Long.valueOf(dto.getInstituteId()));

            BeanUtils.copyProperties(dto, institute);

            institute.setInstituteId(Long.valueOf(dto.getInstituteId()));
            institute.setStudentCount(Integer.valueOf(dto.getTotalIntake()));

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

            // TODO check galleries, images, logos and notable allumni

            // S3 upload
            uploadImages(institute);

            institutes.add(institute);
        }

        return institutes;
    }

    private void uploadImages(Institute institute) {
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
                        String imageUrl = uploadUtil.uploadImage(url, imageName,
                                institute.getInstituteId(), AwsConfig.getS3ExploreBucketName(),
                                AWSConstants.S3_RELATIVE_PATH_FOR_EXPLORE);
                        if (Objects.nonNull(imageUrl)) {
                            newUrls.add(imageUrl);
                        } else {
                            // TODO add fail over strategy
                        }
                    }
                    images.put(key, newUrls);
                }
            }

            Map<String, List<String>> videos = g.getVideos();
            // TODO upload videos

            String logoUrl = g.getLogo();
            String logoName = getImageName(logoUrl);
            String logoS3Url = uploadUtil.uploadImage(logoUrl, logoName,
                    institute.getInstituteId(), AwsConfig.getS3ExploreBucketName(),
                    AWSConstants.S3_RELATIVE_PATH_FOR_EXPLORE);

            if (Objects.nonNull(logoS3Url)) {
                g.setLogo(logoS3Url);
            } else {
                // TODO add fail over strategy
            }
        }
    }

    private String getImageName(String url) {
        String[] arr = url.split("/");
        return arr[arr.length - 1];
    }

}
