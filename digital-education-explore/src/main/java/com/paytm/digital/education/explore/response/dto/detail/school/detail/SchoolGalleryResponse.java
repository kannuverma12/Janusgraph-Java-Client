package com.paytm.digital.education.explore.response.dto.detail.school.detail;

import com.paytm.digital.education.explore.database.entity.SchoolGallery;
import com.paytm.digital.education.utility.CommonUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;

import static com.paytm.digital.education.explore.constants.ExploreConstants.ASSET_CDN_PREFIX;
import static com.paytm.digital.education.utility.CommonUtils.encodeUrl;

public class SchoolGalleryResponse extends SchoolGallery {

    public SchoolGalleryResponse(SchoolGallery schoolGallery) {
        super(schoolGallery.getImages(), schoolGallery.getVideos(), schoolGallery.getLogo());
        this.setImages(
                this.getImages()
                        .stream()
                        .map(x -> ASSET_CDN_PREFIX + x)
                        .map(CommonUtils::encodeUrl)
                        .collect(Collectors.toList())

        );
        if (StringUtils.isNotBlank(this.getLogo())) {
            this.setLogo(encodeUrl(ASSET_CDN_PREFIX + this.getLogo()));
        }
    }
}
