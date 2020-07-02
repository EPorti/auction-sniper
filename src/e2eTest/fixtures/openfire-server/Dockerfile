FROM alpine/git:latest AS repo
ARG OPENFIRE_VERSION
WORKDIR /src
RUN git clone --branch v${OPENFIRE_VERSION} --single-branch https://github.com/igniterealtime/Openfire.git .

FROM openjdk:8-jdk-alpine AS builder
WORKDIR /src
COPY --from=repo /src .
RUN chmod 755 mvnw && ./mvnw verify -pl distribution -am

FROM openjdk:8-jre-alpine
WORKDIR /usr/local/openfire
COPY --from=builder /src/distribution/target/distribution-base .
COPY openfire.xml ./conf/
EXPOSE 5222/tcp 9090/tcp 9091/tcp
ENTRYPOINT ["./bin/openfire.sh"]
