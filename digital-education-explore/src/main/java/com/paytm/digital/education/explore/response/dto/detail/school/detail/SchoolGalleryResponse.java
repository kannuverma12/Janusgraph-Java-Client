package com.paytm.digital.education.explore.response.dto.detail.school.detail;

import com.paytm.digital.education.explore.database.entity.SchoolGallery;
import com.paytm.digital.education.utility.CommonUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;

public class SchoolGalleryResponse extends SchoolGallery {

    public SchoolGalleryResponse(SchoolGallery schoolGallery) {
        super(schoolGallery.getImages(), schoolGallery.getVideos(), schoolGallery.getLogo());
        this.setImages(
                this.getImages()
                        .stream()
                        .map(CommonUtils::addCDNPrefixAndEncode)
                        .collect(Collectors.toList())

        );
        if (StringUtils.isNotBlank(this.getLogo())) {
            this.setLogo(CommonUtils.addCDNPrefixAndEncode(this.getLogo()));
        }
    }
}
