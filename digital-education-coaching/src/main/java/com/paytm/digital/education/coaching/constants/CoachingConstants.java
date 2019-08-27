package com.paytm.digital.education.coaching.constants;

import com.paytm.digital.education.coaching.consumer.model.dto.ExamAdditionalInfo;
import com.paytm.digital.education.config.AwsConfig;

import java.util.ArrayList;
import java.util.List;

public class CoachingConstants {
    public static final String INSTITUTE_ID                          = "institute_id";
    public static final String INSTITUTE                             = "institute";
    public static final String COACHING_CENTER                       = "coaching_center";
    public static final String EXAM                                  = "exam";
    public static final String COACHING                              = "coaching";
    public static final String COURSE                                = "course";
    public static final String COACHING_CENTER_ID                    = "center_id";
    public static final String EXAM_ID                               = "exam_id";
    public static final String COURSE_ID                             = "course_id";
    public static final String ACTIVE                                = "active";
    public static final String KEY_STRING                            = "key";
    public static final String SEQUENCE                              = "sequence";
    public static final String UPDATED_AT                            = "updated_at";
    public static final String YYYY_MM_DD                            = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_T_HH_MM_SS                 = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String EXAM_NOT_FOUND_ERROR                  =
            "No such exam exists. Please check your request.";
    public static final String COACHING_NOT_FOUND_ERROR              =
            "No such coaching institute exists. Please check your request.";
    public static final String SUCCESS_MESSAGE                       = "Success";
    public static final String COURSE_NOT_FOUND_ERROR                =
            "No such course exists. Please check your request.";
    public static final String CENTER_NOT_FOUND_ERROR                =
            "No such center exists. Please check your request.";
    public static final String COACHING_COMPONENT                    = "coaching";
    public static final String GOOGLE_SHEETS_INFO                    = "google_sheets_info";
    public static final String DATA_INGEST                           = "coaching_data_ingest";
    public static final String ATTRIBUTES                            = "attributes";
    public static final String INSTITUTE_SHEET_ID                    = "institute_sheet_id";
    public static final String INSTITUTE_SHEET_HEADER_RANGE          =
            "institute_sheet_header_range";
    public static final String INSTITUTE_SHEET_RANGE_TEMPLATE        =
            "institute_sheet_range_template";
    public static final String INSTITUTE_SHEET_START_ROW             = "institute_sheet_start_row";
    public static final String CENTRE_SHEET_ID                       = "centre_sheet_id";
    public static final String CENTRE_SHEET_HEADER_RANGE             = "centre_sheet_header_range";
    public static final String CENTRE_SHEET_RANGE_TEMPLATE           =
            "centre_sheet_range_template";
    public static final String CENTRE_SHEET_START_ROW                = "centre_sheet_start_row";
    public static final String STUDENT_SELECTED_SHEET_ID             = "student_selected_sheet_id";
    public static final String STUDENT_SELECTED_SHEET_HEADER_RANGE   =
            "student_selected_sheet_header_range";
    public static final String STUDENT_SELECTED_SHEET_RANGE_TEMPLATE =
            "student_selected_sheet_range_template";
    public static final String STUDENT_SELECTED_SHEET_START_ROW      =
            "student_selected_sheet_start_row";
    public static final String GALLERY_SHEET_ID                      = "gallery_sheet_id";
    public static final String GALLERY_SHEET_HEADER_RANGE            = "gallery_sheet_header_range";
    public static final String GALLERY_SHEET_RANGE_TEMPLATE          =
            "gallery_sheet_range_template";
    public static final String GALLERY_SHEET_START_ROW               = "gallery_sheet_start_row";
    public static final String EXAM_SHEET_ID                         = "exam_sheet_id";
    public static final String EXAM_SHEET_HEADER_RANGE               = "exam_sheet_header_range";
    public static final String EXAM_SHEET_RANGE_TEMPLATE             = "exam_sheet_range_template";
    public static final String EXAM_SHEET_START_ROW                  = "exam_sheet_start_row";
    public static final String COURSE_SHEET_ID                       = "course_sheet_id";
    public static final String COURSE_SHEET_HEADER_RANGE             = "course_sheet_header_range";
    public static final String COURSE_SHEET_RANGE_TEMPLATE           =
            "course_sheet_range_template";
    public static final String COURSE_SHEET_START_ROW                = "course_sheet_start_row";
    public static final String FACILITY_SHEET_ID                     = "facility_sheet_id";
    public static final String FACILITY_SHEET_HEADER_RANGE           =
            "facility_sheet_header_range";
    public static final String FACILITY_SHEET_RANGE_TEMPLATE         =
            "facility_sheet_range_template";
    public static final String FACILITY_SHEET_START_ROW              = "facility_sheet_start_row";
    public static final String FACILITY                              = "facility";
    public static final String COMPONENT                             = "component";
    public static final String TYPE                                  = "type";
    public static final String HAS_IMPORTED                          = "has_imported";
    public static final String IS_IMPORTABLE                         = "is_importable";
    public static final String STUDENT_SELECTED                      = "student_selected";
    public static final String HTTP                                  = "http://";
    public static final String HTTPS                                 = "https://";
    public static final String IMAGE                                 = "image";
    public static final String VIDEO                                 = "video";
    public static final String FAILED_MEDIA                          = "failed";
    public static final String GALLERY                               = "gallery";
    public static final String DB_DATE_FORMAT                        = "yyyy-MM-dd HH:mm:ss";
    public static final String XCEL_EXAM_DATE_FORMAT                 = "dd/MM/yyyy";
    public static final String S3_UPLOAD_FAILED                      = "File upload failed";
    public static final String PROGRAM                               = "program";
    public static final String COACHING_EXAM                         = "coaching_exam";
    public static final String COACHING_PROGRAM_ID                   = "program_id";


