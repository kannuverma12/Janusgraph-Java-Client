package com.paytm.digital.education.coaching.service.impl;

import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.coaching.database.entity.CoachingInstitute;
import com.paytm.digital.education.coaching.database.entity.Media;
import com.paytm.digital.education.coaching.database.repository.CoachingInstituteRepository;
import com.paytm.digital.education.coaching.googlesheet.model.GalleryForm;
import com.paytm.digital.education.coaching.service.helper.IngestDataHelper;
import com.paytm.digital.education.config.GoogleConfig;
import com.paytm.digital.education.database.entity.FailedData;
import com.paytm.digital.education.database.repository.FailedDataRepository;
import com.paytm.digital.education.utility.GoogleDriveUtil;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COMPONENT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.FAILED_MEDIA;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.GALLERY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.GALLERY_SHEET_HEADER_RANGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.GALLERY_SHEET_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.GALLERY_SHEET_RANGE_TEMPLATE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.GALLERY_SHEET_START_ROW;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.HAS_IMPORTED;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.IMAGE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.IS_IMPORTABLE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.S3_UPLOAD_FAILED;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.TYPE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.VIDEO;

@AllArgsConstructor
@Service
public class ImportGalleryService {
    private IngestDataHelper            ingestDataHelper;
    private CoachingInstituteRepository coachingInstituteRepository;
    private FailedDataRepository        failedDataRepository;

    public boolean importData()
            throws IOException, GeneralSecurityException {
        Map<String, Object> propertyMap = ingestDataHelper.getDataIngestionProperties();
        String sheetId = (String) propertyMap.get(GALLERY_SHEET_ID);
        String headerRange = (String) propertyMap.get(GALLERY_SHEET_HEADER_RANGE);
        double startRow = (double) propertyMap.get(GALLERY_SHEET_START_ROW);
        String dataRangeTemplate = (String) propertyMap.get(GALLERY_SHEET_RANGE_TEMPLATE);
        List<Object> gallerySheetData = GoogleDriveUtil.getDataFromSheet(sheetId,
                MessageFormat.format(dataRangeTemplate, startRow), headerRange,
                GoogleConfig.getCoachingCredentialFileName(),
                GoogleConfig.getCoachingCredentialFolderPath());
        List<Long> instituteIds = new ArrayList<>();
        List<GalleryForm> galleryFormSheetData = new ArrayList<>();
        List<Object> failedDataList = new ArrayList<>();
        List<Media> previousFailedGalleryData = getAllFailedData(instituteIds);
        if (Objects.nonNull(gallerySheetData)) {
            galleryFormSheetData = gallerySheetData.stream()
                    .map(e2 -> JsonUtils.convertValue(e2, GalleryForm.class))
                    .peek(galleryForm -> instituteIds
                            .add(galleryForm.getInstituteId()))
                    .collect(Collectors.toList());
        }
        Map<Long, CoachingInstitute> instituteMap = new HashMap<>();
        if (!instituteIds.isEmpty()) {
            // Optimization required
            List<CoachingInstitute> existingInstitutes =
                    coachingInstituteRepository.findAllCoachingInstitutes(instituteIds);
            instituteMap = existingInstitutes.stream()
                    .collect(Collectors.toMap(c -> c.getInstituteId(), c -> c));
        }
        if (!previousFailedGalleryData.isEmpty()) {
            reimportFailedGalleryData(previousFailedGalleryData, instituteMap, failedDataList);
            ingestDataHelper.updateReimportStatus(GALLERY, COACHING);
        }
        if (!galleryFormSheetData.isEmpty()) {
            buildInstituteGalleryMap(galleryFormSheetData, instituteMap,
                    failedDataList);
            ingestDataHelper.saveCoachingInstitutes(instituteMap);
        }
        if (!failedDataList.isEmpty()) {
            failedDataRepository.saveAll(failedDataList);
        }
        //Update the next read row no. of excel in property map
        ingestDataHelper.updatePropertyMap(GALLERY_SHEET_START_ROW, gallerySheetData, startRow);
        return true;
    }


