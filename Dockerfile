FROM tomcat:11.0.25-jre25-temurin-noble

RUN rm -rf /usr/local/tomcat/webapps/*

COPY target/eks-transactions-service.war \
     /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080