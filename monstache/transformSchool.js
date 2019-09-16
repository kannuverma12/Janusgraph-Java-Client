var database_name = "digital_education";
var school_collection = "school";
var target_collection = "education_search_school_v1";
var target_doc_type = "education";
var MAX_SAFE_INTEGER = 9007199254740991;


module.exports = function(dbDocument, ns, updateDesc) {

    var db = ns.split(".")[0];
    var collection = ns.split(".")[1];

    if (db !== database_name)
        return false;
    if (collection !== school_collection)
        return false;

    var transformedSchool = transformSchool(dbDocument);

    if (transformedSchool == null)
        return false;

    // update meta
    var meta = { id: transformedSchool._id, index: target_collection, type: target_doc_type };
    transformedSchool._meta_monstache = meta;
    return transformedSchool;
}

function transformSchool(dbDocument) {
    var targetSchool = {};
    targetSchool._id = dbDocument._id;
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
        targetSchool.state = dbDocument.address.state;
        targetSchool.city = dbDocument.address.city;

        if(dbDocument.address.lat_lon !== undefined) {
            var locationData = {};
            var latLonArray = dbDocument.address.lat_lon.split(',');

            if(latLonArray.length == 2) {
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

            if (dbSchoolBoard.data.school_facilities !== undefined) {
                targetSchool.facilities = merge_array(targetSchool.facilities, dbSchoolBoard.data.school_facilities);
            }

            if (dbSchoolBoard.data.medium_of_instruction !== undefined) {
                targetSchool.lang_medium = merge_array(targetSchool.lang_medium, dbSchoolBoard.data.medium_of_instruction);
            }

            if (dbSchoolBoard.data.fees_data !== undefined && Array.isArray(dbSchoolBoard.data.fees_data)
                && dbSchoolBoard.data.fees_data.length > 0) {
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
            }
            targetSchool.boards.push(singleBoard);
        }
    }
    return targetSchool;
}

function merge_array(array1, array2) {
    var result_array = [];
    var arr = array1.concat(array2);
    var len = arr.length;
    var assoc = {};
    while(len--) {
        var item = arr[len];
        if(!assoc[item]) {
            result_array.unshift(item);
            assoc[item] = true;
        }
    }
    return result_array;
}
