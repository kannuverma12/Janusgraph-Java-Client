package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.explore.database.entity.Section;

import java.util.Map;

public interface PageService {
    Map<String, Section> getPageSections(String pageName);
}
