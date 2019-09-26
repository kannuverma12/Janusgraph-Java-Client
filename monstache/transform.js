/*
 * Data transformation script for EDU-Explore institutes data
 *
 * @author: shashank.chhikara
 */

var database_name = "digital_education";
var college_collection = "institute";
var course_collection = "course";
var exam_collection = "exam";
var target_collection = "education_search_institute_v2";
var target_doc_type = "education";

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
    console.warn("WARN Cannot find college with id :" + college_id);
    return null;
  }
  return listOfColleges[0];
}



/**
 * get College document with extra entities embedded like
 * course and exams.
 * @return null: if college not found.
 * @param college_id: int
 */

function getCollegeSuperDocument(college_id) {
  var targetCollege = findCollegeById(college_id);

  if (targetCollege === null || targetCollege.publishing_status !== "PUBLISHED") {
    return null;
  }



  var coursesQueryOptions = {
    database: database_name,
    collection: course_collection

  };

  var listOfCourses = find({ institute_id: college_id, publishing_status: "PUBLISHED" }, coursesQueryOptions);

  // extract exams

  if (Array.isArray(listOfCourses)) {
    var exam_map = {};

    // unique id of each exam in exam_map
    // exam_map will be in form = { 1: true, 3: true }

    listOfCourses.forEach(function(course) {
      if (Array.isArray(course.exams_accepted)) {
        course.exams_accepted.forEach(function(exam_id) {
          exam_map[exam_id] = true;
        });
      }
    });

    var examsQueryOptions = {
      database: database_name,
      collection: 'exam',
      select: {
        exam_id: 1,
        exam_short_name: 1
      }
    };

    var listOfExamIds = Object.keys(exam_map).map(function (exam_id) {
      return Number(exam_id);
    }); // array of unique exam ids

    var listOfExams = find({exam_id: { $in: listOfExamIds }, published_status: "PUBLISHED"}, examsQueryOptions);

    /*
     * populate exam map in target College Object
     *
     * it will be in format = { 1: "Exam A", 3: "Exam B" }
     */

    targetCollege.exam_map = {};
    if (Array.isArray(listOfExams)) {
      listOfExams.forEach(function(exam) {
        targetCollege.exam_map[exam.exam_id] = exam.exam_short_name;
      })
    }
  }

  targetCollege.courses = listOfCourses;
  return targetCollege;
}



/**

 * recursively get Parent University of an entity whose entity_type is UNIVERSITY

 * @param entity_id: int

 */

function getParentUniversity(entity_id) {
  var entity = findCollegeById(entity_id);
  if (!entity) { // entity not found
    return null;
  }

  if (entity.entity_type === "UNIVERSITY") {
    return entity;
  } else if (entity.parent_institution) {
    return getParentUniversity(entity.parent_institution);
  } else {
    return null;
  }
}

function ConvertInCamelCase(stringName) {
  if(stringName != undefined || stringName != null){
    var stringArray = stringName.split(" ");
    var resultString = "";

    stringArray.forEach(function (tempString) {
      if(tempString.toLocaleLowerCase() === "and"){
        resultString = resultString + tempString.toLocaleLowerCase();
      }
      else if(tempString.length > 0){
        var part1 = tempString[0];
        var part2 = tempString.substring(1, tempString.length);
        if(part1){
          resultString = resultString + part1.toUpperCase();
        }
        if(part2){
          resultString = resultString + part2.toLowerCase();
        }
      }
      resultString = resultString + " ";
    });
    return resultString.trim();
  }
}



/**
 * do the transformation and return entity to store in ES.
 * @param superDoc: college object with related entities embedded.
 */

