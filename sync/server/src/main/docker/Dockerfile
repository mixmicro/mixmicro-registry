FROM openjdk:8-jre-alpine
MAINTAINER Elve.Xu <iskp.me@gmail.com>

ARG PROJECT_VERSION
ARG PROJECT_NAME

ENV VERSION ${PROJECT_VERSION}

# processed by base image
RUN echo "http://mirrors.aliyun.com/alpine/v3.8/main" > /etc/apk/repositories \
    && echo "http://mirrors.aliyun.com/alpine/v3.8/community" >> /etc/apk/repositories \
    && apk update upgrade \
    && apk add --no-cache procps unzip curl bash tzdata \
    && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

ADD ${PROJECT_NAME}-${VERSION}.zip /${PROJECT_NAME}/${PROJECT_NAME}-${VERSION}.zip

RUN unzip /${PROJECT_NAME}/${PROJECT_NAME}-${VERSION}.zip -d /${PROJECT_NAME} \
    && rm -rf /${PROJECT_NAME}/${PROJECT_NAME}-${VERSION}.zip

WORKDIR /${PROJECT_NAME}

ENTRYPOINT bin/startup.sh

###
### docker run -p 2663:2663 --rm -e SERVER_ENV="docker" -v /tmp/logs:/tmp/logs -v /tmp/logs/mixmicro-registry-sync-server:/mixmicro-registry-sync-server/logs --name mixmicro-registry-sync-server mixmicro-registry-sync-server:1.0.0.BUILD-SNAPSHOT
###
###