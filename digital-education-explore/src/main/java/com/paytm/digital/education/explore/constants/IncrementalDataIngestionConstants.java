package com.paytm.digital.education.explore.constants;

public interface IncrementalDataIngestionConstants {
    String EXAM_FILE_NAME                  = "exams.json";
    String INSTITUTE_FILE_NAME             = "institutions.json";
    String COURSES_FILE_NAME               = "courses.json";
    String SFTP_INSTITUTE_FILE_NAME_FORMAT = "institutions_{0}.json";
    String SFTP_COURSE_FILE_NAME_FORMAT    = "courses_{0}.json";
    String SFTP_EXAM_FILE_NAME_FORMAT      = "exams_{0}.json";
    String NEXT_INSTITUTE_FILE_VERSION     = "next_institute_file_version";
    String NEXT_COURSE_FILE_VERSION        = "next_course_file_version";
    String NEXT_EXAM_FILE_VERSION          = "next_exam_file_version";
    String DATA_INGESTION                  = "data_ingestion";
    String INCREMENTAL                     = "incremental";
}
