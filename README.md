# Janusgraph-Java-Client
A simplistic Java client for Connecting to Janusgraph


How to run:
1. Go to project directory.
2. Execute the following command:
    mvn clean install
3. To load the data to graph, run the java file GraphBulkUpload.java.
4. To get the nodes count, run the java file FollowCountNormal.java.
5. To get the nodes count using spark, uncomment the dependencies in pom.xml, mvnc clean install and
   run the java file FollowCountSpark.java.
