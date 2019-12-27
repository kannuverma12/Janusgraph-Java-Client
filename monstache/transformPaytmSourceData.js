

var database_name = "digital-education";
var institute_collection = "institute";
var course_collection = "course";
var exam_collection = "exam";
var school_collection = "school";
var stream_collection = "stream";
var paytm_source_data_collection = "paytm_source_data";
var entity_source_mapping_collection = "entity_source_mapping";
var target_institute_collection = "education_search_institute_v4";
var target_exam_collection = "education_search_exam_v4";
var target_school_collection = "education_search_school_v2";
var target_course_collection = "education_search_course_v4";
var target_doc_type = "education";
var MAX_SAFE_INTEGER = 9007199254740991;

module.exports = function (document, ns, updateDesc) {

	var db = ns.split(".")[0];
	var coll = ns.split(".")[1];
	var college_id;

	if (db !== database_name)
		return false;
	if ((coll !== paytm_source_data_collection) && (coll !== entity_source_mapping_collection))
		return false;

	try {
		info(coll + "," + document.entity_id + ", " + document.entity);
		if (coll === entity_source_mapping_collection)
			document = findPaytmSourceDataForEntity(document.entity_id, document.entity);
		var source = findSourceConfigured(document.entity_id, document.entity);
		if (source === undefined || source !== "PAYTM" || (document.is_active !== true)) {
			info("Paytm source not configured for entity_id : " + document.entity_id + ", skipping. ");
			return false;
		}
		info("Processing doc : " + JSON.stringify(document));
		var entity_data_key = document.entity.toLowerCase() + "_data";
		var doc = document[entity_data_key];
		if (document.entity === "INSTITUTE") {
			if (doc.publishing_status !== "PUBLISHED") {
				warn("Institute_id : " + doc.institute_id + " is not in PUBLISHED status.");
				return false;
			}
			college_id = doc.institute_id;
			info("Processing Collection : " + coll + " institute_id : " + doc.institute_id);
			var superDocument = getCollegeSuperDocument(college_id);
			if (superDocument === null) {
				return false;
			}
			superDocument = transformCollege(superDocument);
			var meta = {
				id: superDocument._id,
				index: target_institute_collection,
				type: target_doc_type
			};
			superDocument._meta_monstache = meta;
			info("Processed Institute : " + doc.institute_id + "," + JSON.stringify(superDocument));
			return superDocument;
		} else if (document.entity === "EXAM") {
			if (doc.published_status !== "PUBLISHED") {
				warn("Exam_id : " + doc.exam_id + " is not in PUBLISHED status.");
				return false;
			}

			var targetExam = transformExam(doc);
			// update meta
			var meta = {
				id: targetExam._id,
				index: target_exam_collection,
				type: target_doc_type
			};
			targetExam._meta_monstache = meta;
			info("Processed Exam : " + doc.exam_id + "," + targetExam);
			return targetExam;
		} else if (document.entity === "SCHOOL") {
			info("Processing school_id : " + document.school_id);
			var transformedSchool = transformSchool(doc);

			if (transformedSchool == null)
				return false;

			// update meta
			var meta = {
				id: transformedSchool._id,
				index: target_school_collection,
				type: target_doc_type
			};
			transformedSchool._meta_monstache = meta;
			info("Processed School : " + doc.school_id + "," + transformedSchool);
			return transformedSchool;
		} else if (document.entity === "COURSE") {
			if (doc.publishing_status !== "PUBLISHED") {
				warn("Course_id : " + doc.course_id + " is not in PUBLISHED status.");
				return false;
			}
			info("Processing course_id : " + doc.course_id);
			var transformedCourse = transformCourse(doc);
			if (transformedCourse == null)
				return false;

			var meta = {
				id: transformedCourse._id,
				index: target_course_collection,
				type: target_doc_type
			};
			transformedCourse._meta_monstache = meta;
			info("Processed Course : " + doc.course_id + "," + transformedCourse);
			return transformedCourse;
		}
	} catch (e) {
		error("Error in processing paytm source entity : " + document.entity_id + " entity  : " + document.entity + ", " + e);
		return false;
	}
	return false;
}

