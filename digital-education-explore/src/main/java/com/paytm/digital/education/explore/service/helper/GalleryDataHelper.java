package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.utility.CustomStringUtils.splitAndConvertToCamelCase;

import com.paytm.digital.education.explore.response.dto.detail.Gallery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GalleryDataHelper {

    private static String GALLERY_SEPERATOR = "[-.,_]";

    @Value("${institute.gallery.image.prefix}")
    private String imageUrlPrefix;

    public Gallery getGalleryData(
            com.paytm.digital.education.explore.database.entity.Gallery galleryData) {
        Gallery response = new Gallery();
        if (galleryData != null) {
            if (!CollectionUtils.isEmpty(galleryData.getImages())) {
                Map<String, List<String>> imageMap = new HashMap<>();
                for (String key : galleryData.getImages().keySet()) {
                    List<String> urlList = galleryData.getImages().get(key).stream().map(url ->
                            imageUrlPrefix + url).collect(Collectors.toList());
                    imageMap.put(splitAndConvertToCamelCase(key, GALLERY_SEPERATOR),
                            urlList);
                }
                response.setImages(imageMap);
            }
        }
        return response;
    }
}
