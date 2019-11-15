package com.paytm.digital.education.constant;

import freemarker.template.Configuration;
import freemarker.template.Version;

import java.text.SimpleDateFormat;

public interface ExploreConstants {

    ////// Common constants /////////////
    String INSTITUTE_ID                = "institute_id";
    String EXAM_ID                     = "exam_id";
    String COURSE_ID                   = "course_id";
    String IS_ACCEPTING_APPLICATION    = "is_accepting_applications";
    String ENTITY_TYPE                 = "entity_type";
    String IS_CLIENT                   = "is_client";
    int    DEFAULT_OFFSET              = 0;
    int    DEFAULT_SIZE                = 10;
    int    DEFAULT_AUTOSUGGEST_SIZE    = 3;
    int    DEFAULT_AUTOSUGGEST_COMPARE = 10;
    String SORT_DISTANCE_FIELD         = "location";
    String SPACE_SEPERATOR             = " ";
    String DISTANCE_KILOMETERS         = "Km";

    ///// Exam Detail//////////
    String TENTATIVE     = "tentative";
    String NON_TENTATIVE = "non_tentative";
    String APPLICATION   = "APPLICATION";
    String EXAM          = "EXAM";
    String PRECEDENCE    = "precedence";
    String DEFAULT       = "DEFAULT";

    String GROUP_NAME   = "name";
    String GROUP_ENTITY = "entity";
    String GROUP_ACTIVE = "active";
    String SUB_ITEMS    = "subitems";
    String DATES        = "dates";

    String EDUCATION_BASE_URL = "/explore";

    String EXPLORE_COMPONENT = "explore";

    String COURSE_DETAIL = "course_detail";
    String CASTE_GROUPS  = "caste_groups";

    ////// AutoSuggest Constants ////////////
    String AUTOSUGGEST_INDEX         = "education_autosuggestion_v2";
    String AUTOSUGGEST_ANALYZER      = "words_with_spaces_analyzer";
    int    AUTOSUGGEST_MIN_CHARS     = 3;
    int    AUTOSUGGEST_MAX_CHARS     = 200;
    int    SEARCH_REQUEST_MAX_OFFSET = 9950;
    int    SEARCH_REQUEST_MAX_LIMIT  = 500;
    String AUTOSUGGEST_OFFICIAL_NAME = "official_name";
    String AUTOSUGGEST_NAMES         = "names";
    String RESULT                    = "results";
    String ENTITY_TYPE_STATE         = "state";
    String ENTITY_TYPE_CITY          = "city";

    ///////// Institute search Constatnts ///////////
    String SEARCH_INDEX_COURSE        = "education_search_course_v4";
    String SEARCH_ANALYZER_COURSE     = "word_delimiter_analyzer";
    String SEARCH_NAMES_INSTITUTE     = "names";
    String MAX_RANK_INSTITUTE         = "max_rank";
    String STATE_INSTITUTE            = "state";
    String CITY_INSTITUTE             = "city";
    String STREAM_INSTITUTE           = "domain_name";
    String COURSE_LEVEL_INSTITUTE     = "level";
    String EXAMS_ACCEPTED_INSTITUTE   = "exams";
    String FEES_INSTITUTE             = "fees";
    String EXAM_PREFIX                = "exam.";
    String INSTITUTE_GENDER           = "institute_gender";
    String UNIVERSITY_NAME            = "university_name.keyword";
    String ESTABLISHMENT_YEAR         = "year_of_estd";
    String OWNERSHIP                  = "ownership";
    String FACILITIES                 = "facilities";
    String USER_UNAUTHORIZED_MESSAGE  = "Unauthorized Request";
    String APPROVALS                  = "approvals";
    String OFFICIAL_NAME_SEARCH       = "official_name.search";
    String FORMER_NAME                = "former_name";
    String COMMON_NAME                = "common_name";
    String ALTERNATE_NAMES            = "alternate_names";
    String UNIVERSITY_NAME_SEARCH     = "university_name";
    String OTHER_NAMES_NGRAM          = "names_ngram";
    String CITY_SEARCH                = "city.search";
    String STATE_SEARCH               = "state.search";
    String INSTITUTE_SEARCH_NAMESPACE = "institute_search";
    String SORT_PARAM_KEY             = "sort_params";
    String STOPWORDS_KEY              = "stop_words";
    String STOPWORDS                  = "stop_words";
    String NGRAM                      = ".ngram";
    String INSTANCES                  = "instances";
    String SUB_EXAMS                  = "subexams";