function findCollegeById(college_id) {
	var collegeQueryOptions = {
		database: database_name,
		collection: institute_collection
	};

	var listOfColleges = find({
		institute_id: college_id
	}, collegeQueryOptions);
	if (listOfColleges === undefined || listOfColleges.length !== 1) {
		// requested document not found or invalid case when more then 1 doc found for same id
		warn("Cannot find college with id :" + college_id);
		return null;
	}
	return listOfColleges[0];
}

function findExam(examid) {
	var examQueryOptions = {
		database: database_name,
		collection: exam_collection
	};

	var listOfExams = find({
		exam_id: examid
	}, examQueryOptions);
	if (listOfExams === undefined || listOfExams.length !== 1) {
		// requested document not found or invalid case when more then 1 doc found for same id
		warn("Cannot find Exam with id :" + examid);
		return null;
	}
	return listOfExams[0];
}

function findSchoolById(schoolid) {
	var queryOptions = {
		database: database_name,
		collection: school_collection
	};

	var listOfSchools = find({
		school_id: schoolid
	}, queryOptions);
	if (listOfSchools === undefined || listOfSchools.length !== 1) {
		// requested document not found or invalid case when more then 1 doc found for same id
		warn("Cannot find school with id :" + schoolid);
		return null;
	}
	return listOfSchools[0];
}

