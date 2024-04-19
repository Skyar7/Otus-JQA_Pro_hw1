FROM maven:3.9.6-eclipse-temurin-8

USER root

RUN mkdir -p /home/ubuntu/ui_tests

WORKDIR /home/ubuntu/ui_tests

COPY . /home/ubuntu/ui_tests

ENTRYPOINT ["/bin/bash", "entrypoint.sh"]