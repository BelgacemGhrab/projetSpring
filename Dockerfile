FROM anapsix/alpine-java:8u202b08_jdk

ENTRYPOINT exec java -jar /app.jar

COPY target/*.jar /app.jar