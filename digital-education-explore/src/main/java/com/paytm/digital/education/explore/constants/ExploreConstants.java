package com.paytm.digital.education.explore.constants;

public interface ExploreConstants {

    ///////////////////////////////////////
    ////// Common constants /////////////

    public static final String INSTITUTE_ID              = "institute_id";
    public static final String EXAM_ID                   = "exam_id";
    public static final String COURSE_ID                 = "course_id";

    public static final String ENTITY_TYPE               = "entity_type";

    public static final int    DEFAULT_OFFSET            = 0;
    public static final int    DEFAULT_SIZE              = 10;

    public static final int    DEFAULT_AUTOSUGGEST_SIZE  = 4;


    ///////////////////////////////////////////
    ////// AutoSuggest Constants ////////////

    public static final String AUTOSUGGEST_INDEX         = "education_autosuggestion_v1";
    public static final String AUTOSUGGEST_ANALYZER      = "words_with_spaces_analyzer";

    public static final int    AUTOSUGGEST_MIN_CHARS     = 3;
    public static final int    AUTOSUGGEST_MAX_CHARS     = 50;


    public static final String AUTOSUGGEST_OFFICIAL_NAME = "official_name";
    public static final String AUTOSUGGEST_NAMES         = "names";

    ////////////////////////////////////////////////
    ///////// Explore search Constatnts ///////////

    public static final String SEARCH_INDEX              = "education_search_institute_v1";
    public static final String SEARCH_ANALYZER           = "word_delimiter_analyzer";

    public static final String SEARCH_NAMES              = "names";
    public static final String MAX_RANK                  = "max_rank";
    public static final String STATE                     = "state";
    public static final String CITY                      = "city";
    public static final String STREAM                    = "domain_name";
    public static final String COURSE_LEVEL              = "level";
    public static final String EXAMS_ACCEPTED            = "exams";
    public static final String FEES                      = "fees";

    public static final String EDUCATION_BASE_URL        = "/explore";
}