    public static final String DETAILS_FIELD_GROUP       = "details_coaching";
    public static final String COACHING_INSTITUTE_PREFIX = "coaching_institute.";
    public static final String COACHING_PROGRAM_PREFIX   = "coaching_program.";


    public static class S3RelativePath {
        private static final String PREFIX           = AwsConfig.getRelativePathPrefix();
        public static final  String GALLERY          = PREFIX + "/institute/{0}/gallery";
        public static final  String STUDENT_SELECTED = PREFIX + "/institute/{0}/student_selected";
        public static final  String LOGO             = PREFIX + "/institute/{0}/logo";
        public static final  String COVER_IMAGE      = PREFIX + "/institute/{0}/cover_image";
        public static final  String CLASS_SCHEDULE   = PREFIX + "/course/{0}/class_schedule";
        public static final  String SCHOLARMATRIX    = PREFIX + "/course/{0}/scholarmatrix";
        public static final  String BROCHURE         = PREFIX + "/course/{0}/brochure";
    }


    public static final String INSTITUTES_GOOGLE_SHEET =
            "https://docs.google.com/spreadsheets/u/1/d/1gXwzFk3tLldPBYkgWZZINAc4gNJBSyItINeLLg4Bqs8/edit#gid=1844071691";

    public static final String TOP_RANKER_GOOGLE_SHEET =
            "https://docs.google.com/spreadsheets/d/1wBWJbX57bTgZxkwCx9Ml3Z9fVOP-yQkVvlRA-krdOZo/edit#gid=1642305828";

    public static final String RESOURCE_NOT_PRESENT = "resource not available";

    public static final String TOP_RANKER = "top_ranker";


    public static class URL {

        public static final String COACHING_BASE = "/coaching";
        public static final String V1            = "/v1";

        public static final String GET_EXAM_DETAILS = "/exam-details";

        public static final String COACHING_PROGRAM_DETAILS = "/program";
    }


    public static final List<ExamAdditionalInfo> ExamAdditionalInfoParams = new ArrayList<>();

    static {
        ExamAdditionalInfoParams.add(
                new ExamAdditionalInfo("Eligibility", "Find out eligibility", "image_link"));
        ExamAdditionalInfoParams.add(
                new ExamAdditionalInfo("Syllabus", "Find out Syllabus", "image_link"));
        ExamAdditionalInfoParams.add(
                new ExamAdditionalInfo("Fill Application Form", "Fill out application form",
                        "image_link"));
        ExamAdditionalInfoParams.add(
                new ExamAdditionalInfo(
                        "Frequently Asked Questions",
                        "Find answers to your most common questions",
                        "image_link"));
    }
}
