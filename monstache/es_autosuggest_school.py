from elasticsearch import Elasticsearch
from elasticsearch.helpers import bulk

es = Elasticsearch(
    ['10.20.33.122'],
    scheme="http",
    port=9200,
)

autosuggestIndex="education_autosuggestion_v2"
autosuggestIndexType="education"

cityNames = []
stateNames = []

# Process hits here
def process_hits(hits):
    for school in hits:
        names = []
        logo = None
        city = None
        state = None
        if 'city' in school['_source'] and school['_source']['city']:
            cityNames.append(school['_source']['city'])
            city = school['_source']['city']
        if 'state' in school['_source'] and school['_source']['state']:
            stateNames.append(school['_source']['state'])
            state = school['_source']['state']
        if 'names' in school['_source'] and school['_source']['names']:
            for name in school['_source']['names']:
                names.append(name)
        if 'imageLink' in school['_source'] and school['_source']['imageLink']:
            logo = school['_source']['imageLink']
        yield {"_index": autosuggestIndex, "_type": autosuggestIndexType,
               "_source": {"names": names, "official_name": school['_source']['official_name'],
                           "logo": logo, "official_address": {"state": state, "city": city},
                           "entity_type": "school",
                           "entity_id": school['_source']['school_id']}, }


#Get city data for autosuggest ingestion
def getCityData(cityNames):
    for city in cityNames:
        yield {"_index": autosuggestIndex, "_type": autosuggestIndexType,"_id": 'city_'+city,
               "_source": {"names": [city], "official_name": city, "entity_type": "city"}, }

#Get state data for autosuggest ingestion
def getStateData(stateNames):
    for state in stateNames:
        yield {"_index": autosuggestIndex, "_type": autosuggestIndexType,"_id": 'state_'+state,
               "_source": {"names": [state], "official_name": state, "entity_type": "state"}, }


print "initialize scroll.. "
# Init scroll by search
esSchoolData = es.search(
    index='education_search_school_v1',
    scroll='2m',
    size=100,
    body={}
)

print "Going to scroll.. "
# print esSchoolData
# Get the scroll ID
sid = esSchoolData['_scroll_id']
print sid
scroll_size = len(esSchoolData['hits']['hits'])

# Before scroll, process current batch of hits

bulk(es, process_hits(esSchoolData['hits']['hits']))

print "scroll started..."

while scroll_size > 0:
    print "Scrolling..."
    print sid
    data = es.scroll(scroll_id=sid, scroll='2m')

    # Process current batch of hits
    bulk(es, process_hits(data['hits']['hits']))

    # Update the scroll ID
    sid = data['_scroll_id']

    # Get the number of results that returned in the last scroll
    scroll_size = len(data['hits']['hits'])

#print "ingesting city auto suggest data:"
#print
#cityNamesSet = set(cityNames)
#bulk(es, getCityData(cityNamesSet))

#print "ingesting state auto suggest data:"
#print
#stateNamesSet = set(stateNames)
#bulk(es, getStateData(stateNamesSet))


