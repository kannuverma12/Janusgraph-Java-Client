package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.config.AwsConfig;
import com.paytm.digital.education.explore.constants.AWSConstants;
import com.paytm.digital.education.explore.database.entity.Gallery;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.entity.Ranking;
import com.paytm.digital.education.explore.dto.InstituteDto;
import com.paytm.digital.education.explore.dto.RankingDto;
import com.paytm.digital.education.utility.UploadUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Map;

@Service
@AllArgsConstructor
public class TransformInstituteService {
    private UploadUtil uploadUtil;

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