package com.paytm.digital.education.constant;

import java.text.SimpleDateFormat;

public interface CommonConstants {

    String COACHING_STREAMS                  = "coaching_streams";
    String TOP_COACHING_INSTITUTES           = "coaching_top_institutes";
    String TOP_COACHING_INSTITUTES_LOGO      = "coaching_top_institutes_logo";
    String TOP_COACHING_INSTITUTES_IMAGE     = "coaching_top_institutes_image";
    String COACHING_INSTITUTE_BROCHURE       = "coaching_institute_brochure";
    String COACHING_TOP_EXAMS                = "coaching_top_exams";
    String COACHING_TOP_COURSES              = "coaching_top_courses";
    String COACHING_COURSE_BROCHURE          = "coaching_course_brochure";
    String LOGO                              = "logo";
    String COACHING_COURSES                  = "coaching_courses";
    String COACHING_COURSE_TYPES             = "coaching_course_types";
    String COACHING_COURSE_FEATURE           = "coaching_course_feature";
    String COACHING_TOP_RANKER               = "coaching_top_ranker";
    String COACHING_BANNER                   = "coaching_banner";
    String COACHING_CENTER                   = "coaching_center";
    String COACHING_INSTITUTE_HIGHLIGHT_LOGO = "coaching_institute_highlight_logo";
    String SECTION                           = "section";
    String STREAMS                           = "streams";

    //AutoSuggest constants
    String AUTOSUGGEST_INDEX    = "education_autosuggestion_v2";
    String AUTOSUGGEST_ANALYZER = "words_with_spaces_analyzer";
    String AUTOSUGGEST_NAMES    = "names";
    String ENTITY_TYPE_STATE    = "state";
    String ENTITY_TYPE_CITY     = "city";
    String ENTITY_TYPE          = "entity_type";
    int    DEFAULT_OFFSET       = 0;
    String OFFICIAL_NAME        = "official_name";

    String APPLICATION            = "APPLICATION";
    String EXPLORE_COMPONENT      = "explore";
    String EXAM_SEARCH_NAMESPACE  = "exam";
    String DATES                  = "dates";
    String EVENT_TYPE_EXAM        = "EXAM";
    String OTHER_CATEGORIES       = "OTHERS";
    String EXAM_DEGREES           = "exam_degrees";
    String EXAM_CUTOFF_GENDER     = "exam_cutoff_gender";
    String EXAM_CUTOFF_CASTEGROUP = "exam_cutoff_castegroup";
    String ZERO                   = "0";


    SimpleDateFormat MMM_YYYY    = new SimpleDateFormat("MMM,yyyy");
    SimpleDateFormat DD_MMM_YYYY = new SimpleDateFormat("dd MMM,yyyy");
    SimpleDateFormat YYYY_MM     = new SimpleDateFormat("yyyy-MM");
    String REDIS_LOCK_POSTFIX   = "::redisLock";

    String CUSTOMER_ID      = "CUSTOMERID";
    String EMAIL_ID         = "EMAIL";
    String CLIENT_ID        = "client_id";
    String SECRET_KEY       = "secret_key";
    String CONTENT_TYPE     = "content-type";
    String APPLICATION_JSON = "application/json";
    String NO_REPLY_EMAIL   = "no-reply@paytm.com";

}
