package com.paytm.digital.education.admin.service;

import com.paytm.digital.education.admin.request.AmbassadorRequest;
import com.paytm.digital.education.admin.request.ArticleRequest;
import com.paytm.digital.education.admin.request.EventRequest;
import com.paytm.digital.education.admin.response.CampusAdminResponse;
import com.paytm.digital.education.explore.xcel.model.XcelArticle;
import com.paytm.digital.education.explore.xcel.model.XcelCampusAmbassador;
import com.paytm.digital.education.explore.xcel.model.XcelEvent;

import java.util.List;

public interface CampusAdminService {

    CampusAdminResponse addAmbassadors(AmbassadorRequest campusAmbassador);

    List<XcelCampusAmbassador> getAllAmbassadors();

    CampusAdminResponse addArticles(ArticleRequest xcelArticle);

    List<XcelArticle> getAllArticles();

    CampusAdminResponse addEvents(EventRequest xcelEvent);

    List<XcelEvent> getAllEvents();
}