    Float  TIE_BREAKER                  = 0.4f;
    Float  CITY_SEARCH_BOOST            = 1F;
    Float  STATE_SEARCH_BOOST           = 1F;
    Float  OFFICIAL_NAME_SEARCH_BOOST   = 1F;
    Float  FORMER_NAME_BOOST            = 1F;
    Float  C0MMON_NAME_BOOST            = 1F;
    Float  ALTERNATE_NAMES_BOOST        = 1F;
    Float  UNIVERSITY_NAME_SEARCH_BOOST = 1F;
    Float  OTHER_NAMES_NGRAM_BOOST      = 0.00001F;
    String FE_RANK_SORT                 = "ranking";
    String FE_RELEVANCE_SORT            = "relevance";
    String DB_RANK_OVERALL              = "overall";

    SimpleDateFormat MMM_YYYY    = new SimpleDateFormat("MMM,yyyy");
    SimpleDateFormat DD_MMM_YYYY = new SimpleDateFormat("dd MMM,yyyy");
    SimpleDateFormat YYYY_MM     = new SimpleDateFormat("yyyy-MM");

    //Classifier
    String CLASSIFIER_ANALYZER      = "word_delimiter_analyzer";
    String CLASSIFIER_INDEX         = "education_search_dictionary_v2";
    String CLASSIFIER_KEYWORD       = "keyword";
    String CLASSIFIER_ENTITY        = "entity";
    String CLASSIFIER_ENTITY_PATH   = "";
    String CLASSIFIER_NAMESPACE     = "institute_search";
    String CLASSIFIER_KEY           = "streams";
    String CLASSIFIER_CURATED_FIELD = "streams";
    Float  CLASSIFIER_KEYWORD_BOOST = 1F;


    ///////// Explore search Constatnts ///////////
    // Exam
    String SEARCH_INDEX_EXAM              = "education_search_exam_v4";
    String SEARCH_ANALYZER_EXAM           = "word_delimiter_analyzer_search";
    String SEARCH_NAMES_EXAM              = "names";
    String EXAM_FULL_NAME_SEARCH          = "exam_full_name";
    String EXAM_SHORT_NAME_SEARCH         = "exam_short_name";
    String EXAM_OFFICIAL_NAME             = "official_name";
    String EXAM_OFFICIAL_NAME_NGRAM       = "official_name.raw";
    String EXAM_NAME_SYNONYMS             = "exam_name_synonyms";
    String EXAM_DOMAIN_NAME               = "domain_name";
    Float  EXAM_FULL_NAME_BOOST           = 1F;
    Float  EXAM_SHORT_NAME_BOOST          = 1F;
    Float  EXAM_OFFICIAL_NAME_BOOST       = 1F;
    Float  EXAM_NAME_SYNONYMS_BOOST       = 1F;
    Float  EXAM_OFFICIAL_NAME_NGRAM_BOOST = 0.00001F;
    String DATE_TAB                       = "dates";
    String SYLLABUS_TAB                   = "syllabus";
    String SEARCH_INDEX_INSTITUTE         = "education_search_institute_v4";
    String SEARCH_ANALYZER_INSTITUTE      = "word_delimiter_analyzer";
    String SEARCH_NAMES                   = "names";
    String MAX_RANK                       = "max_rank";
    String OFFICIAL_NAME                  = "official_name";
    String STATE                          = "state";
    String CITY                           = "city";
    String INSTITUTION_STATE              = "institution_state";
    String INSTITUTION_CITY               = "institution_city";
    String STREAM                         = "domain_name";
    String COURSE_LEVEL                   = "level";
    String FEES                           = "fees";
    String COURSE_CLASS                   = "Course";
    String EXAM_CLASS                     = "Exam";
    String INSTITUTE_CLASS                = "Institute";
    String DATA                           = "data";
    String KEY                            = "key";


    String LINGUISTIC_MEDIUM_NAMESPACE = "linguistic_medium";
    String FACILITIES_NAMESPACE        = "facilities";
    String INSTITUTE_FILTER_NAMESPACE  = "institute_filter";
    String INSTITUTE_NAMESPACE         = "institute_search";
    String COURSE_FILTER_NAMESPACE     = "course_filter";
    String EXAM_FILTER_NAMESPACE       = "exam_filter";
    String EXAM_SEARCH_NAMESPACE       = "exam";
    String LINGUISTIC_MEDIUM           = "linguistic_medium";
    String SEARCH_EXAM_LEVEL           = "level";
    String SEARCH_EXAM_DOMAIN          = "domain_name";
    String IGNORE_VALUES               = "ignore";
    String RANKING_STREAM_NAMESPACE    = "ranking_stream";
    String FACILITIES_MASTER_LIST      = "master_list";