function transformCollege(superDoc) {
  var transformedCollege = {};
  transformedCollege._id = superDoc._id;
  transformedCollege.institute_id = superDoc.institute_id;


  if (superDoc.parent_institution !== undefined){
    transformedCollege.parent_institute_id = superDoc.parent_institution;
  }
  else{
    transformedCollege.parent_institute_id = superDoc.institute_id;
  }

  //former_name, alternate_names, official_name, common_name, university_name

  transformedCollege.alternate_names = [];
  if (Array.isArray(superDoc.alternate_names)) {
    transformedCollege.alternate_names = transformedCollege.alternate_names.concat(superDoc.alternate_names);
  }

  transformedCollege.former_name = [];
  if (Array.isArray(superDoc.former_name)) {
    transformedCollege.former_name = transformedCollege.former_name.concat(superDoc.former_name);
  }

  if (superDoc.official_name) {
     transformedCollege.official_name = superDoc.official_name;
  }

  if (superDoc.common_name) {
    transformedCollege.common_name = superDoc.common_name;
  }
  // setup university name

  if(superDoc.parent_institution){
    var university_college = getParentUniversity(superDoc.parent_institution);
    if (university_college !== null) {
      transformedCollege.university_name = university_college.official_name;
    }
  }
  else{
    transformedCollege.university_name = superDoc.official_name;
  }

  //transformedCollege.institute_type = superDoc.institute_types; // array
  transformedCollege.approved_by = superDoc.approvals; // array

  // extract accreditation names : array

  if (superDoc.accreditations) { // if key exists
    transformedCollege.accredited_to = superDoc.accreditations.map(function(val) {
      return val.name;
    });
  }

  // logo url : assusming superDoc has logo_url
  // TODO: change this to logo_url later
  //transformedCollege.image_link = superDoc.logo_url;

  if(superDoc.gallery.logo){
    transformedCollege.image_link = superDoc.gallery.logo;
  }

  transformedCollege.official_name = superDoc.official_name;
  transformedCollege.ownership = superDoc.ownership;



  // if (superDoc.official_address) { // if key exists
  //   transformedCollege.state = superDoc.official_address.state;
  //   transformedCollege.city = superDoc.official_address.city;
  // }



  if(superDoc.institution_city){
    transformedCollege.city = ConvertInCamelCase(superDoc.institution_city);
  }

  if(superDoc.institution_state){
    transformedCollege.state = ConvertInCamelCase(superDoc.institution_state);
  }



  transformedCollege.year_of_estd = superDoc.established_year;
  transformedCollege.institute_type = superDoc.entity_type;
  transformedCollege.is_client = superDoc.is_client;
  transformedCollege.brochure_url = superDoc.official_url_brochure;
  if (superDoc.paytm_keys) {
    transformedCollege.paytm_keys = superDoc.paytm_keys;
  }
  //transformedCollege.institute_gender = superDoc.genders_accepted; //array



  if (Array.isArray(superDoc.genders_accepted)){
    transformedCollege.institute_gender = [];
    var male = false;
    var female = false;
    for(var z = 0; z < superDoc.genders_accepted.length; z++){
      var gen = superDoc.genders_accepted[z];
      if(gen.toUpperCase() === 'MALE'){
        male = true;
      }
      if(gen.toUpperCase() === 'FEMALE'){
        female = true;
      }
    }

    if(female && male){
          transformedCollege.institute_gender.push('Co-Ed');
    }
    else if(male){
      transformedCollege.institute_gender.push('male');
    } else if(female){
      transformedCollege.institute_gender.push('female');
    }


  }

  if (superDoc.facilities) {
    transformedCollege.facilities = superDoc.facilities;
  }



  // exams accepted: array

  if (Object.keys(superDoc.exam_map).length > 0) {
    transformedCollege.exams_accepted = Object.keys(superDoc.exam_map).map(function (key) {
      return superDoc.exam_map[key];
    });
      transformedCollege.exams_accepted_search = Object.keys(superDoc.exam_map).map(function (key) {
      return superDoc.exam_map[key];
    });
  }
  // setup per stream ranking, max-rank, max-rating

  transformedCollege.ranking = superDoc.rankings; // array of objects

  if (Array.isArray(superDoc.rankings) && superDoc.rankings.length > 0) {
    // sort rankings by year
    superDoc.rankings.sort(function (a, b) {
      return a.year - b.year;
    });

    transformedCollege.max_rank = 1e10 - 1; // max rank possible
    transformedCollege.max_rating = 0.0; // min score/rating possible

    for (var k = 0; k < superDoc.rankings.length; k++) {
      var ranking = superDoc.rankings[k];
      // since list is sorted by year if we have more than one entry

      // of same stream then only latest year's value will/should be saved.

    var ranking = superDoc.rankings[k];
    var rating_prefix;
    var rating_suffix;
    var rating_key;
    if(ranking.source){
        rating_prefix = ranking.source.toLowerCase();
    }
    if(ranking.ranking_type && ranking.ranking_type !== 'STREAM WISE COLLEGES'){
        rating_suffix = ranking.ranking_type.toLowerCase();
    } else if(ranking.ranking_stream){
        rating_suffix = ranking.stream.toLowerCase();
    }
    if(rating_suffix && rating_prefix) {
        rating_key = 'ranking_' + rating_prefix + '_' +  rating_suffix;
    }
    if(rating_key && ranking.score){
        transformedCollege[rating_key] = ranking.score;
    }

     // var stream_rank_key = "ranking_" + rating_prefix + '_' + rating_suffix;
      //transformedCollege[stream_rank_key.toLowerCase()] = ranking.score;
      // NOTE: we are keeping minimum rank in max_rank here.

      if (ranking.rank < transformedCollege.max_rank) {
        transformedCollege.max_rank = ranking.rank;
      }

      // keep max score as max_rating

      if (ranking.score > transformedCollege.max_rating) {
        transformedCollege.max_rating = ranking.score;
      }
    }
  }



  // update courses data
  transformedCollege.courses = [];
  transformedCollege.courses_offered = [];
  for (var i = 0; i <= superDoc.courses.length - 1; i++) {
    var course = superDoc.courses[i];
    // setup array of course names at college level
    transformedCollege.courses_offered.push(course.course_name_official);
    transformedCollege.courses[i] = {};

    transformedCollege.courses[i].course_id = course.course_id;
    transformedCollege.courses[i].name = course.course_name_official;
    transformedCollege.courses[i].degree = course.master_degree; // array
    transformedCollege.courses[i].level = ConvertInCamelCase(course.course_level);
    transformedCollege.courses[i].study_mode = course.study_mode;
    transformedCollege.courses[i].duration_in_months = course.course_duration;
    transformedCollege.courses[i].domain_name = [];
    for (var k = 0; k < course.streams.length; k++) {
        if(course.streams[k].toLowerCase() === 'education' ||
            course.streams[k].toLowerCase() === 'sciences' ||
            course.streams[k].toLowerCase() === 'arts_humanities_and_social_sciences' ){
            transformedCollege.courses[i].domain_name.push('Humanities and Sciences');
        } else {
            transformedCollege.courses[i].domain_name.push(course.streams[k]);
        }
    }
    transformedCollege.courses[i].branch = course.master_branch;
    transformedCollege.courses[i].seats = course.seats_available;


    // exams array

    if (Array.isArray(course.exams_accepted) && Object.keys(superDoc.exam_map).length > 0) {
      transformedCollege.courses[i].exams = course.exams_accepted.map(function (exam_id) {
        return superDoc.exam_map[exam_id];
      }).filter(Boolean); // filter out null/undefined values
      if (transformedCollege.courses[i].exams.length === 0) {
        delete transformedCollege.courses[i].exams;
      }
    }



    if (Array.isArray(course.course_fees) && course.course_fees.length > 0) {
      var college_fees;
      if (course.course_fees.length === 1) {
        college_fees = course.course_fees[0].fee;
      } else {
        var gen_fees = course.course_fees.filter(function (caste_fee) {
          return caste_fee.caste_group === 'GENERAL';
        });
        if (gen_fees.length > 0) {
          college_fees = gen_fees[0].fee;
        }
      }

      if (college_fees) {
        transformedCollege.courses[i].fees = college_fees;
      }
    }
  }



  if (transformedCollege.courses.length === 0) {
    delete transformedCollege.courses;
    delete transformedCollege.courses_offered;
  }
  console.log(JSON.stringify(transformedCollege.institute_id));
  return transformedCollege;

}