    private void buildInstituteGalleryMap(
            List<GalleryForm> sheetDataList, Map<Long, CoachingInstitute> instituteMap,
            List<Object> failedDataList) {
        for (GalleryForm sheetData : sheetDataList) {
            Media media = new Media();
            BeanUtils.copyProperties(sheetData, media);
            if (StringUtils.isNotBlank(sheetData.getMediaFiles())) {
                Long instituteId = media.getInstituteId();
                if (Objects.nonNull(instituteId) && Objects
                        .nonNull(instituteMap.get(instituteId))) {
                    List<String> mediaUrl = Arrays.asList(sheetData.getMediaFiles().split(", "));
                    boolean isMediaEmpty = setMediaFields(mediaUrl, media.getInstituteId(), media);
                    if (!isMediaEmpty) {
                        List<Media> gallery = instituteMap.get(instituteId).getGallery();
                        if (Objects.isNull(gallery)) {
                            gallery = new ArrayList<>();
                        }
                        gallery.add(media);
                        instituteMap.get(instituteId).setGallery(gallery);
                    }
                    if (Objects.nonNull(media.getFailedMedia())) {
                        ingestDataHelper.addToFailedList(media, S3_UPLOAD_FAILED, true,
                                failedDataList, COACHING, GALLERY);
                    }
                } else {
                    // Failure Cases Handling (Invalid InstituteId)
                    ingestDataHelper.addToFailedList(media, "InstituteId is empty or invalid",
                            false,
                            failedDataList, COACHING, GALLERY);
                }
            } else {
                ingestDataHelper.addToFailedList(media, "Media Field is mandatory.", false,
                        failedDataList, COACHING, GALLERY);
            }

        }
    }

    /*
     ** Set the media fields of the campus event model and return response if all the media
     * uploaded failed(true) else false
     */
    private boolean setMediaFields(List<String> mediaUrlList, long instituteId, Media media) {
        Map<String, List<String>> mediaMap = ingestDataHelper.getMediaUrl(mediaUrlList,
                instituteId, CoachingConstants.S3RelativePath.GALLERY);
        boolean isMediaEmpty = true;
        if (Objects.nonNull(mediaMap)) {
            if (Objects.nonNull(mediaMap.get(IMAGE))) {
                isMediaEmpty = false;
                media.setImages(mediaMap.get(IMAGE));
            }
            if (Objects.nonNull(mediaMap.get(VIDEO))) {
                isMediaEmpty = false;
                media.setVideos(mediaMap.get(VIDEO));
            }
            if (Objects.nonNull(FAILED_MEDIA)) {
                media.setFailedMedia(mediaMap.get(FAILED_MEDIA));
            }
        }
        return isMediaEmpty;
    }

    private boolean reimportFailedGalleryData(List<Media> mediaList,
            Map<Long, CoachingInstitute> instituteMap, List<Object> failedDataList) {
        for (Media media : mediaList) {
            List<String> failedMedia = media.getFailedMedia();
            if (Objects.nonNull(failedMedia)) {
                media.setImages(null);
                media.setVideos(null);
                media.setFailedMedia(null);
                Long instituteId = media.getInstituteId();
                boolean isMediaEmpty = setMediaFields(failedMedia, instituteId, media);
                if (!isMediaEmpty) {
                    List<Media> gallery = instituteMap.get(instituteId).getGallery();
                    if (Objects.isNull(gallery)) {
                        gallery = new ArrayList<>();
                    }
                    gallery.add(media);
                    instituteMap.get(instituteId).setGallery(gallery);
                }
                if (Objects.nonNull(media.getFailedMedia())) {
                    ingestDataHelper.addToFailedList(media, "S3 upload failed", true,
                            failedDataList, COACHING, GALLERY);
                }
            }
        }
        return true;
    }


    private List<Media> getAllFailedData(List<Long> instituteIds) {
        Map<String, Object> queryObject = new HashMap<>();
        queryObject.put(COMPONENT, COACHING);
        queryObject.put(TYPE, GALLERY);
        queryObject.put(HAS_IMPORTED, false);
        queryObject.put(IS_IMPORTABLE, true);
        List<FailedData> failedGalleryData = failedDataRepository.findAll(queryObject);
        List<Media> mediaList =
                failedGalleryData.stream().map(c -> JsonUtils.convertValue(c.getData(),
                        Media.class)).peek(media -> instituteIds
                        .add(media.getInstituteId())).collect(Collectors.toList());
        return mediaList;
    }
}