    String COURSE_ALPHABETICAL_SORT_KEY     = "alphabetical";
    String STREAM_COURSE                    = "domain_name";
    String DEGREE_COURSE                    = "degree";
    String BRANCH_COURSE                    = "branch";
    String LEVEL_COURSE                     = "level";
    String ACCEPTING_APPLICATION            = "is_accepting_application";
    String INSTITUTE_NAME_COURSE            = "institute_official_name";
    String SEATS_COURSE                     = "seats";
    String FEE_COURSE                       = "fees";
    String NAME_COURSE                      = "name";
    String NAME_COURSE_SEARCH               = "name.search";
    String DURATION_COURSE                  = "duration_in_months";
    String PARENT_INSTITUTE_ID_COURSE       = "parent_institute_id";
    String INSTITUTE_ID_COURSE              = "institute_id";
    String GALLERY_LOGO                     = "gallery.logo";
    String OFFICIAL_ADDRESS                 = "official_address";
    String ENTITY_ID                        = "entity_id";
    float  NAME_COURSE_BOOST                = 1f;
    String ENTITY_NAME                      = "entity_name";
    int    COURSE_SIZE_FOR_INSTITUTE_DETAIL = 6;
    String COMPARE                          = "compare";
    int    SEARCH_REQUEST_MAX_RADIUS        = 50;
    int    SEARCH_REQUEST_MIN_RADIUS        = 1;

    // Detail APIs constants
    String EXAM_SHORT_NAME     = "exam_short_name";
    String EXAM_FULL_NAME      = "exam_full_name";
    String COURSE_PREFIX       = "course.";
    String INSTITUTE_PREFIX    = "institute.";
    String HIGHLIGHTS_TEMPLATE = "highlights";
    String HIGHLIGHTS_BASE_URL = "highlight_base_url";
    String OVERALL_RANKING     = "overall";

    String MAXIMUM_PACKAGE_LABEL = "Maximum Package";
    String MINIMUM_PACKAGE_LABEL = "Minimum Package";
    String AVERAGE_PACKAGE_LABEL = "Average Package";
    String MEDIAN_PACKAGE_LABEL  = "Median Package";
    String EVENT_TYPE_EXAM       = "EXAM";
    String ZERO                  = "0";

    String DISPLAY_NAME                  = "display_name";
    String LOGO                          = "logo";
    String SECTION_ORDER_NAMESPACE       = "section_order";
    String DETAIL_PAGE_SECTION_ORDER     = "detail_page_section_order";
    String DETAIL_PAGE_SECTION_ORDER_APP = "detail_page_section_order_app";
    String BANNER                        = "banner";
    String BANNER_APP                    = "banner_app";
    String WIDGETS                       = "widgets";
    String DATA_STRING                   = "data";
    String PARENT_INSTITUTION            = "parent_institution";
    String NOTABLE_ALUMNI_PLACEHOLDER    = "/notable_alumni_placeholder.png";

    // Other Constants
    Version FTL_CURRENT_VERSION = Configuration.VERSION_2_3_23;
    String  SUCCESS             = "success";

    //CUTOFF Constants
    String CUTOFF_EXAM_ID         = "cutoffs.exam_id";
    String CUTOFF_GENDER          = "cutoffs.gender";
    String CUTOFF_CASTE_GROUP     = "cutoffs.caste_group";
    String EXAM_DEGREES           = "exam_degrees";
    String EXAM_CUTOFF_GENDER     = "exam_cutoff_gender";
    String EXAM_CUTOFF_CASTEGROUP = "exam_cutoff_castegroup";
    String CUTOFF                 = "cutoffs";
    String SUBEXAM_ID             = "subexams.id";
    String EXAMS_ACCEPTED         = "exams_accepted";
    String OTHER_CATEGORIES       = "OTHERS";
    String GENDER                 = "gender";
    String CASTEGROUP             = "caste_group";

    String AFFILIATED           = "affiliated";
    String UGC                  = "ugc";
    String CONSTITUENT          = "constituent";
    String AUTONOMOUS           = "autonomous";
    String STANDALONE_INSTITUTE = "Standalone Institute";
    String STATE_LEGISLATURE    = "state legislature/parliament";
    String AFFILIATED_TO        = "Affiliated To";
    String APPROVED_BY          = "Approved By";
    String CONSTITUENT_OF       = "Constituent Of";
    String GOVERNED_BY          = "Governed By";
    String INSTITUTE_TYPE       = "Institute Governance";

    String EQ_OPERATOR               = "$eq";
    String IN_OPERATOR               = "$in";
    String EMPTY_SQUARE_BRACKETS     = "[]";
    String STREAMS                   = "streams";
    String SIMILAR_COLLEGES          = "Similar Colleges";
    String SIMILAR_COLLEGE_NAMESPACE = "similar_colleges";
    int    TOTAL_SIMILAR_COLLEGE     = 4;
    int    NO_OF_LOWER_RANK_COLLEGE  = 2;
    int    NO_OF_HIGHER_RANK_COLLEGE = 2;
    int    MAX_STREAMS               = 2;
    int    COLLEGES_PER_STREAM       = 2;

