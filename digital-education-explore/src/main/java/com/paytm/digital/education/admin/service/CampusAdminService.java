package com.paytm.digital.education.admin.service;

import com.paytm.digital.education.admin.response.CampusAdminResponse;
import com.paytm.digital.education.explore.xcel.model.XcelArticle;
import com.paytm.digital.education.explore.xcel.model.XcelCampusAmbassador;
import com.paytm.digital.education.explore.xcel.model.XcelEvent;

import java.util.List;

public interface CampusAdminService {

    CampusAdminResponse addAmbassadors(XcelCampusAmbassador campusAmbassador);

    List<XcelCampusAmbassador> getAllAmbassadors();

    CampusAdminResponse addArticles(XcelArticle xcelArticle);

    List<XcelArticle> getAllArticles();

    CampusAdminResponse addEvents(XcelEvent xcelEvent);

    List<XcelEvent> getAllEvents();
}
