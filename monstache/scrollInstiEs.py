from elasticsearch import Elasticsearch
from elasticsearch.helpers import bulk

es = Elasticsearch(
    ['http://10.20.33.122:9200','http://10.20.33.133:9200','http://10.20.33.242:9200'],
    scheme="http",
    port=9200,
)

autosuggestIndex = "education_autosuggestion_v2"
autosuggestIndexType = "education"
# print autosuggestIndex

cityNames = []
stateNames = []


# Process hits here
def process_hits(hits):
    for insti in hits:
        names = []
        logo = None
        city = None
        state = None
        # cityNames.append(insti['_source']['city'])
        # stateNames.append(insti['_source']['state'])
        if 'city' in insti['_source'] and insti['_source']['city']:
            cityNames.append(insti['_source']['city'])
        if 'state' in insti['_source'] and insti['_source']['state']:
            stateNames.append(insti['_source']['state'])
        if 'university_name' in insti['_source'] and insti['_source']['university_name']:
            names.append(insti['_source']['university_name'])
        if 'official_name' in insti['_source'] and insti['_source']['official_name']:
            names.append(insti['_source']['official_name'])
        if 'former_name' in insti['_source'] and insti['_source']['former_name']:
            for name in insti['_source']['former_name']:
                names.append(name)
        if 'alternate_names' in insti['_source'] and insti['_source']['alternate_names']:
            for name in insti['_source']['alternate_names']:
                names.append(name)
        if 'common_name' in insti['_source'] and insti['_source']['common_name']:
            names.append(insti['_source']['common_name'])
        if 'image_link' in insti['_source'] and insti['_source']['image_link']:
            logo = insti['_source']['image_link']
        if 'stste' in insti['_source'] and insti['_source']['state']:
            state = insti['_source']['state']
        if 'city' in insti['_source'] and insti['_source']['city']:
            city = insti['_source']['city']
        yield {"_index": autosuggestIndex, "_type": autosuggestIndexType,
               "_source": {"names": names, "official_name": insti['_source']['official_name'],
                           "logo": logo, "official_address": {"state": state, "city": city},
                           "entity_type": "institute",
                           "entity_id": insti['_source']['institute_id']}, }


def getCityData(cityNames):
    for city in cityNames:
        yield {"_index": autosuggestIndex, "_type": autosuggestIndexType,
               "_source": {"names": [city], "official_name": city, "entity_type": "city"}, }


def getStateData(stateNames):
    for state in stateNames:
        yield {"_index": autosuggestIndex, "_type": autosuggestIndexType,
               "_source": {"names": [state], "official_name": state, "entity_type": "state"}, }


print "init scroll.. "

# Init scroll by search
esInstiData = es.search(
    index='education_search_institute_v4',
    doc_type='education',
    scroll='2m',
    size=100,
    body={}
)

print "about to scroll.. "
# print esInstiData

# Get the scroll ID
sid = esInstiData['_scroll_id']
print sid
scroll_size = len(esInstiData['hits']['hits'])

# Before scroll, process current batch of hits

bulk(es, process_hits(esInstiData['hits']['hits']))

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

print "ingesting city auto suggest data:"
print
cityNamesSet = set(cityNames)
bulk(es, getCityData(cityNamesSet))

print "ingesting state auto suggest data:"
print
stateNamesSet = set(stateNames)
bulk(es, getStateData(stateNamesSet))
