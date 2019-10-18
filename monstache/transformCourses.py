from elasticsearch import Elasticsearch
from elasticsearch.helpers import bulk

es = Elasticsearch(
    ['10.20.33.122'],
    scheme="http",
    port=9200,
)

courseIndex="education_search_course_v4"
courseIndexType="education"


def process_hits(hits):
    for insti in hits:
        main_college_doc = { "_index": courseIndex, "_type":courseIndexType,"_source" : {"institute_id" : insti['_source']['institute_id'], "institute_official_name" : insti['_source']['official_name'], "parent_institute_id": insti['_source']['parent_institute_id']}, }
        if 'courses' in insti['_source']:
            all_courses = insti['_source'].get('courses')
            for course in all_courses:
                main_dict = {}
                main_dict = main_college_doc.copy()
                main_dict['_source'].update(course)
                yield main_dict

print "init scroll.. "


# Init scroll by search
esInstiData = es.search(
    index='education_search_institute_v4',
    doc_type='education',
    scroll='2m',
    size=10,
    body={}
)

print "about to scroll.. "

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
