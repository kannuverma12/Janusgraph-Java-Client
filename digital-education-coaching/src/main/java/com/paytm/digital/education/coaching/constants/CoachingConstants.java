package com.paytm.digital.education.coaching.constants;

import com.paytm.digital.education.coaching.consumer.model.dto.ExamAdditionalInfoData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoachingConstants {

    public static final String INSTITUTE_ID                    = "institute_id";
    public static final String INSTITUTE                       = "institute";
    public static final String STREAM                          = "stream";
    public static final String EXAM                            = "exam";
    public static final String COACHING                        = "coaching";
    public static final String COURSE                          = "course";
    public static final String EXAM_ID                         = "exam_id";
    public static final String STREAM_ID                       = "stream_id";
    public static final String COURSE_ID                       = "course_id";
    public static final String COURSE_TYPE                     = "course_type";
    public static final String COACHING_EXAM_STREAMS           = "stream_names";
    public static final String COACHING_COURSE_EXAMS           = "exam_names";
    public static final String COACHING_COURSE_STREAMS         = "stream_names";
    public static final String COACHING_COURSE_INSTITUTE       = "coaching_institute_name";
    public static final String COACHING_COURSE_LEVEL           = "level";
    public static final String COACHING_COURSE_DURATION        = "course_duration_days";
    public static final String COACHING_INSTITUTE_STREAMS      = "stream_names";
    public static final String COACHING_INSTITUTE_EXAMS        = "exam_names";
    public static final String COACHING_INSTITUTE_COURSE_TYPES = "course_types";
    public static final String COACHING_CENTER_CITY            = "city";
    public static final String COACHING_CENTER_STATE           = "state";
    public static final String COACHING_CENTER_LOCATION        = "location";
    public static final String DISTANCE_KILOMETERS             = " km";

    public static final String COACHING_COMPONENT  = "coaching";
    public static final String GOOGLE_SHEETS_INFO  = "google_sheets_info";
    public static final String DATA_INGEST         = "coaching_data_ingest";
    public static final String DATA_EXPORT         = "coaching_data_export";
    public static final String TYPE                = "type";
    public static final String COACHING_COURSE_ID  = "course_id";
    public static final String COACHING_COURSE_IDS = "course_ids";
    public static final String NAME                = "name";
    public static final String PAYTM_REQUEST_ID    = "PaytmRequestId";
    public static final String CHECKSUM_HASH       = "ChecksumHash";
    public static final String ACCESS_KEY          = "AccessKey";

    public static final String INSTITUTES_GOOGLE_SHEET =
            "https://docs.google.com/spreadsheets/u/1/d/1gXwzFk3tLldPBYkgWZZINAc4gNJBSyItINeLLg4Bqs8/edit#gid=1844071691";
    public static final String TOP_RANKER_GOOGLE_SHEET =
            "https://docs.google.com/spreadsheets/d/1wBWJbX57bTgZxkwCx9Ml3Z9fVOP-yQkVvlRA-krdOZo/edit#gid=1642305828";

    public static final SimpleDateFormat MMM_YYYY    = new SimpleDateFormat("MMM,yyyy");
    public static final SimpleDateFormat DD_MMM_YYYY = new SimpleDateFormat("dd MMM,yyyy");
    public static final SimpleDateFormat YYYY_MM     = new SimpleDateFormat("yyyy-MM");

    public static final List<ExamAdditionalInfoData>
                                     EXAM_ADDITIONAL_INFO_PARAMS                  =
            new ArrayList<ExamAdditionalInfoData>();
    public static final String       DETAILS_PROPERTY_KEY                         =
            "detail_page_section_order";
    public static final String       DETAILS_PROPERTY_COMPONENT                   = "coaching";
    public static final String       DETAILS_PROPERTY_NAMESPACE                   = "section_order";
    public static final List<String> EXAM_DETAILS_FIELDS                          = new ArrayList<>(
            Arrays.asList("exam_id", "exam_full_name", "exam_short_name", "about_exam",
                    "instances", "logo", "stream_ids"));
    public static final List<String> STREAM_DETAILS_FIELDS                        = new ArrayList<>(
            Arrays.asList("stream_id", "name", "is_enabled"));
    public static final String       NON_TENTATIVE                                = "non_tentative";
    public static final String       STREAM_PLACEHOLDER                           =
            "/stream_placeholder.svg";
    public static final String       EXAM_PLACEHOLDER                             =
            "/exam_placeholder.svg";
    public static final String       INSTITUTE_PLACEHOLDER                        =
            "/institute_placeholder.svg";
    public static final String       INSTITUTE_COVER_IMAGE_PLACEHOLDER            =
            "/institute_cover_image_placeholder.svg";
    public static final String       COACHING_COURSE_PLACEHOLDER                  =
            "/coaching_course_placeholder.svg";
    public static final String       COACHING_COURSE_TYPE_PLACEHOLDER             =
            "/coaching_course_type_placeholder.svg";
    public static final String       TOP_RANKER_PLACEHOLDER                       =
            "/top_ranker_placeholder.svg";
    public static final String       COACHING_INSTITUTE_KEY_HIGHLIGHT_PLACEHOLDER =
            "/key_highlight_placeholder.svg";
    public static final String       COACHING_CENTER_PLACEHOLDER                  =
            "/coaching_center_placeholder.svg";
    public static final String       EMPTY_STRING                                 = "";

    static {
        EXAM_ADDITIONAL_INFO_PARAMS.add(
                new ExamAdditionalInfoData("Eligibility", "Find out eligibility", "image_link"));
        EXAM_ADDITIONAL_INFO_PARAMS.add(
                new ExamAdditionalInfoData("Syllabus", "Find out Syllabus", "image_link"));
        EXAM_ADDITIONAL_INFO_PARAMS.add(
                new ExamAdditionalInfoData("Fill Application Form", "Fill out application form",
                        "image_link"));
        EXAM_ADDITIONAL_INFO_PARAMS.add(
                new ExamAdditionalInfoData(
                        "Frequently Asked Questions",
                        "Find answers to your most common questions",
                        "image_link"));
    }


    public static class URL {

        public static final String COACHING_BASE = "/coaching";
        public static final String V1            = "/v1";

        public static final String GET_EXAM_DETAILS = "/exam-details";

        public static final String COACHING_COURSE_DETAILS = "/course";

        public static final String GET_STREAM_DETAILS = "/stream-details";

        public static final String GET_COACHING_INSTITUTE_DETAILS = "/coaching-institute-details";

        public static final String GET_COCHING_AUTOSUGGESTIONS = "/autosuggest";

        public static final String LANDING_PAGE = "/page";

        public static final String CHECKOUT_DATA = "/checkout-data";

        public static final String VERIFY          = "/verify";
        public static final String MERCHANT_NOTIFY = "/merchant-notify";
    }


    public static class Search {

        public static final String EXAM_INDEX                      = "education_search_exam_v2";
        public static final String SEARCH_INDEX_COACHING_COURSE    = "coaching_course_v1";
        public static final String SEARCH_INDEX_COACHING_INSTITUTE = "coaching_institute_v1";
        public static final String SEARCH_INDEX_COACHING_CENTER    = "coaching_center_v1";

        public static final String IGNORE_GLOBAL_PRIORITY = "ignore_global_priority";
        public static final String IGNORE_ENTITY_POSITION = "ignore_entity_position";

        public static final String EXAM_ANALYZER                      =
                "word_delimiter_analyzer_search";
        public static final String SEARCH_ANALYZER_COACHING_COURSE    =
                "word_delimiter_analyzer_search";
        public static final String SEARCH_ANALYZER_COACHING_INSTITUTE =
                "word_delimiter_analyzer_search";
        public static final String SEARCH_ANALYZER_COACHING_CENTER    =
                "word_delimiter_analyzer_search";

        public static final String EXAM_SHORT_NAME                = "exam_short_name";
        public static final String EXAM_FULL_NAME                 = "exam_full_name";
        public static final String EXAM_OFFICIAL_NAME             = "official_name";
        public static final String EXAM_OFFICIAL_NAME_NGRAM       = "official_name.raw";
        public static final String EXAM_NAME_SYNONYMS             = "exam_name_synonyms";
        public static final Float  EXAM_FULL_NAME_BOOST           = 1F;
        public static final Float  EXAM_SHORT_NAME_BOOST          = 1F;
        public static final Float  EXAM_OFFICIAL_NAME_BOOST       = 1F;
        public static final Float  EXAM_NAME_SYNONYMS_BOOST       = 1F;
        public static final Float  EXAM_OFFICIAL_NAME_NGRAM_BOOST = 0.00001F;
        public static final String APPLICATION                    = "APPLICATION";

        public static final String EXAM_IDS                         = "exam_ids";
        public static final String STREAM_IDS                       = "stream_ids";
        public static final String COACHING_INSTITUTE_ID            = "coaching_institute_id";
        public static final String COACHING_INSTITUTE_BRAND         = "brand_name";
        public static final Float  COACHING_INSTITUTE_BRAND_BOOST   = 1F;
        public static final String COACHING_COURSE_NAME             = "course_name";
        public static final Float  COACHING_COURSE_NAME_BOOST       = 1F;
        public static final String COACHING_CENTER_NAME             = "official_name";
        public static final Float  COACHING_CENTER_NAME_BRAND_BOOST = 1F;

        public static final String SEARCH_STREAM_PREFIX = "streams.";
        public static final String SEARCH_STREAM_SUFFIX = ".position";
        public static final String SEARCH_EXAM_PREFIX   = "exams.";
        public static final String SEARCH_EXAM_SUFFIX   = ".position";

        public static final String GLOBAL_PRIORITY = "global_priority";
        public static final String DATE_TAB        = "dates";
        public static final String SYLLABUS_TAB    = "syllabus";
        public static final String RESULT          = "results";

        public static final String FEES                      = "fees";
        public static final String DATA                      = "data";
        public static final String DISPLAY_NAME              = "display_name";
        public static final String KEY                       = "key";
        public static final int    SEARCH_REQUEST_MAX_OFFSET = 9950;
        public static final int    SEARCH_REQUEST_MAX_LIMIT  = 500;
        public static final int    DEFAULT_OFFSET            = 0;
        public static final int    DEFAULT_SIZE              = 10;

    }


    public static class LandingPage {
        public static final String NAME            = "display_name";
        public static final String ID              = "entity_id";
        public static final String URL_DISPLAY_KEY = "url_display_key";
        public static final String LOGO            = "logo";
        public static final String FULL_NAME       = "full_name";
    }


    public static class ImportantDates {
        public static final String HEADER      = "Important Dates!";
        public static final String DESCRIPTION =
                "Keep track of important dates like Last date of form submission, Examination date, etc.";
        public static final String LOGO        = "image_logo";
        public static final String BUTTON_TEXT = "View Dates";
    }


    public static class TransactionConstants {
        public static final Float CONVENIENCE_FEE_PERCENTAGE       = 0F;
        public static final Float CONVENIENCE_FEE_IGST_PERCENTAGE  = 0F;
        public static final Float CONVENIENCE_FEE_CGST_PERCENTAGE  = 0F;
        public static final Float CONVENIENCE_FEE_SGST_PERCENTAGE  = 0F;
        public static final Float CONVENIENCE_FEE_UTGST_PERCENTAGE = 0F;

        public static final Float ITEM_IGST_PERCENTAGE  = 0F;
        public static final Float ITEM_CGST_PERCENTAGE  = 0F;
        public static final Float ITEM_SGST_PERCENTAGE  = 0F;
        public static final Float ITEM_UTGST_PERCENTAGE = 0F;

        public static final Float TCS_IGST_PERCANTAGE  = 0F;
        public static final Float TCS_CGST_PERCANTAGE  = 0F;
        public static final Float TCS_SGST_PERCANTAGE  = 0F;
        public static final Float TCS_UTGST_PERCANTAGE = 0F;

        public static final Long SAC  = 999615L;
        public static final Long SPIN = 400086L;
        public static final Long DPIN = 400086L;
    }


    public static final class FailedDataCollection {
        public static final String COMPONENT     = "component";
        public static final String TYPE          = "type";
        public static final String HAS_IMPORTED  = "has_imported";
        public static final String IS_IMPORTABLE = "is_importable";
    }


    public static class RestTemplateConstants {
        public static final Integer HTTP_MAX_CONNECTION_ALLOWED     = 200;
        public static final Integer HTTP_MAX_CONNECTION_PER_REQUEST = 50;

        public static final Integer MERCHANT_COMMIT_TIMEOUT_MS = 10000;

        public static final String PAYTM_HOST_FOR_SIGNATURE = "https://paytm.com";
    }
}
