package com.paytm.digital.education.explore.constants;

import freemarker.template.Configuration;
import freemarker.template.Version;
import java.text.SimpleDateFormat;

public interface ExploreConstants {

    ////// Common constants /////////////
    String           INSTITUTE_ID              = "institute_id";
    String           EXAM_ID                   = "exam_id";
    String           COURSE_ID                 = "course_id";
    String           ENTITY_TYPE               = "entity_type";
    int              DEFAULT_OFFSET            = 0;
    int              DEFAULT_SIZE              = 10;
    int              DEFAULT_AUTOSUGGEST_SIZE  = 4;

    ///// Exam Detail//////////
    String           TENTATIVE                 = "tentative";
    String           NON_TENTATIVE             = "non_tentative";
    String           APPLICATION               = "APPLICATION";
    String           EXAM                      = "EXAM";

    String           GROUP_NAME                = "name";
    String           GROUP_ENTITY              = "entity";
    String           GROUP_ACTIVE              = "active";

    String           EDUCATION_BASE_URL        = "/explore";

    String           EXPLORE_COMPONENT         = "explore";

    ////// AutoSuggest Constants ////////////
    String           AUTOSUGGEST_INDEX         = "education_autosuggestion_v1";
    String           AUTOSUGGEST_ANALYZER      = "words_with_spaces_analyzer";
    int              AUTOSUGGEST_MIN_CHARS     = 3;
    int              AUTOSUGGEST_MAX_CHARS     = 200;
    int              SEARCH_REQUEST_MAX_OFFSET = 50;
    int              SEARCH_REQUEST_MAX_LIMIT  = 50;
    String           AUTOSUGGEST_OFFICIAL_NAME = "official_name";
    String           AUTOSUGGEST_NAMES         = "names";
    String           RESULT                    = "results";

    ///////// Institute search Constatnts ///////////
    String           SEARCH_INDEX_INSTITUTE    = "education_search_institute_v1";
    String           SEARCH_ANALYZER_INSTITUTE = "word_delimiter_analyzer";
    String           SEARCH_NAMES_INSTITUTE    = "names";
    String           MAX_RANK_INSTITUTE        = "max_rank";
    String           STATE_INSTITUTE           = "state";
    String           CITY_INSTITUTE            = "city";
    String           STREAM_INSTITUTE          = "domain_name";
    String           COURSE_LEVEL_INSTITUTE    = "level";
    String           EXAMS_ACCEPTED_INSTITUTE  = "exams";
    String           FEES_INSTITUTE            = "fees";
    String           EXAM_PREFIX               = "exam.";
    String           INSTITUTE_GENDER          = "institute_gender";
    String           ESTABLISHMENT_YEAR        = "year_of_estd";
    String           OWNERSHIP                 = "ownership";
    String           FACILITIES                = "facilities";
    String           USER_UNAUTHORIZED_MESSAGE = "Unauthorized Request";

    SimpleDateFormat MMM_YYYY                  = new SimpleDateFormat("MMM,yyyy");
    SimpleDateFormat DD_MMM_YYYY               = new SimpleDateFormat("dd MMM,yyyy");
    SimpleDateFormat YYYY_MM                   = new SimpleDateFormat("yyyy-MM");
    SimpleDateFormat DD_MMMM_YYYY              = new SimpleDateFormat("dd MMMM,yyyy");
    SimpleDateFormat MMMM_YYYY                 = new SimpleDateFormat("MMMM,yyyy");

    ///////// Explore search Constatnts ///////////
    // Exam
    String           SEARCH_INDEX_EXAM         = "education_search_exam_v2";
    String           SEARCH_ANALYZER_EXAM      = "word_delimiter_analyzer";
    String           SEARCH_NAMES_EXAM         = "names";
    String           DATE_TAB                  = "dates";
    String           SYLLABUS_TAB              = "syllabus";
    String           SEARCH_INDEX              = "education_search_institute_v1";
    String           SEARCH_ANALYZER           = "word_delimiter_analyzer";
    String           SEARCH_NAMES              = "names";
    String           MAX_RANK                  = "max_rank";
    String           STATE                     = "state";
    String           CITY                      = "city";
    String           STREAM                    = "domain_name";
    String           COURSE_LEVEL              = "level";
    String           EXAMS_ACCEPTED            = "exams";
    String           FEES                      = "fees";

    String           LINGUISTIC_MEDIUM         = "linguistic_medium";
    String           SEARCH_EXAM_LEVEL         = "level";

    // Detail APIs constants
    String           EXAM_SHORT_NAME           = "exam_short_name";
    String           COURSE_PREFIX             = "course.";
    String           INSTITUTE_PREFIX          = "institute.";
    String           HIGHLIGHTS_TEMPLATE       = "highlights";

    String           MAXIMUM_PACKAGE_LABEL     = "Maximum Package";
    String           MINIMUM_PACKAGE_LABEL     = "Minimum Package";
    String           AVERAGE_PACKAGE_LABEL     = "Average Package";
    String           MEDIAN_PACKAGE_LABEL      = "Median Package";
    String           EVENT_TYPE_EXAM           = "EXAM";

    String           DISPLAY_NAME              = "display_name";
    String           LOGO                      = "logo";

    // Other Constants
    Version          FTL_CURRENT_VERSION       = Configuration.VERSION_2_3_23;

}
