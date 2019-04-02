package com.paytm.digital.education.explore.constants;

import freemarker.template.Configuration;
import freemarker.template.Version;

import java.text.SimpleDateFormat;

public interface ExploreConstants {

    ////// Common constants /////////////
    String INSTITUTE_ID             = "institute_id";
    String EXAM_ID                  = "exam_id";
    String COURSE_ID                = "course_id";
    String ENTITY_TYPE              = "entity_type";
    int    DEFAULT_OFFSET           = 0;
    int    DEFAULT_SIZE             = 10;
    int    DEFAULT_AUTOSUGGEST_SIZE = 4;

    ///// Exam Detail//////////
    String TENTATIVE     = "tentative";
    String NON_TENTATIVE = "non_tentative";
    String APPLICATION   = "APPLICATION";
    String EXAM          = "EXAM";

    String GROUP_NAME   = "name";
    String GROUP_ENTITY = "entity";
    String GROUP_ACTIVE = "active";

    String EDUCATION_BASE_URL = "/explore";

    String EXPLORE_COMPONENT = "explore";

    ////// AutoSuggest Constants ////////////
    String AUTOSUGGEST_INDEX         = "education_autosuggestion_v1";
    String AUTOSUGGEST_ANALYZER      = "words_with_spaces_analyzer";
    int    AUTOSUGGEST_MIN_CHARS     = 3;
    int    AUTOSUGGEST_MAX_CHARS     = 200;
    int    SEARCH_REQUEST_MAX_OFFSET = 9950;
    int    SEARCH_REQUEST_MAX_LIMIT  = 50;
    String AUTOSUGGEST_OFFICIAL_NAME = "official_name";
    String AUTOSUGGEST_NAMES         = "names";
    String RESULT                    = "results";
    String ENTITY_TYPE_STATE         = "state";
    String ENTITY_TYPE_CITY          = "city";

    ///////// Institute search Constatnts ///////////
    String SEARCH_INDEX_INSTITUTE    = "education_search_institute_v1";
    String SEARCH_ANALYZER_INSTITUTE = "word_delimiter_analyzer";
    String SEARCH_INDEX_COURSE       = "education_search_course_v1";
    String SEARCH_ANALYZER_COURSE    = "word_delimiter_analyzer";
    String SEARCH_NAMES_INSTITUTE    = "names";
    String MAX_RANK_INSTITUTE        = "max_rank";
    String STATE_INSTITUTE           = "state";
    String CITY_INSTITUTE            = "city";
    String STREAM_INSTITUTE          = "domain_name";
    String COURSE_LEVEL_INSTITUTE    = "level";
    String EXAMS_ACCEPTED_INSTITUTE  = "exams";
    String FEES_INSTITUTE            = "fees";
    String EXAM_PREFIX               = "exam.";
    String INSTITUTE_GENDER          = "institute_gender";
    String ESTABLISHMENT_YEAR        = "year_of_estd";
    String OWNERSHIP                 = "ownership";
    String FACILITIES                = "facilities";
    String USER_UNAUTHORIZED_MESSAGE = "Unauthorized Request";
    String APPROVALS                 = "approvals";

    SimpleDateFormat MMM_YYYY    = new SimpleDateFormat("MMM,yyyy");
    SimpleDateFormat DD_MMM_YYYY = new SimpleDateFormat("dd MMM,yyyy");
    SimpleDateFormat YYYY_MM     = new SimpleDateFormat("yyyy-MM");

    ///////// Explore search Constatnts ///////////
    // Exam
    String SEARCH_INDEX_EXAM    = "education_search_exam_v1";
    String SEARCH_ANALYZER_EXAM = "word_delimiter_analyzer";
    String SEARCH_NAMES_EXAM    = "names";
    String DATE_TAB             = "dates";
    String SYLLABUS_TAB         = "syllabus";
    String SEARCH_INDEX         = "education_search_institute_v1";
    String SEARCH_ANALYZER      = "word_delimiter_analyzer";
    String SEARCH_NAMES         = "names";
    String MAX_RANK             = "max_rank";
    String OFFICIAL_NAME        = "official_name";
    String STATE                = "state";
    String CITY                 = "city";
    String INSTITUTION_STATE    = "institution_state";
    String INSTITUTION_CITY     = "institution_city";
    String STREAM               = "domain_name";
    String COURSE_LEVEL         = "level";
    String FEES                 = "fees";
    String COURSE_CLASS         = "Course";
    String EXAM_CLASS           = "Exam";
    String INSTITUTE_CLASS      = "Institute";

    String LINGUISTIC_MEDIUM_NAMESPACE = "linguistic_medium";
    String FACILITIES_NAMESPACE        = "facilities";
    String INSTITUTE_FILTER_NAMESPACE  = "institute_filter";
    String INSTITUTE_SEARCH_NAMESPACE  = "institute";
    String COURSE_FILTER_NAMESPACE     = "course_filter";
    String EXAM_FILTER_NAMESPACE       = "exam_filter";
    String EXAM_SEARCH_NAMESPACE       = "exam";
    String LINGUISTIC_MEDIUM           = "linguistic_medium";
    String SEARCH_EXAM_LEVEL           = "level";
    String IGNORE_VALUES               = "ignore";

    String STREAM_COURSE                    = "domain_name";
    String DEGREE_COURSE                    = "degree";
    String BRANCH_COURSE                    = "branch";
    String LEVEL_COURSE                     = "level";
    String INSTITUTE_NAME_COURSE            = "institute_official_name";
    String SEATS_COURSE                     = "seats";
    String FEE_COURSE                       = "fees";
    String DURATION_COURSE                  = "duration_in_months";
    String PARENT_INSTITUTE_ID_COURSE       = "parent_institute_id";
    String INSTITUTE_ID_COURSE              = "institute_id";
    String GALLERY_LOGO                     = "gallery.logo";
    String OFFICIAL_ADDRESS                 = "official_address";
    String ENTITY_ID                        = "entity_id";
    int    COURSE_SIZE_FOR_INSTITUTE_DETAIL = 6;

    // Detail APIs constants
    String EXAM_SHORT_NAME     = "exam_short_name";
    String EXAM_FULL_NAME      = "exam_full_name";
    String COURSE_PREFIX       = "course.";
    String INSTITUTE_PREFIX    = "institute.";
    String HIGHLIGHTS_TEMPLATE = "highlights";
    String OVERALL_RANKING     = "overall";

    String MAXIMUM_PACKAGE_LABEL = "Maximum Package";
    String MINIMUM_PACKAGE_LABEL = "Minimum Package";
    String AVERAGE_PACKAGE_LABEL = "Average Package";
    String MEDIAN_PACKAGE_LABEL  = "Median Package";
    String EVENT_TYPE_EXAM       = "EXAM";

    String DISPLAY_NAME              = "display_name";
    String LOGO                      = "logo";
    String SECTION_ORDER_NAMESPACE   = "section_order";
    String DETAIL_PAGE_SECTION_ORDER = "detail_page_section_order";
    String BANNER                    = "banner";
    String WIDGETS                   = "widgets";
    String DATA_STRING                   = "data";

    // Other Constants
    Version FTL_CURRENT_VERSION = Configuration.VERSION_2_3_23;
}
