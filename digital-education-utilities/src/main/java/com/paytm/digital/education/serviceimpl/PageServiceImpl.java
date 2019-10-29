package com.paytm.digital.education.serviceimpl;

import static com.paytm.digital.education.constant.ExploreConstants.APP_DISPLAY_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.STREAM_IDS;
import static com.paytm.digital.education.constant.ExploreConstants.NAME;
import static com.paytm.digital.education.mapping.ErrorEnum.PAYTM_STREAM_DISABLED;

import com.paytm.digital.education.constant.ErrorCode;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.exception.ResourceNotFoundException;
import com.paytm.digital.education.database.entity.Page;
import com.paytm.digital.education.database.entity.Section;
import com.paytm.digital.education.database.repository.PageRepository;
import com.paytm.digital.education.database.repository.SectionRepository;
import com.paytm.digital.education.ingestion.dao.StreamDAO;
import com.paytm.digital.education.service.PageService;
import com.paytm.digital.education.utility.CommonUtil;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.paytm.digital.education.constant.ExploreConstants.APP_FOOTER;
import static com.paytm.digital.education.constant.ExploreConstants.BANNER_MID;
import static com.paytm.digital.education.constant.ExploreConstants.BROWSE_BY_EXAM_LEVEL;
import static com.paytm.digital.education.constant.ExploreConstants.CAROUSEL;
import static com.paytm.digital.education.constant.ExploreConstants.COLLEGE_FOCUS;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_FOCUS_APP;
import static com.paytm.digital.education.constant.ExploreConstants.ICON;
import static com.paytm.digital.education.constant.ExploreConstants.IMAGE_URL;
import static com.paytm.digital.education.constant.ExploreConstants.LOCATIONS;
import static com.paytm.digital.education.constant.ExploreConstants.LOGO;
import static com.paytm.digital.education.constant.ExploreConstants.POPULAR_EXAMS_APP;
import static com.paytm.digital.education.constant.ExploreConstants.SCHOOLS_IN_FOCUS;
import static com.paytm.digital.education.constant.ExploreConstants.STREAMS;
import static com.paytm.digital.education.constant.ExploreConstants.SUB_ITEMS;
import static com.paytm.digital.education.constant.ExploreConstants.TOP_COLLEGES;
import static com.paytm.digital.education.constant.ExploreConstants.TOP_EXAMS_APP;
import static com.paytm.digital.education.constant.ExploreConstants.TOP_SCHOOLS;

@Service
@AllArgsConstructor
public class PageServiceImpl implements PageService {

    private PageRepository    pageRepository;
    private SectionRepository sectionRepository;
    private StreamDAO         streamDAO;

    @Override
    @Cacheable(value = "page", key = "#pageName", unless = "#result == null ")
    public List<Section> getPageSections(@NotBlank final String pageName) {
        Page page = pageRepository.getPageByName(pageName);

        if (page == null) {
            throw ResourceNotFoundException.builder()
                    .errorCode(ErrorCode.DP_RESOURCE_NOT_FOUND)
                    .resourceName(pageName).build();
        }

        final Collection<String> pageSectionNames = page.getSections();
        final List<Section> pageSections = sectionRepository.getSectionsByNameIn(pageSectionNames);

        final Map<String, Section> sectionsByName = pageSections.stream()
                .collect(Collectors.toMap(Section::getName, Function.identity()));

        List<Section> response = new ArrayList<>();
        for (String sectionName : pageSectionNames) {
            Section section = sectionsByName.get(sectionName);
            if (section != null && section.getType() != null) {
                String logoFieldName = null;
                switch (section.getType()) {
                    case TOP_EXAMS_APP:
                    case COLLEGE_FOCUS:
                    case EXAM_FOCUS_APP:
                    case BROWSE_BY_EXAM_LEVEL:
                    case SCHOOLS_IN_FOCUS:
                        logoFieldName = LOGO;
                        break;
                    case STREAMS:
                    case TOP_COLLEGES:
                    case TOP_SCHOOLS:
                    case LOCATIONS:
                    case APP_FOOTER:
                    case POPULAR_EXAMS_APP:
                        logoFieldName = ICON;
                        break;
                    case BANNER_MID:
                    case CAROUSEL:
                        logoFieldName = IMAGE_URL;
                        break;
                    default:
                }
                if (Objects.nonNull(logoFieldName) && !CollectionUtils
                        .isEmpty(section.getItems())) {
                    for (Map<String, Object> item : section.getItems()) {
                        if (TOP_EXAMS_APP.equals(section.getType()) || BROWSE_BY_EXAM_LEVEL
                                .equals(section.getType())) {
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
                        } else {
                            String logo =
                                    CommonUtil.getAbsoluteUrl((String) item.get(logoFieldName),
                                            section.getType());
                            item.put(logoFieldName, logo);
                        }
                    }
                }
                if (STREAMS.equals(section.getType())) {
                    updatePaytmStreamData(section);
                }
                response.add(section);
            }
        }
        return response;
    }

    private void updatePaytmStreamData(Section section) {
        if (Objects.nonNull(section) && !CollectionUtils.isEmpty(section.getItems())) {
            Map<Long, StreamEntity> streamEntityMap = streamDAO.getStreamEntityMapById();
            for (Map<String, Object> item : section.getItems()) {
                Long streamId = ((Integer) item.get(STREAM_IDS)).longValue();
                StreamEntity streamEntity = streamEntityMap.get(streamId);
                if (Objects.nonNull(streamEntity) && streamEntity.getIsEnabled()) {
                    item.put(STREAM_IDS, streamId);
                    item.put(NAME, streamEntity.getName());
                    item.put(APP_DISPLAY_NAME, streamEntity.getShortName());
                    item.put(ICON, streamEntity.getLogo());
                } else {
                    throw new EducationException(PAYTM_STREAM_DISABLED,
                            PAYTM_STREAM_DISABLED.getExternalMessage(), new Object[] {streamId});
                }
            }
        }
    }
}

