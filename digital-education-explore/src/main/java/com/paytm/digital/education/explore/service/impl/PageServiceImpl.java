package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.constant.ErrorCode;
import com.paytm.digital.education.exception.ResourceNotFoundException;
import com.paytm.digital.education.explore.database.entity.Page;
import com.paytm.digital.education.explore.database.entity.Section;
import com.paytm.digital.education.explore.database.repository.PageRepository;
import com.paytm.digital.education.explore.database.repository.SectionRepository;
import com.paytm.digital.education.explore.service.PageService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;

@Service
@AllArgsConstructor
public class PageServiceImpl implements PageService {

    private PageRepository pageRepository;
    private SectionRepository sectionRepository;

    @Override
    @Cacheable(value = "page", key = "#pageName", unless = "#result == null ")
    public Map<String, Section> getPageSections(@NotBlank final String pageName) {
        Page page = pageRepository.getPageByName(pageName);

        if (page == null) {
            throw ResourceNotFoundException.builder()
                .errorCode(ErrorCode.DP_RESOURCE_NOT_FOUND)
                .resourceName(pageName).build();
        }

        final Collection<String> pageSectionNames = page.getSections().values();
        final List<Section> pageSections = sectionRepository.getSectionsByNameIn(pageSectionNames);

        final Map<String, Section> sectionsByName = pageSections.stream()
            .collect(Collectors.toMap(Section::getName, Function.identity()));

        // used to represent Section whose entry does exist in Page document
        // but that Section document doesn't exist
        // Note: it'll only be used in case of data mismatch (inconsistent data)
        // and UI folks demanded to send empty Section object in that case
        final Section dummySection = new Section();

        return page.getSections().entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                Section value = sectionsByName.get(entry.getValue());
                return value != null ? value : dummySection;
            }));
    }
}
