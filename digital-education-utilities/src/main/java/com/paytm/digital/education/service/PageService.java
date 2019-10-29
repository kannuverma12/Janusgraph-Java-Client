package com.paytm.digital.education.service;

import com.paytm.digital.education.database.entity.Section;

import java.util.List;

public interface PageService {
    List<Section> getPageSections(String pageName);
}
