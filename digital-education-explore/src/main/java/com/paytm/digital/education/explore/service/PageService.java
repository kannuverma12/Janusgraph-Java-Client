package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.explore.database.entity.Section;

import java.util.List;

public interface PageService {
    List<Section> getPageSections(String pageName);
}