    String COLLEGE_FOCUS    = "colleges_focus";
    String TOP_COLLEGES     = "top_colleges";
    String TOP_SCHOOLS      = "top_schools";
    String SCHOOLS_IN_FOCUS = "schools_focus";
    String TOP_EXAMS        = "top_exams";
    String TOP_EXAMS_APP    = "top_exams_app";
    String ICON             = "icon";
    String LOCATIONS        = "locations";
    String APP_FOOTER       = "app_footer";
    String BANNER_MID       = "banner_mid";
    String CAROUSEL         = "CAROUSEL";
    String IMAGE_URL        = "image_url";
    String DUMMY_EXAM_ICON  = "/exam_placeholder.svg";
    String ID               = "id";
    String RANKING_LOGO     = "ranking";
    String RANKING_NIRF     = "NIRF";
    String RANKING_CAREER   = "CAREERS360";
    String NIRF_LOGO        = "/nirf.jpg";
    String CAREER_LOGO      = "/career360.jpg";
    String EMPTY_STRING     = " ";

    String EXAM_FOCUS_APP               = "exam_focus_app";
    String POPULAR_EXAMS_APP            = "popular_exams_app";
    String BROWSE_BY_EXAM_LEVEL         = "browse_by_exam_level";
    String RECENT_SEARCHES_kAFKA_TOPIC  = "recent_searches_explore";
    String RECENT_SEARCHES_ES_INDEX     = "recent_searches";
    String RECENT_SEARCHES_ES_TYPE      = "education";
    String SEARCH_HISTORY_USERID        = "user_id";
    String SEARCH_HISTORY_TERMS         = "terms";
    String SEARCH_HISTORY_UPDATEDAT     = "updated_at";
    int    DELETE_RECENTS_BATCH_SIZE    = 100;
    String RECENT_SEARCHES_ENTITY       = "entity";
    String RECENT_SEARCHES_ID_SEPERATOR = "-";

    String CTA                  = "cta";
    String EXAM_SEARCH_CTA      = "exam_search_cta";
    String SCHOOL_SEARCH_CTA    = "school_search_cta";
    String INSTITUTE_SEARCH_CTA = "institute_search_cta";
    String SELECTED             = "_selected";
    String CTA_LOGO_PLACEHOLDER = "/placeholder.svg";

    //School Constants
    String SCHOOL_SEARCH_NAMESPACE = "school_search";
    String SCHOOL_FILTER_NAMESPACE = "school_filter";
    String SEARCH_ANALYZER_SCHOOL  = "word_delimiter_analyzer";
    String SEARCH_INDEX_SCHOOL     = "education_search_school_v2";
    String SCHOOL_FACILITY_KEY     = "school_facility_map";

    String OTHER           = "Other";
    String SUMMARY         = "Summary";
    String BLANK           = "";
    String RANKING_OVERALL = "ranking_overall";
    int    MINUS_TEN       = -10;
    int    SIXTY           = 60;
    String YEARS           = "years";
    int    SIX             = 6;
    String P_TOP_INS       = "P_TOP_INS";

    String EDUCATION_EXPLORE_PREFIX      = "education/explore/";
    String DIRECTORY_SEPARATOR_SLASH     = "/";
    String CLIENT                        = "client";
    String FIELD_POST_FIX                = "_id";
    String ERROR_IN_FIELD_VALUE_TEMPLATE = "Incorrect value %s for field %s";
    String SCHOOL                        = "school";
    String COLLEGE                       = "college";
    String HIGHLIGHT                     = "highlight";
    String DETAILS                       = "details";
    String LANDING                       = "landing";
    String SECTIONS                      = "sections";
    String NAME                          = "name";
    String FAILED                        = "failed";

    //Sections constants
    String BROWSE_BY_EXAM_LEVEL_APP         = "browse_by_exam_level_app";
    String EXAM_LEVEL                       = "level";
    String EXAM_STREAM_IDS                  = "stream_ids";
    String EXAM_GLOBAL_PRIORITY             = "global_priority";
    String FILTERS                          = "filters";
    String LEVEL                            = "level";
    int    EXAMS_BROWSE_BY_LEVEL_QUERY_SIZE = 2;
    String EXAM_DETAIL                      = "exam_detail";
    String SECTION                          = "section";
    String SECTION_PLACEHOLDER              = "/section_placeholder.svg";
    String STREAM_IDS                       = "stream_ids";
    String STREAM_COLLECTION_ID_KEY         = "stream_id";
    String DOMAINS                          = "domains";
    String APP_DISPLAY_NAME                 = "app_display_name";
    String WEB_FORM_URI_PREFIX              = "webFormUriPrefix";
    String PRIORITY                         = "priority";
    int    DEFAULT_EXAM_PRIORITY            = 9999;

    String NO_TOPIC_FOUND = "No Topic Found";
}
