package com.paytm.digital.education.coaching.constants;

import com.paytm.digital.education.config.AwsConfig;

public interface CoachingConstants {
    String INSTITUTE_ID                          = "institute_id";
    String INSTITUTE                             = "institute";
    String COACHING_CENTER                       = "coaching_center";
    String EXAM                                  = "exam";
    String COACHING                              = "coaching";
    String COURSE                                = "course";
    String COACHING_CENTER_ID                    = "center_id";
    String EXAM_ID                               = "exam_id";
    String COURSE_ID                             = "course_id";
    String ACTIVE                                = "active";
    String KEY_STRING                            = "key";
    String SEQUENCE                              = "sequence";
    String UPDATED_AT                            = "updated_at";
    String YYYY_MM_DD                            = "yyyy-MM-dd";
    String YYYY_MM_DD_T_HH_MM_SS                 = "yyyy-MM-dd'T'HH:mm:ss";
    String EXAM_NOT_FOUND_ERROR                  =
            "No such exam exists. Please check your request.";
    String COACHING_NOT_FOUND_ERROR              =
            "No such coaching institute exists. Please check your request.";
    String SUCCESS_MESSAGE                       = "Success";
    String COURSE_NOT_FOUND_ERROR                =
            "No such course exists. Please check your request.";
    String CENTER_NOT_FOUND_ERROR                =
            "No such center exists. Please check your request.";
    String COACHING_COMPONENT                    = "coaching";
    String GOOGLE_SHEETS_INFO                    = "google_sheets_info";
    String DATA_INGEST                           = "coaching_data_ingest";
    String ATTRIBUTES                            = "attributes";
    String INSTITUTE_SHEET_ID                    = "institute_sheet_id";
    String INSTITUTE_SHEET_HEADER_RANGE          = "institute_sheet_header_range";
    String INSTITUTE_SHEET_RANGE_TEMPLATE        = "institute_sheet_range_template";
    String INSTITUTE_SHEET_START_ROW             = "institute_sheet_start_row";
    String CENTRE_SHEET_ID                       = "centre_sheet_id";
    String CENTRE_SHEET_HEADER_RANGE             = "centre_sheet_header_range";
    String CENTRE_SHEET_RANGE_TEMPLATE           = "centre_sheet_range_template";
    String CENTRE_SHEET_START_ROW                = "centre_sheet_start_row";
    String STUDENT_SELECTED_SHEET_ID             = "student_selected_sheet_id";
    String STUDENT_SELECTED_SHEET_HEADER_RANGE   = "student_selected_sheet_header_range";
    String STUDENT_SELECTED_SHEET_RANGE_TEMPLATE = "student_selected_sheet_range_template";
    String STUDENT_SELECTED_SHEET_START_ROW      = "student_selected_sheet_start_row";
    String GALLERY_SHEET_ID                      = "gallery_sheet_id";
    String GALLERY_SHEET_HEADER_RANGE            = "gallery_sheet_header_range";
    String GALLERY_SHEET_RANGE_TEMPLATE          = "gallery_sheet_range_template";
    String GALLERY_SHEET_START_ROW               = "gallery_sheet_start_row";
    String EXAM_SHEET_ID                         = "exam_sheet_id";
    String EXAM_SHEET_HEADER_RANGE               = "exam_sheet_header_range";
    String EXAM_SHEET_RANGE_TEMPLATE             = "exam_sheet_range_template";
    String EXAM_SHEET_START_ROW                  = "exam_sheet_start_row";
    String COURSE_SHEET_ID                       = "course_sheet_id";
    String COURSE_SHEET_HEADER_RANGE             = "course_sheet_header_range";
    String COURSE_SHEET_RANGE_TEMPLATE           = "course_sheet_range_template";
    String COURSE_SHEET_START_ROW                = "course_sheet_start_row";
    String FACILITY_SHEET_ID                     = "facility_sheet_id";
    String FACILITY_SHEET_HEADER_RANGE           = "facility_sheet_header_range";
    String FACILITY_SHEET_RANGE_TEMPLATE         = "facility_sheet_range_template";
    String FACILITY_SHEET_START_ROW              = "facility_sheet_start_row";
    String FACILITY                              = "facility";
    String COMPONENT                             = "component";
    String TYPE                                  = "type";
    String HAS_IMPORTED                          = "has_imported";
    String IS_IMPORTABLE                         = "is_importable";
    String STUDENT_SELECTED                      = "student_selected";
    String HTTP                                  = "http://";
    String HTTPS                                 = "https://";
    String IMAGE                                 = "image";
    String VIDEO                                 = "video";
    String FAILED_MEDIA                          = "failed";
    String GALLERY                               = "gallery";
    String DB_DATE_FORMAT                        = "yyyy-MM-dd HH:mm:ss";
    String XCEL_EXAM_DATE_FORMAT                 = "dd/MM/yyyy";
    String S3_UPLOAD_FAILED                      = "File upload failed";


    interface S3RelativePath {
        String PREFIX           = AwsConfig.getRelativePathPrefix();
        String GALLERY          = PREFIX + "/institute/{0}/gallery";
        String STUDENT_SELECTED = PREFIX + "/institute/{0}/student_selected";
        String LOGO             = PREFIX + "/institute/{0}/logo";
        String COVER_IMAGE      = PREFIX + "/institute/{0}/cover_image";
        String CLASS_SCHEDULE   = PREFIX + "/course/{0}/class_schedule";
        String SCHOLARMATRIX    = PREFIX + "/course/{0}/scholarmatrix";
        String BROCHURE         = PREFIX + "/course/{0}/brochure";
    }
}
