/*
 * Data transformation script for EDU-Explore exams data
 *
 * @author: shashank.chhikara
 */

module.exports = function(doc) {

	if (doc.publishedStatus !== "PUBLISHED") {
		return false;
	}

	var targetExam = {};
	targetExam.exam_id = doc.exam_id;
	targetExam.official_name = doc.exam_full_name;

	targetExam.names = [];
	if (Array.isArray(doc.synonyms)) {
		targetExam.names = targetExam.names.concat(doc.synonyms);
	}
	if (doc.exam_full_name) {
		targetExam.names.push(doc.exam_full_name);
	}
	if (doc.exam_short_name) {
		targetExam.names.push(doc.exam_short_name);
	}

	targetExam.level = doc.level_of_exam;
	targetExam.language = doc.linguistic_medium_exam; // array
	targetExam.logo_url = "";

	// build temporary subexams data per parent instance id
	var doc_subexams = {};
	if (Array.isArray(doc.subexams)) {
		for (var i = 0; i < doc.subexams.length; i++) {
			var sub_exam = doc.subexams[i];

			if (sub_exam.publishedStatus === "PUBLISHED" && Array.isArray(sub_exam.instances)) {
				for (var j = 0; j < sub_exam.instances.length; j++) {
					var sub_exam_instance = sub_exam.instances[j];

					if (sub_exam_instance.parentInstanceId) {
						var doc_subexams_instance_array = doc_subexams[sub_exam_instance.parentInstanceId];
						if (!Array.isArray(doc_subexams_instance_array)) {
							doc_subexams_instance_array = [];
						}
						doc_subexams_instance_array = doc_subexams_instance_array.concat(sub_exam_instance.events);
						doc_subexams[sub_exam_instance.parentInstanceId] = doc_subexams_instance_array;
					}
				}
			}
		}
	}

    // setup tabs info
	targetExam.tabs_available = [];
    var event_available = false;
    var syllabus_available = false;

    if (doc.documentsCounselling || doc.Counselling) {
        targetExam.tabs_available.push('Counselling');
    }

    if (doc.application_process) {
        targetExam.tabs_available.push('Application');
    }

    if (doc.result) { // this'll be available later
        targetExam.tabs_available.push('Result');
    }

	if (Array.isArray(doc.instances)) {
		targetExam.instances = [];

		for (var i = 0; i < doc.instances.length; i++) {
			var instance = doc.instances[i];

			if (instance.events) {
			    event_available = true;
			}
			if (instance.syllabus) {
                syllabus_available = true;
            }

			var targetInstance = {};
			targetInstance.instance_id = instance.instanceId;
			targetInstance.admission_year = instance.admissionYear;

			// dates
			targetInstance.exam_dates = [];
			targetInstance.result_dates = [];
			targetInstance.application_dates = [];

			// instance dates
			if (Array.isArray(instance.events)) {
				// insert subexam events to process them together
				if (doc_subexams[instance.instanceId]) {
					instance.events = instance.events.concat(doc_subexams[instance.instanceId]);
				}

				for (var j = 0; j < instance.events.length; j++) {
					var event = instance.events[j];

					if (event.type === "APPLICATION") {
						targetInstance.application_dates.push(event.month);
					} else if (event.type === "RESULTS") {
						targetInstance.result_dates.push(event.month);
					} else if (event.type === "EXAM") {
						targetInstance.exam_dates.push(event.month);
					}
				}
			}

			targetExam.instances.push(targetInstance);
		}
	}

	if (event_available) {
        targetExam.tabs_available.push('Dates');
    }
    if (syllabus_available) {
        targetExam.tabs_available.push('Syllabus');
    }

	return targetExam;
}
