/*
 * Data transformation script for EDU-Explore exams data
 *
 * @author: shashank.chhikara
 */

module.exports = function(doc) {
	if (doc.published_status !== "PUBLISHED") {
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

//	targetExam.category = doc.exam_category;
	// mode : Online/Offline
//	targetExam.mode = "";

	targetExam.level = doc.level_of_exam;
	targetExam.linguistic_medium = doc.linguistic_medium_exam; // array

	//targetExam.logo_url = "";
	// targetExam.type = doc.exam_category;
	// build temporary subexams data per parent instance id

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

	if (doc.documents_counselling || doc.Counselling){
		targetExam.tabs_available.push('Counselling');
	}

	if (doc.application_process){
		targetExam.tabs_available.push('Application');
	}

	if (doc.Result){
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

			if(instance.events && (instance.events.length > 0) && instance.syllabus && (instance.syllabus.length > 0)){
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
					if(event.event_id){
						targetEvent.event_id = event.event_id;
					}
					if(event.type){
						targetEvent.type = event.type;
					}
					if(event.certainty){
						targetEvent.certainty = event.certainty;
					}
					if(event.date_start){
						targetEvent.date_range_start = event.date_start;
					}

					if(event.date_end){
						targetEvent.date_range_end = event.date_end;
					}

					if(event.date){
						targetEvent.date = event.date;
					}

					if(event.month){
						targetEvent.month = event.month;
					}

					targetInstance.events.push(targetEvent);
				}
			}
			targetExam.instances.push(targetInstance);
		}
	}

	if (event_availale){
		targetExam.tabs_available.push('Dates');
	}
	if (syllabus_available){
		targetExam.tabs_available.push('Syllabus');
	}

	//console.log ("exam: " + targetExam.exam_id );
	return targetExam;
}