from elasticsearch import Elasticsearch
from elasticsearch.helpers import bulk

es = Elasticsearch(
    ['10.20.33.122'],
    scheme="http",
    port=9200,
)

autosuggestIndex="education_autosuggestion_v2"
autosuggestIndexType="education"
print autosuggestIndex

cityNames=[]
stateNames=[]

# Process hits here
def process_hits(hits):
    for insti in hits:
        #cityNames.append(insti['_source']['city'])
        #stateNames.append(insti['_source']['state'])
    if 'city' in insti['_source'] and insti['_source']['city'] :
        cityNames.append(insti['_source']['city'])
    if 'state' in insti['_source'] and insti['_source']['state']:
        stateNames.append(insti['_source']['state'])
    yield { "_index": autosuggestIndex, "_type":autosuggestIndexType,"_source" : {"names" : insti['_source']['names'], "official_name" : insti['_source']['official_name'], "entity_type": "institute", "entity_id": insti['_source']['institute_id']}, }

def getCityData(cityNames):
    for city in cityNames:
        yield { "_index": autosuggestIndex, "_type":autosuggestIndexType,"_source" : {"names" : [city], "official_name" : city, "entity_type": "city"}, }

def getStateData(stateNames):
    for state in stateNames:
        yield { "_index": autosuggestIndex, "_type":autosuggestIndexType,"_source" : {"names" : [state], "official_name" : state, "entity_type": "state"}, }


print "init scroll.. "


# Init scroll by search
esInstiData = es.search(
    index='education_search_institute_v2',
    doc_type='education',
    scroll='2m',
    size=100,
    body={}
)

print "about to scroll.. "
print esInstiData

# Get the scroll ID
sid = esInstiData['_scroll_id']
print sid
scroll_size = len(esInstiData['hits']['hits'])

# Before scroll, process current batch of hits

bulk(es,process_hits(esInstiData['hits']['hits']))

print "scroll started..."

while scroll_size > 0:
    print "Scrolling..."
    print sid
    data = es.scroll(scroll_id=sid, scroll='2m')

    # Process current batch of hits
    bulk(es,process_hits(data['hits']['hits']))

    # Update the scroll ID
    sid = data['_scroll_id']

    # Get the number of results that returned in the last scroll
    scroll_size = len(data['hits']['hits'])



print "ingesting city auto suggest data:"
print
cityNamesSet=set(cityNames)
bulk(es,getCityData(cityNamesSet))

print "ingesting state auto suggest data:"
print
stateNamesSet=set(stateNames)
bulk(es,getStateData(stateNamesSet))
