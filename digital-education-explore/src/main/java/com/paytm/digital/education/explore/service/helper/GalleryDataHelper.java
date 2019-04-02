package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.utility.CustomStringUtils.splitAndConvertToCamelCase;

import com.paytm.digital.education.explore.response.dto.detail.Gallery;
import com.paytm.digital.education.explore.utility.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GalleryDataHelper {

    private static String GALLERY_SEPERATOR = "[-.,_]";

    @Cacheable(value = "gallery", key = "#instituteId")
    public Gallery getGalleryData(long instituteId,
            com.paytm.digital.education.explore.database.entity.Gallery galleryData) {
        Gallery response = new Gallery();
        if (galleryData != null) {
            if (!CollectionUtils.isEmpty(galleryData.getImages())) {
                Map<String, List<String>> imageMap = new HashMap<>();
                for (String key : galleryData.getImages().keySet()) {
                    List<String> urlList = new ArrayList<>(galleryData.getImages().get(key).stream()
                            .filter(url -> StringUtils.isNotBlank(url))
                            .map(url -> CommonUtil.getLogoLink(url)).collect(Collectors.toSet()));
                    if (!CollectionUtils.isEmpty(urlList)) {
                        imageMap.put(splitAndConvertToCamelCase(key, GALLERY_SEPERATOR),
                                urlList);
                    }
                }
                response.setImages(imageMap);
            }
        }
        return response;
    }
}
