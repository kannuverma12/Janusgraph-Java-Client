package com.paytm.digital.education.explore.constants;

import freemarker.template.Configuration;
import freemarker.template.Version;

import java.text.SimpleDateFormat;

public interface ExploreConstants {

    ////// Common constants /////////////

    String INSTITUTE_ID = "institute_id";
    String EXAM_ID      = "exam_id";
    String COURSE_ID    = "course_id";

    String ENTITY_TYPE = "entity_type";

    int DEFAULT_OFFSET = 0;
    int DEFAULT_SIZE   = 10;

    public static final int    DEFAULT_AUTOSUGGEST_SIZE  = 4;

    String GROUP_NAME   = "name";
    String GROUP_ENTITY   = "entity";
    String GROUP_ACTIVE   = "active";

    String EDUCATION_BASE_URL = "/explore";

    String EXPLORE_COMPONENT         = "explore";

    ////// AutoSuggest Constants ////////////

    String AUTOSUGGEST_INDEX    = "education_autosuggestion_v1";
    String AUTOSUGGEST_ANALYZER = "words_with_spaces_analyzer";

    int AUTOSUGGEST_MIN_CHARS   = 3;
    int AUTOSUGGEST_MAX_CHARS   = 200;

    int SEARCH_REQUEST_MAX_LIMIT = 50;

    String AUTOSUGGEST_OFFICIAL_NAME    = "official_name";
    String AUTOSUGGEST_NAMES    = "names";

    ///////// Explore search Constatnts ///////////

    String SEARCH_INDEX    = "education_search_institute_v1";
    String SEARCH_ANALYZER = "word_delimiter_analyzer";

    String SEARCH_NAMES       = "names";
    String MAX_RANK           = "max_rank";
    String STATE              = "state";
    String CITY               = "city";
    String STREAM             = "domain_name";
    String COURSE_LEVEL       = "level";
    String EXAMS_ACCEPTED     = "exams";
    String FEES               = "fees";
    String INSTITUTE_GENDER   = "institute_gender";
    String ESTABLISHMENT_YEAR = "year_of_estd";
    String OWNERSHIP          = "ownership";
    String FACILITIES         = "facilities";

    // Detail APIs constants

    String EXAM_SHORT_NAME = "exam_short_name";
    String COURSE_PREFIX   = "course.";
    String INSTITUTE_PREFIX   = "institute.";
    String EXAM_PREFIX   = "exam.";
    String HIGHLIGHTS_TEMPLATE   = "highlights";

    String MAXIMUM_PACKAGE_LABEL = "Maximum Package";
    String MINIMUM_PACKAGE_LABEL = "Minimum Package";
    String AVERAGE_PACKAGE_LABEL = "Average Package";
    String MEDIAN_PACKAGE_LABEL = "Median Package";

    String NON_TENTATIVE = "NON_TENTATIVE";
    String TENTATIVE = "TENTATIVE";
    String EVENT_TYPE_EXAM = "EXAM";

    String DISPLAY_NAME = "display_name";
    String LOGO = "logo";

    // Date formatter
    SimpleDateFormat YYYY_MM = new SimpleDateFormat("yyyy-MM");
    SimpleDateFormat DD_MMM_YYYY = new SimpleDateFormat("dd MMM,yyyy");
    SimpleDateFormat MMM_YYYY = new SimpleDateFormat("MMM,yyyy");

    //Other Constants
    Version FTL_CURRENT_VERSION = Configuration.VERSION_2_3_23;

    String USER_UNAUTHORIZED_MESSAGE = "Unauthorized Request";
}
