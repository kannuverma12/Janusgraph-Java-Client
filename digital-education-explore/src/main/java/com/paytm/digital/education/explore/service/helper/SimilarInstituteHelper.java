package com.paytm.digital.education.explore.service.helper;

import com.paytm.digital.education.database.entity.Institute;
import com.paytm.digital.education.database.repository.CommonEntityMongoDAO;
import com.paytm.digital.education.dto.OfficialAddress;
import com.paytm.digital.education.explore.response.dto.common.Widget;
import com.paytm.digital.education.explore.response.dto.common.WidgetData;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
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

import static com.paytm.digital.education.constant.ExploreConstants.GALLERY_LOGO;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTION_CITY;
import static com.paytm.digital.education.constant.ExploreConstants.INSTITUTION_STATE;
import static com.paytm.digital.education.constant.ExploreConstants.OFFICIAL_NAME;
import static com.paytm.digital.education.enums.EducationEntity.INSTITUTE;

@Service
@AllArgsConstructor
public class SimilarInstituteHelper {

    private static final Logger       log              =
            LoggerFactory.getLogger(SimilarInstituteHelper.class);
    private static final List<String> projectionFields =
            Arrays.asList(INSTITUTE_ID, OFFICIAL_NAME, INSTITUTION_STATE, INSTITUTION_CITY,
                    GALLERY_LOGO);

    private final WidgetsDataHelper     widgetsDataHelper;
    private final CommonEntityMongoDAO  commonEducationEntityMongoRepository;

    @Cacheable(value = "similar_institutes_widgets", key = "'institute_id.'+#institute.instituteId")
    public List<Widget> getSimilarInstituteWigets(Institute institute) {
        List<Widget> widgetDataList = widgetsDataHelper
                .getWidgets(INSTITUTE.name().toLowerCase(), institute.getInstituteId());
        return buildSimilarInstituteWidgets(widgetDataList);
    }

    private List<Widget> buildSimilarInstituteWidgets(List<Widget> widgets) {
        if (!CollectionUtils.isEmpty(widgets)) {
            for (Widget widget : widgets) {
                List<Long> instituteIds =
                        widget.getData().stream().map(widgetData -> widgetData.getEntityId())
                                .collect(Collectors.toList());
                Map<Long, Institute> instituteDataMap = getInstituteDataMap(instituteIds);
                for (WidgetData widgetData : widget.getData()) {
                    if (instituteDataMap.containsKey(widgetData.getEntityId())) {
                        Institute institute = instituteDataMap.get(widgetData.getEntityId());
                        widgetData.setOfficialName(institute.getOfficialName());
                        if (Objects.nonNull(institute.getGallery()) && StringUtils
                                .isNotBlank(institute.getGallery().getLogo())) {
                            widgetData.setLogoUrl(CommonUtil
                                    .getLogoLink(institute.getGallery().getLogo(), INSTITUTE));
                        }
                        widgetData.setUrlDisplayKey(CommonUtil
                                .convertNameToUrlDisplayName(institute.getOfficialName()));
                        OfficialAddress address = new OfficialAddress();
                        address.setState(institute.getInstitutionState());
                        address.setCity(institute.getInstitutionCity());
                        widgetData.setOfficialAddress(address);
                    } else {
                        log.warn("InstituteId : {} not present in db for similar institute.",
                                widgetData.getEntityId());
                    }
                }
            }
        }
        return widgets;
    }

    private Map<Long, Institute> getInstituteDataMap(List<Long> instituteIds) {
        List<Institute> institutes = commonEducationEntityMongoRepository
                .getInstitutesByIdsIn(instituteIds, projectionFields);
        return Optional.ofNullable(institutes).orElse(new ArrayList<>()).stream()
                .collect(Collectors.toMap(Institute::getInstituteId, Function
                        .identity()));
    }
}
