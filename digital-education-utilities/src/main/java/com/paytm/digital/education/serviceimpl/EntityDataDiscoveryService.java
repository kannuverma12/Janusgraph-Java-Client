package com.paytm.digital.education.serviceimpl;

import static com.paytm.digital.education.constant.ExploreConstants.APP_DISPLAY_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.ICON;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.constant.ExploreConstants.LOGO;
import static com.paytm.digital.education.constant.ExploreConstants.NAME;
import static com.paytm.digital.education.constant.ExploreConstants.STREAM_IDS;
import static com.paytm.digital.education.constant.ExploreConstants.SUB_ITEMS;
import static com.paytm.digital.education.constant.ExploreConstants.URL_DISPLAY_KEY;
import static com.paytm.digital.education.enums.EducationEntity.INSTITUTE;
import static com.paytm.digital.education.mapping.ErrorEnum.PAYTM_STREAM_DISABLED;

import com.paytm.digital.education.database.dao.StreamDAO;
import com.paytm.digital.education.database.entity.Institute;
import com.paytm.digital.education.database.entity.Section;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EntityDataDiscoveryService {

    private final CommonMongoRepository commonMongoRepository;
    private final StreamDAO             streamDAO;

    private static final List<String> INSTITUTE_PROJECTION_FIELDS = Arrays.asList();
    private static final Logger log = LoggerFactory.getLogger(EntityDataDiscoveryService.class);

    public Section updateInstituteData(Section section) {
        List<Map<String, Object>> sectionItems = section.getItems();
        List<Map<String, Object>> resultItems = new ArrayList<>();
        if (!CollectionUtils.isEmpty(sectionItems)) {
            List<Long> instituteIds = sectionItems.stream().map(item->(Long)item.get(INSTITUTE_ID)).collect(
                    Collectors.toList());
            Map<Long, Institute> instituteMap = getInstituteMap(instituteIds);
            for (Map<String, Object> item : sectionItems) {
                if (instituteMap.containsKey(item.get(INSTITUTE_ID))) {
                    Institute institute = instituteMap.get(item.get(INSTITUTE_ID));
                    item.put(NAME, institute.getOfficialName());
                    item.put(URL_DISPLAY_KEY, CommonUtil.convertNameToUrlDisplayName(institute.getOfficialName()));
                    item.put(LOGO, CommonUtil.getLogoLink(institute.getGallery().getLogo(), INSTITUTE));
                    item.put(ICON, CommonUtil.getLogoLink(institute.getGallery().getLogo(), INSTITUTE));
                    resultItems.add(item);
                } else {
                    log.error("Institute Id : {} of College in focus not found in the databse.", item.get(INSTITUTE_ID));
                }
            }
            section.setItems(resultItems);
        }
        return section;
    }

    public Section updatePaytmStreamData(Section section) {
        if (Objects.nonNull(section) && !CollectionUtils.isEmpty(section.getItems())) {
            Map<Long, StreamEntity> streamEntityMap = streamDAO.getStreamEntityMapById();
            for (Map<String, Object> item : section.getItems()) {
                Long streamId = ((Integer) item.get(STREAM_IDS)).longValue();
                StreamEntity streamEntity = streamEntityMap.get(streamId);
                if (Objects.nonNull(streamEntity) && streamEntity.getIsEnabled()) {
                    item.put(STREAM_IDS, streamId);
                    item.put(NAME, streamEntity.getName());
                    item.put(APP_DISPLAY_NAME, streamEntity.getShortName());
                    item.put(ICON,
                            CommonUtil.getAbsoluteUrl(streamEntity.getLogo(), section.getType()));
                } else {
                    throw new EducationException(PAYTM_STREAM_DISABLED,
                            PAYTM_STREAM_DISABLED.getExternalMessage(), new Object[] {streamId});
                }
            }
            return section;
        }
        return null;
    }

    public Section updateTopExamsData(Section section) {
        if (!CollectionUtils.isEmpty(section.getItems())) {
            section.getItems().stream().flatMap()

            for (Map<String, Object> item : section.getItems()) {
                for (Map.Entry<String, Object> topExamsPerLevel : item.entrySet()) {
                    Map<String, Object> subitems =
                            (Map<String, Object>) topExamsPerLevel.getValue();
                    List<Map<String, String>> topExams =
                            (List<Map<String, String>>) subitems.get(SUB_ITEMS);
                    for (Map<String, String> topExam : topExams) {
                        String logo = CommonUtil
                                .getAbsoluteUrl(topExam.get(logoFieldName),
                                        section.getType());
                        topExam.put(logoFieldName, logo);
                    }
                }
            }
        }


    }

    private Map<Long, Institute> getInstituteMap(List<Long> instituteIds) {
        List<Institute> instituteList = commonMongoRepository
                .getEntityFieldsByValuesIn(INSTITUTE_ID, instituteIds, Institute.class,
                        INSTITUTE_PROJECTION_FIELDS);
        return Optional.ofNullable(instituteList).orElse(new ArrayList<>()).stream()
                .collect(Collectors.toMap(Institute::getInstituteId, Function
                        .identity()));
    }
}
