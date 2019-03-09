package com.paytm.digital.education.explore.constants;

public interface ExploreConstants {

    ////// Common constants /////////////

    String INSTITUTE_ID = "institute_id";
    String EXAM_ID      = "exam_id";
    String COURSE_ID    = "course_id";

    String ENTITY_TYPE = "entity_type";

    int DEFAULT_OFFSET = 0;
    int DEFAULT_SIZE   = 10;

    public static final int    DEFAULT_AUTOSUGGEST_SIZE  = 4;


    ///////////////////////////////////////////
    ////// AutoSuggest Constants ////////////

    String AUTOSUGGEST_INDEX    = "education_autosuggestion_v1";
    String AUTOSUGGEST_ANALYZER = "words_with_spaces_analyzer";

    int AUTOSUGGEST_MIN_CHARS = 3;
    int AUTOSUGGEST_MAX_CHARS = 50;

    String AUTOSUGGEST_OFFICIAL_NAME = "official_name";
    String AUTOSUGGEST_NAMES         = "names";

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

    String EDUCATION_BASE_URL = "/explore";
}