/**

 * method called by monstache when any collection is modified.

 * @param doc: complete document object that is updated.

 * @param ns: namespace of collection that is updated in formal "db.collection"

 * @param updateDesc: update description

 * @return document object: if it needs to be saved in ES.

 * @return false: if this change should be ignored.

 */

module.exports = function(doc, ns, updateDesc) {

  var db = ns.split(".")[0];
  var coll = ns.split(".")[1];
  var college_id;

  // check valid dB change

  if (db !== database_name)
    return false;



  if (coll === college_collection && doc.publishing_status === "PUBLISHED") {
    college_id = doc.institute_id;
    console.log("Collection : " + coll + " institute_id : " + doc.institute_id);
  } else if (coll === course_collection && doc.publishing_status === "PUBLISHED") {
    college_id = doc.institute_id;
    if(doc.course_id){
      console.log("Collection : " + coll + " course_id : " + doc.course_id);
    }
  } else {
    // don't handle other collections for now.
    return false;
  }

  var superDocument = getCollegeSuperDocument(college_id);

  if (superDocument === null) {
    return false;
  }



  superDocument = transformCollege(superDocument);



  // update meta

  var meta = { id: superDocument._id, index: target_collection, type: target_doc_type };
  superDocument._meta_monstache = meta;
  return superDocument;

}
