package com.paytm.digital.education.explore.constants;

public interface IncrementalDataIngestionConstants {
    String EXAM_FILE_NAME                  = "exams.json";
    String INSTITUTE_FILE_NAME             = "institutions.json";
    String COURSES_FILE_NAME               = "courses.json";
    String SCHOOLS_FILE_NAME               = "schools.json";
    String SFTP_INSTITUTE_FILE_NAME_FORMAT = "institutions_{0}.json";
    String SFTP_COURSE_FILE_NAME_FORMAT    = "courses_{0}.json";
    String SFTP_EXAM_FILE_NAME_FORMAT      = "exams_{0}.json";
    String SFTP_SCHOOL_FILE_NAME_FORMAT    = "schools_{0}.json";
    String NEXT_INSTITUTE_FILE_VERSION     = "next_institute_file_version";
    String NEXT_COURSE_FILE_VERSION        = "next_course_file_version";
    String NEXT_EXAM_FILE_VERSION          = "next_exam_file_version";
    String NEXT_SCHOOL_FILE_VERSION        = "next_school_file_version";
    String DATA_INGESTION                  = "data_ingestion";
    String INCREMENTAL                     = "incremental";
    String COURSE_IDS                      = "course_ids";
    String COURSES                         = "courses";
    String INSTITUTE_FILE_VERSION          = "attributes.next_institute_file_version";
    String COURSE_FILE_VERSION             = "attributes.next_course_file_version";
    String EXAM_FILE_VERSION               = "attributes.next_exam_file_version";
    String SCHOOL_FILE_VERSION             = "attributes.next_school_file_version";
    String COURSES_DIRECTORY               = "/courses";
    String INSTITUTION_DIRECTORY           = "/institutions";
    String EXAM_DIRECTORY                  = "/exams";
    String SCHOOL_DIRECTORY                = "/schools";
    String EXAM_ENTITY                     = "exam";
    String COURSE_ENTITY                   = "course";
    String INSTITUTE_ENTITY                = "institute";
    String SCHOOL_ENTITY                   = "school";
    String S3_IMAGE_PATH_SUFFIX            = "/images";
}