function findCourseById(courseid) {
	var queryOptions = {
		database: database_name,
		collection: course_collection
	};

	var listOfCourses = find({
		course_id: courseid
	}, queryOptions);
	if (listOfCourses === undefined || listOfCourses.length !== 1) {
		// requested document not found or invalid case when more then 1 doc found for same id
		warn("Cannot find course with id :" + courseid);
		return null;
	}
	return listOfCourses[0];
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
	var targetCourseMerchantData = findCourseById(doc.course_id);
	transformedCourse._id = targetCourseMerchantData._id;
	transformedCourse.course_id = doc.course_id;
	transformedCourse.name = doc.course_name_official;
	transformedCourse.institute_id = doc.institute_id;

	if (targetCollege.parent_institution) {
		transformedCourse.parent_institute_id = targetCollege.parent_institution;
	} else {
		transformedCourse.parent_institute_id = targetCollege.institute_id;
	}
	transformedCourse.institute_official_name = targetCollege.official_name;
	transformedCourse.degree = doc.master_degree;
	transformedCourse.level = doc.course_level;
	transformedCourse.branch = doc.master_branch;
	transformedCourse.study_mode = doc.study_mode;
	transformedCourse.duration_in_months = doc.course_duration;
	info("course_id : " + doc.course_id + ", lead_enabled = " + doc.lead_enabled);
	transformedCourse.is_accepting_application = doc.lead_enabled;
	transformedCourse.domain_name = [];
	transformedCourse.stream_ids = doc.stream_ids;
	for (var k = 0; k < doc.streams.length; k++) {
		if (doc.streams[k].toLowerCase() === 'education' ||
			doc.streams[k].toLowerCase() === 'sciences' ||
			doc.streams[k].toLowerCase() === 'arts_humanities_and_social_sciences') {
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
	if (Array.isArray(doc.exams_accepted) && doc.exams_accepted.length > 0) {
		var exam_ids = doc.exams_accepted;
		exam_ids.forEach(function (exam_id) {
			var examData = findExam(exam_id);
			if (examData !== null) {
				transformedCourse.exams.push(examData.exam_short_name);
			}
		});

	}
	info("Processed course : " + transformedCourse.course_id);
	return transformedCourse;
}

function findPaytmSourceDataForEntity(entity_id, entity) {
	var queryOptions = {
		database: database_name,
		collection: paytm_source_data_collection
	};

	var listOfEntities = find({
		entity_id: entity_id,
		entity: entity
	}, queryOptions);
	if (listOfEntities === undefined || listOfEntities.length !== 1) {
		// requested document not found or invalid case when more then 1 doc found for same id
		warn("Cannot find entity with  paytm source entity id :" + entity_id + " entity : " + entity);
		return null;
	}
	return listOfEntities[0];
}

function findCollegeByIdInPaytmSourceData(college_id) {
	var collegeQueryOptions = {
		database: database_name,
		collection: paytm_source_data_collection
	};

	var listOfColleges = find({
		entity_id: college_id,
		entity: 'INSTITUTE'
	}, collegeQueryOptions);
	if (listOfColleges === undefined || listOfColleges.length !== 1) {
		// requested document not found or invalid case when more then 1 doc found for same id
		warn("Cannot find college with  paytm source entity id :" + college_id);
		return null;
	}
	return listOfColleges[0].institute_data;
}

function findSourceConfigured(entity_id, entity) {
	var collegeQueryOptions = {
		database: database_name,
		collection: entity_source_mapping_collection
	};

	var mapping = find({
		entity_id: entity_id,
		entity: entity
	}, collegeQueryOptions);
	if (mapping === undefined || mapping.length !== 1) {
		// requested document not found or invalid case when more then 1 doc found for same id
		warn("Cannot find entity with id :" + entity_id);
		return null;
	}
	return mapping[0].entity_source;
}


/**
 * get College document with extra entities embedded like
 * course and exams.
 * @return null: if college not found.
 * @param college_id: int
 */

function getCollegeSuperDocument(college_id) {
	var targetCollege = findCollegeByIdInPaytmSourceData(college_id);
	info("findCollegeByIdInPaytmSourceData : " + JSON.stringify(targetCollege));
	if (targetCollege === null || targetCollege.publishing_status !== "PUBLISHED") {
		warn("Skipping institute_id : " + college_id + " is not in published status. status : " + targetCollege.publishing_status);
		return null;
	}

	var coursesQueryOptions = {
		database: database_name,
		collection: course_collection
	};

	var listOfCourses = find({
		institute_id: college_id,
		publishing_status: "PUBLISHED"
	}, coursesQueryOptions);

	// extract exams

	if (Array.isArray(listOfCourses)) {
		var exam_map = {};

		// unique id of each exam in exam_map
		// exam_map will be in form = { 1: true, 3: true }

		listOfCourses.forEach(function (course) {
			if (Array.isArray(course.exams_accepted)) {
				course.exams_accepted.forEach(function (exam_id) {
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

		var listOfExams = find({
			exam_id: {
				$in: listOfExamIds
			},
			published_status: "PUBLISHED"
		}, examsQueryOptions);

		/*
		 * populate exam map in target College Object
		 *
		 * it will be in format = { 1: "Exam A", 3: "Exam B" }
		 */

		targetCollege.exam_map = {};
		if (Array.isArray(listOfExams)) {
			listOfExams.forEach(function (exam) {
				targetCollege.exam_map[exam.exam_id] = exam.exam_short_name;
			})
		}
	}

	targetCollege.courses = listOfCourses;
	return targetCollege;
}

function getParentUniversity(entity_id) {
	var entity = findCollegeByIdInPaytmSourceData(entity_id);
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
	if (stringName != undefined || stringName != null) {
		var stringArray = stringName.split(" ");
		var resultString = "";

		stringArray.forEach(function (tempString) {
			if (tempString.toLocaleLowerCase() === "and") {
				resultString = resultString + tempString.toLocaleLowerCase();
			} else if (tempString.length > 0) {
				var part1 = tempString[0];
				var part2 = tempString.substring(1, tempString.length);
				if (part1) {
					resultString = resultString + part1.toUpperCase();
				}
				if (part2) {
					resultString = resultString + part2.toLowerCase();
				}
			}
			resultString = resultString + " ";
		});
		return resultString.trim();
	}
}

function transformCollege(superDoc) {
	var transformedCollege = {};
	var targetCollege = findCollegeById(superDoc.institute_id);
	transformedCollege._id = targetCollege._id;
	transformedCollege.institute_id = superDoc.institute_id;


	if (superDoc.parent_institution !== undefined) {
		transformedCollege.parent_institute_id = superDoc.parent_institution;
	} else {
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

	if (superDoc.parent_institution) {
		var university_college = getParentUniversity(superDoc.parent_institution);
		if (university_college !== null) {
			transformedCollege.university_name = university_college.official_name;
		}
	} else {
		transformedCollege.university_name = superDoc.official_name;
	}

	//transformedCollege.institute_type = superDoc.institute_types; // array
	transformedCollege.approved_by = superDoc.approvals; // array

	// extract accreditation names : array

	if (superDoc.accreditations) { // if key exists
		transformedCollege.accredited_to = superDoc.accreditations.map(function (val) {
			return val.name;
		});
	}

	// logo url : assusming superDoc has logo_url
	// TODO: change this to logo_url later
	//transformedCollege.image_link = superDoc.logo_url;

	if (superDoc.gallery.logo) {
		transformedCollege.image_link = superDoc.gallery.logo;
	}

	transformedCollege.official_name = superDoc.official_name;
	transformedCollege.ownership = superDoc.ownership;

	// if (superDoc.official_address) { // if key exists
	//   transformedCollege.state = superDoc.official_address.state;
	//   transformedCollege.city = superDoc.official_address.city;
	// }

	if (superDoc.institution_city) {
		transformedCollege.city = ConvertInCamelCase(superDoc.institution_city);
	}

	if (superDoc.institution_state) {
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

	if (Array.isArray(superDoc.genders_accepted)) {
		transformedCollege.institute_gender = [];
		var male = false;
		var female = false;
		for (var z = 0; z < superDoc.genders_accepted.length; z++) {
			var gen = superDoc.genders_accepted[z];
			if (gen.toUpperCase() === 'MALE') {
				male = true;
			}
			if (gen.toUpperCase() === 'FEMALE') {
				female = true;
			}
		}

		if (female && male) {
			transformedCollege.institute_gender.push('Co-Ed');
		} else if (male) {
			transformedCollege.institute_gender.push('male');
		} else if (female) {
			transformedCollege.institute_gender.push('female');
		}

	}

	if (superDoc.facilities) {
		transformedCollege.facilities = superDoc.facilities;
	}

	// exams accepted: array

	if (Object.keys(superDoc.exam_map).length > 0) {
		transformedCollege.exams_accepted = Object.keys(superDoc.exam_map)
			.filter(function (key) {
				return superDoc.exam_map[key];
			})
			.map(function (key) {
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
			var rating_prefix = "";
			var rating_suffix = "";
			var rating_key = "";
			if (ranking.source) {
				rating_prefix = ranking.source.toLowerCase();
			}
			if (ranking.ranking_type && ranking.ranking_type !== 'STREAM WISE COLLEGES') {
				rating_suffix = ranking.ranking_type.toLowerCase();
			} else if (ranking.stream) {
				rating_suffix = ranking.stream.toLowerCase();
			}
			if (rating_suffix && rating_prefix) {
				rating_key = 'ranking_' + rating_prefix + '_' + rating_suffix;
			}
			if (rating_key && ranking.score) {
				transformedCollege[rating_key] = ranking.score;
			}

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
		transformedCollege.courses[i].stream_ids = course.stream_ids;
		for (var k = 0; k < course.streams.length; k++) {
			if (course.streams[k].toLowerCase() === 'education' ||
				course.streams[k].toLowerCase() === 'sciences' ||
				course.streams[k].toLowerCase() === 'arts_humanities_and_social_sciences') {
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
	info("Transformed college : " + JSON.stringify(transformedCollege.institute_id));
	return transformedCollege;

}

function transformExam(doc) {
	var targetExam = {};
	var targetExamMerchantData = findExam(doc.exam_id);
	info("targetExamMerchantData : " + JSON.stringify(targetExamMerchantData));
	targetExam._id = targetExamMerchantData._id;
	targetExam.exam_id = doc.exam_id;
	targetExam.official_name = doc.exam_full_name;
	targetExam.image_link = doc.logo;

	if (Array.isArray(doc.synonyms)) {
		targetExam.exam_name_synonyms = doc.synonyms;
	}

	if (doc.exam_full_name) {
		targetExam.exam_full_name = doc.exam_full_name;
	}

	if (doc.exam_short_name) {
		targetExam.exam_short_name = doc.exam_short_name;
	}
	var tags = [];
	if (doc.domains) {
		if (doc.domains === 'MANAGEMENT_AND_BUSINESS_ADMINISTRATION') {
			tags.push('mba');
		}
		if (doc.domains === 'ENGINEERING_AND_ARCHITECTURE') {
			tags.push('engineering');
		}
		if (doc.domains === 'COMPETITION') {
			tags.push('government');
		}
		targetExam.tags = tags;
	}

	targetExam.level = doc.level_of_exam;
	targetExam.linguistic_medium = doc.linguistic_medium_exam; // array
	targetExam.domain_name = doc.domains;
	if (doc.paytm_keys) {
		targetExam.paytm_keys = doc.paytm_keys;
	}

	var doc_subexams_events = {};
	var doc_subexams_syllabus = {};

	if (Array.isArray(doc.subexams)) {
		for (var i = 0; i < doc.subexams.length; i++) {
			var sub_exam = doc.subexams[i];

			if (sub_exam.published_status === "PUBLISHED" && Array.isArray(sub_exam.instances)) {
				for (var j = 0; j < sub_exam.instances.length; j++) {
					var sub_exam_instance = sub_exam.instances[j];
					if (sub_exam_instance.parent_instance_id) {
						// build events array
						var doc_subexams_instance_events_array = doc_subexams_events[sub_exam_instance.parent_instance_id];
						if (!Array.isArray(doc_subexams_instance_events_array)) {
							doc_subexams_instance_events_array = [];
						}

						if (Array.isArray(sub_exam_instance.events)) {
							doc_subexams_instance_events_array = doc_subexams_instance_events_array.concat(sub_exam_instance.events);
							doc_subexams_events[sub_exam_instance.parent_instance_id] = doc_subexams_instance_events_array;
						}

						// build syllabus array
						var doc_subexams_instance_syllabus_array = doc_subexams_syllabus[sub_exam_instance.parent_instance_id];
						if (!Array.isArray(doc_subexams_instance_syllabus_array)) {
							doc_subexams_instance_syllabus_array = [];
						}
						if (Array.isArray(sub_exam_instance.syllabus)) {
							doc_subexams_instance_syllabus_array = doc_subexams_instance_syllabus_array.concat(sub_exam_instance.syllabus);
							doc_subexams_syllabus[sub_exam_instance.parent_instance_id] = doc_subexams_instance_syllabus_array;
						}
					}
				}
			}
		}
	}

	targetExam.tabs_available = [];
	var event_availale = false;
	var syllabus_available = false;

	if (doc.documents_counselling || doc.Counselling) {
		targetExam.tabs_available.push('Counselling');
	}

	if (doc.application_process) {
		targetExam.tabs_available.push('Application');
	}

	if (doc.Result) {
		targetExam.tabs_available.push('Result');
	}

	if (Array.isArray(doc.instances)) {
		targetExam.instances = [];

		for (var i = 0; i < doc.instances.length; i++) {
			var instance = doc.instances[i];
			if (doc_subexams_events[instance.instance_id]) {
				var sub_exam_events = doc_subexams_events[instance.instance_id];
				// merge events array
				if (!instance.events) {
					instance.events = [];
				}
				instance.events = instance.events.concat(sub_exam_events);
			}

			if (doc_subexams_syllabus[instance.instance_id]) {
				var sub_exam_syllabus = doc_subexams_syllabus[instance.instance_id];
				// merge syllabus array
				if (!instance.syllabus) {
					instance.syllabus = [];
				}
				instance.syllabus = instance.syllabus.concat(sub_exam_syllabus);
			}

			if (instance.events && (instance.events.length > 0) && instance.syllabus && (instance.syllabus.length > 0)) {
				event_availale = true;
				syllabus_available = true;
			}

			var targetInstance = {};
			targetInstance.instance_id = instance.instance_id;
			targetInstance.admission_year = instance.admission_year;

			targetInstance.events = [];
			// instance dates

			if (Array.isArray(instance.events)) {
				// insert subexam events to process them together
				for (var j = 0; j < instance.events.length; j++) {
					var event = instance.events[j];
					// if undefined continue
					if (!event) {
						continue;
					}
					var targetEvent = {};
					if (event.event_id) {
						targetEvent.event_id = event.event_id;
					}
					if (event.type) {
						targetEvent.type = event.type;
					}
					if (event.certainty) {
						targetEvent.certainty = event.certainty;
					}
					if (event.date_start) {
						targetEvent.date_range_start = event.date_start;
					}

					if (event.date_end) {
						targetEvent.date_range_end = event.date_end;
					}

					if (event.date) {
						targetEvent.date = event.date;
					}

					if (event.month) {
						targetEvent.month = event.month;
					}

					targetInstance.events.push(targetEvent);
				}
			}
			targetExam.instances.push(targetInstance);
		}
	}

	if (event_availale) {
		targetExam.tabs_available.push('Dates');
	}
	if (syllabus_available) {
		targetExam.tabs_available.push('Syllabus');
	}

	if (doc.priority) {
		targetExam.global_priority = doc.priority;
	}

	if (doc.stream_ids != undefined && Array.isArray(doc.stream_ids)) {
		targetExam.stream_ids = doc.stream_ids;
		targetExam.streams = {};
		for (var i = 0; i < doc.stream_ids.length; i++) {
			targetExam.streams[doc.stream_ids[i]] = {
				"position": i
			};
		}
		var stream_names = getStreamNamesFromStreamIds(doc.stream_ids);
		if (stream_names !== null) {
			targetExam.stream_names = stream_names;
		}
	}
	info("Processed exam : " + targetExam.exam_id);
	return targetExam;
}

function transformSchool(dbDocument) {
	var targetSchool = {};
	try {
		var targetSchoolMerchantData = findSchoolById(dbDocument.school_id);
		targetSchool._id = targetSchoolMerchantData._id;
		targetSchool.school_id = dbDocument.school_id;
		targetSchool.official_name = dbDocument.name;

		targetSchool.names = [];
		if (dbDocument.name) {
			targetSchool.names = targetSchool.names.concat(dbDocument.name);
		}

		if (dbDocument.former_name) {
			targetSchool.names = targetSchool.names.concat(dbDocument.former_name);
		}

		if (dbDocument.short_name) {
			targetSchool.names = targetSchool.names.concat(dbDocument.short_name);
		}

		targetSchool.area_name = dbDocument.area_name;

		if (dbDocument.address !== undefined) {
			info(JSON.stringify(dbDocument.address));
			targetSchool.state = dbDocument.address.state;
			targetSchool.city = dbDocument.address.city;
			targetSchool.street_address = dbDocument.address.street_address;

			if (dbDocument.address.lat_lon !== undefined) {
				var locationData = {};
				var latLonArray = dbDocument.address.lat_lon.split(',');

				if (latLonArray !== undefined && latLonArray.length == 2) {
					locationData.lat = latLonArray[0];
					locationData.lon = latLonArray[1];
					targetSchool.location = locationData;
				}
			}
		}

		targetSchool.year_of_estd = dbDocument.established_year;

		if (dbDocument.gallery !== undefined && dbDocument.gallery.logo) {
			targetSchool.image_link = dbDocument.gallery.logo;
		}

		targetSchool.is_client = dbDocument.is_client;
		if (dbDocument.paytm_keys) {
			targetSchool.paytm_keys = dbDocument.paytm_keys;
		}

		targetSchool.facilities = [];
		targetSchool.lang_medium = [];
		targetSchool.boards = [];

		if (dbDocument.boards !== undefined) {
			for (var i = 0; i < dbDocument.boards.length; i++) {
				var dbSchoolBoard = dbDocument.boards[i];
				var singleBoard = {};
				singleBoard.board_name = dbSchoolBoard.name;
				singleBoard.education_level = dbSchoolBoard.data.education_level;
				singleBoard.class_from = dbSchoolBoard.data.class_from;
				singleBoard.class_to = dbSchoolBoard.data.class_to;
				if (dbSchoolBoard.data.gender && dbSchoolBoard.data.gender !== "NOT_PROVIDED")
					singleBoard.gender_accepted = dbSchoolBoard.data.gender;
				if (dbSchoolBoard.data.ownership && dbSchoolBoard.data.ownership !== "NOT_PROVIDED")
					singleBoard.ownership = dbSchoolBoard.data.ownership;
				singleBoard.affiliation_type = dbSchoolBoard.data.affiliation_type;
				singleBoard.residential_status = dbSchoolBoard.data.residential_status;
				singleBoard.brochure_url = dbSchoolBoard.data.school_brochure_link;

				if (dbSchoolBoard.data.school_facilities !== undefined) {
					targetSchool.facilities = merge_array(targetSchool.facilities, dbSchoolBoard.data.school_facilities);
				}

				if (dbSchoolBoard.data.medium_of_instruction !== undefined) {
					targetSchool.lang_medium = merge_array(targetSchool.lang_medium, dbSchoolBoard.data.medium_of_instruction);
				}

				if (dbSchoolBoard.data.fees_data !== undefined && Array.isArray(dbSchoolBoard.data.fees_data) &&
					dbSchoolBoard.data.fees_data.length > 0) {
					var min_fee = MAX_SAFE_INTEGER;
					dbSchoolBoard.data.fees_data.forEach(function (fee) {
						var cur_fee = 0;
						if (fee.fees_tenure && fee.fees_tenure === "Yearly") {
							cur_fee = fee.fees;
						} else if (fee.fees_tenure && fee.fees_tenure === "Half-Yearly") {
							cur_fee = fee.fees * 2;
						} else if (fee.fees_tenure && fee.fees_tenure === "Quaterly") {
							cur_fee = fee.fees * 4;
						} else if (fee.fees_tenure && fee.fees_tenure === "Monthly") {
							cur_fee = fee.fees * 12;
						}
						if (cur_fee < min_fee) {
							min_fee = cur_fee;
						}
					});
					if (min_fee != MAX_SAFE_INTEGER && min_fee != 0)
						singleBoard.fees = min_fee;

					if (dbSchoolBoard.data.contact_number_1 !== undefined && dbSchoolBoard.data.contact_number_1 !== '') {
						singleBoard.contact_number = dbSchoolBoard.data.contact_number_1;
					} else if (dbSchoolBoard.data.contact_number_2 !== undefined && dbSchoolBoard.data.contact_number_2 !== '') {
						singleBoard.contact_number = dbSchoolBoard.data.contact_number_2;
					}
				}
				targetSchool.boards.push(singleBoard);
			}
		}
		info("Processed school : " + targetSchool.school_id);
		return targetSchool;
	} catch (e) {
		error("Error in processing school : " + dbDocument.school_id + ", " + JSON.stringify(e));
		return false;
	}
}

function merge_array(array1, array2) {
	var result_array = [];
	var arr = array1.concat(array2);
	var len = arr.length;
	var assoc = {};
	while (len--) {
		var item = arr[len];
		if (!assoc[item]) {
			result_array.unshift(item);
			assoc[item] = true;
		}
	}
	return result_array;
}

function getStreamNamesFromStreamIds(stream_ids) {
	if (stream_ids === null) {
		return null;
	}

	var streamQueryOptions = {
		database: database_name,
		collection: stream_collection
	};

	var listOfStreams = find({
		stream_id: {
			$in: stream_ids
		}
	}, streamQueryOptions);

	if (listOfStreams != undefined && Array.isArray(listOfStreams)) {
		var stream_names = [];
		listOfStreams.forEach(function (stream) {
			stream_names.push(stream.name);
		});
	}

	return stream_names;
}

function info(message) {
	console.log("INFO : " + currentTimestamp() + " " + message);
}

function error(message) {
	console.error("ERROR : " + currentTimestamp() + " " + message);
}

function warn(message) {
	console.warn("WARN : " + currentTimestamp() + " " + message);
}

function currentTimestamp() {
	return new Date().toISOString();
}

