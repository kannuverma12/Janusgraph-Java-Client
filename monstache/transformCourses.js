/*
 * Data transformation script for EDU-Explore Courses data
*/

var database_name = "digital_education";
var college_collection = "institute";
var course_collection = "course";
var exam_collection = "exam";

/**
 * get College document from mongo or return null.
 * @param college_id: int
 */

function findCollegeById(college_id) {
    var collegeQueryOptions = {
        database: database_name,
        collection: college_collection
    };

    var listOfColleges = find({ institute_id: college_id }, collegeQueryOptions);
    if (listOfColleges === undefined || listOfColleges.length !== 1) {
        // requested document not found or invalid case when more then 1 doc found for same id
        warn("Cannot find college with id :" + college_id);
        return null;
    }
    return listOfColleges[0];
}

function findExam(examid){
    var examQueryOptions = {
        database: database_name,
        collection: exam_collection
    };

    var listOfExams = find({ exam_id: examid }, examQueryOptions);
    if (listOfExams === undefined || listOfExams.length !== 1) {
        // requested document not found or invalid case when more then 1 doc found for same id
        warn("Cannot find Exam with id :" + examid);
        return null;
    }
    return listOfExams[0];
}


//Do transformation

function transformCourse(doc) {

    var college_id = doc.institute_id;
    var targetCollege = findCollegeById(college_id);
    if (targetCollege === null || targetCollege.publishing_status !== "PUBLISHED") {
        warn("institute_id : " + college_id + " is not published, hence skipping course_id : " + doc.course_id);
        return null;
    }


    var transformedCourse = {};
    transformedCourse.course_id = doc.course_id;
    transformedCourse.name = doc.course_name_official;
    transformedCourse.institute_id = doc.institute_id;

    if(targetCollege.parent_institution){
        transformedCourse.parent_institute_id = targetCollege.parent_institution;
    }
    else{
        transformedCourse.parent_institute_id = targetCollege.institute_id;
    }
    transformedCourse.institute_official_name = targetCollege.official_name;
    transformedCourse.degree = doc.master_degree;
    transformedCourse.level = doc.course_level;
    transformedCourse.branch = doc.master_branch;
    transformedCourse.study_mode = doc.study_mode;
    transformedCourse.duration_in_months = doc.course_duration;
    info("course_id : " + doc.course_id + ", lead_enabled = " + doc.lead_enabled);
    if(doc.lead_enabled && doc.lead_enabled == 1) {
        transformedCourse.is_accepting_application = true;
    } else {
        transformedCourse.is_accepting_application = false;
    }
    transformedCourse.domain_name = [];
    transformedCourse.stream_ids = doc.stream_ids;
    for (var k = 0; k < doc.streams.length; k++) {
          if(doc.streams[k].toLowerCase() === 'education' ||
              doc.streams[k].toLowerCase() === 'sciences' ||
              doc.streams[k].toLowerCase() === 'arts_humanities_and_social_sciences' ){
              transformedCourse.domain_name.push('Humanities and Sciences');
          } else {
              transformedCourse.domain_name.push(doc.streams[k]);
          }
    }
    transformedCourse.seats = doc.seats_available;

    if (Array.isArray(doc.course_fees) && doc.course_fees.length > 0) {
        var college_fees;
        if (doc.course_fees.length === 1) {
            college_fees = doc.course_fees[0].fee;
        } else {
            var gen_fees = doc.course_fees.filter(function (caste_fee) {
                return caste_fee.caste_group === 'GENERAL';
            });
            if (gen_fees.length > 0) {
                college_fees = gen_fees[0].fee;
            }
        }

        if (college_fees) {
            transformedCourse.fees = college_fees;
        }
    }

    transformedCourse.exams = [];
    if (Array.isArray(doc.exams_accepted) && doc.exams_accepted.length > 0){
        var exam_ids = doc.exams_accepted;
        exam_ids.forEach(function(exam_id){
            var examData = findExam(exam_id);
            if(examData !== null){
                transformedCourse.exams.push(examData.exam_short_name);
            }
        });

    }
    info("Processed course : " + transformedCourse.course_id);
    return transformedCourse;
}

module.exports = function(doc, ns, updateDesc) {
    var db = ns.split(".")[0];
    var coll = ns.split(".")[1];
    if (db !== database_name)
        return false;
    try {
        if (coll === course_collection && doc.publishing_status === "PUBLISHED") {
            info("Processing Collection : " + coll + " course_id : " + doc.course_id);
        } else {
            warn(" course_id : " + doc.course_id + " is not in published status. skipping");
            return false;
        }
        return transformCourse(doc);
    } catch (e) {
        error("Error in processing courses : " + doc.course_id + ", " + e);
        return false;
    }
}

function info(message) {
    console.log("INFO : " + currentTimestamp() + " " + message);
}

function error(message) {
    console.error("ERROR : "+ currentTimestamp() + " " + message);
}

function warn(message) {
    console.warn("WARN : " + currentTimestamp() + " " + message);
}

function currentTimestamp() {
    return new Date().toISOString();
}
