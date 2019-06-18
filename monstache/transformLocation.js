module.exports = function(doc) {
        var targetLocation = {};
        targetLocation.location_id = doc.location_id;
        targetLocation.names = [doc.loc_string];
        targetLocation.official_name = doc.loc_string;
        targetLocation.state_id = doc.state_id;
        targetLocation.city_id = doc.city_id;
        targetLocation.entity_type = 'location';
        console.log ("doc: " + targetLocation);
        return targetLocation;
}
