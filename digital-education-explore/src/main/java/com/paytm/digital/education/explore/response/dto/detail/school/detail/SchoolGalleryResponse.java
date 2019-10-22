package com.paytm.digital.education.explore.response.dto.detail.school.detail;

import com.paytm.digital.education.explore.database.entity.SchoolGallery;
import com.paytm.digital.education.explore.utility.SchoolUtilService;
import com.paytm.digital.education.utility.CommonUtils;

import java.util.stream.Collectors;

public class SchoolGalleryResponse extends SchoolGallery {

    public SchoolGalleryResponse(SchoolGallery schoolGallery, SchoolUtilService schoolUtilService) {
        super();
        this.setVideos(schoolGallery.getVideos());
        this.setImages(
                schoolGallery.getImages()
                        .stream()
                        .map(CommonUtils::addCDNPrefixAndEncode)
                        .collect(Collectors.toList())

        );
        this.setLogo(schoolUtilService.buildLogoFullPathFromRelativePath(schoolGallery.getLogo()));
    }
}
