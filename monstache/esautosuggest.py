from elasticsearch import Elasticsearch
from elasticsearch.helpers import bulk

es = Elasticsearch(
    ['10.20.33.122'],
    scheme="http",
    port=9200,
)

autosuggestIndex="education_autosuggestion_test"
autosuggestIndexType="education"


instiESData= es.search(index='education_search_institute_v1', filter_path=['hits.hits._source.names', 'hits.hits._source.official_name', 'hits.hits._source.city', 'hits.hits._source.state', 'hits.hits._source.institute_id'],size=10000)
examESData= es.search(index='education_search_exam_v1', filter_path=['hits.hits._source.names', 'hits.hits._source.official_name','hits.hits._source.exam_id'],size=10000)

instiNames= instiESData['hits']['hits']
examNames= examESData['hits']['hits']
cityNames=[]
stateNames=[]
streams = ["Animation and Design","Arts, Humanities and Social Sciences","Commerce","Competition","Computer Application and IT","Education","Engineering and Architecture","Hospitality and Tourism","Law","Management and Business Administration","Media, Mass Communication and Journalism","Medicine and Allied Sciences","Pharmacy","School","Sciences","Study Abroad","University"]

def getInstiData(instiNames):
	for insti in instiNames: 
		if 'city' in insti['_source'] and insti['_source']['city'] :	
			cityNames.append(insti['_source']['city'])
		if 'state' in insti['_source'] and insti['_source']['state']:
			stateNames.append(insti['_source']['state'])
		yield { "_index": autosuggestIndex, "_type":autosuggestIndexType,"_source" : {"names" : insti['_source']['names'], "official_name" : insti['_source']['official_name'], "entity_type": "institute", "entity_id": insti['_source']['institute_id']}, }


def genExamData(examNames):
	for exam in examNames:
		print exam
		print
		yield { "_index": autosuggestIndex, "_type":autosuggestIndexType,"_source" : {"names" : exam['_source']['names'], "official_name" : exam['_source']['official_name'], "entity_type": "exam", "entity_id": exam['_source']['exam_id']}, }


def getCityData(cityNames):
	for city in cityNames:
		yield { "_index": autosuggestIndex, "_type":autosuggestIndexType,"_source" : {"names" : [city], "official_name" : city, "entity_type": "city"}, }

def getStateData(stateNames):
	for state in stateNames:
		yield { "_index": autosuggestIndex, "_type":autosuggestIndexType,"_source" : {"names" : [state], "official_name" : state, "entity_type": "state"}, }


def getStreamsData(streamNames):
	for stream in streamNames:
		yield { "_index": autosuggestIndex, "_type":autosuggestIndexType,"_source" : {"names" : [stream], "official_name" : stream, "entity_type": "stream"}, }

print "ingesting streams auto suggest data:"
print
bulk(es,getStreamsData(streams))

print "ingesting exams auto suggest data:"
print
bulk(es,genExamData(examNames))

#print "ingesting institute auto suggest data:"
#print
#bulk(es,getInstiData(instiNames))
#print cityNames

#print "ingesting city auto suggest data:"
#print
#cityNamesSet=set(cityNames)
#bulk(es,getCityData(cityNamesSet))

#print "ingesting state auto suggest data:"
#print
#stateNamesSet=set(stateNames)
#bulk(es,getStateData(stateNamesSet))


