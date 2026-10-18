# syntax=docker/dockerfile:1
# Build context is diagrammer-code-project/ (see render.yaml).
# Vaadin 25 has no production Maven profile: ./mvnw package writes the
# production frontend bundle and leaves the optional vaadin-dev tools out.

FROM eclipse-temurin:21-jdk-noble AS build

RUN apt-get update \
    && apt-get install -y --no-install-recommends ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Node 24 LTS for the Hilla frontend build. The Maven wrapper is the build tool.
COPY --from=node:24-bookworm-slim /usr/local/bin/node /usr/local/bin/node
COPY --from=node:24-bookworm-slim /usr/local/lib/node_modules /usr/local/lib/node_modules
RUN ln -sf ../lib/node_modules/npm/bin/npm-cli.js /usr/local/bin/npm \
    && ln -sf ../lib/node_modules/npm/bin/npx-cli.js /usr/local/bin/npx \
    && node --version \
    && npm --version

WORKDIR /src

# Dependency layer. pom and the wrapper change less often than sources.
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw \
    && ./mvnw -B -DskipTests dependency:go-offline dependency:resolve-plugins

# Frontend lockfile before sources so npm installs can reuse this layer.
COPY package.json package-lock.json ./
RUN npm ci

COPY tsconfig.json types.d.ts vite.config.ts ./
COPY src src

RUN ./mvnw -B -DskipTests package \
    && mkdir -p /out \
    && cp target/diagrammer-*.jar /out/app.jar

FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S -g 1001 diagrammer \
    && adduser -S -D -u 1001 -G diagrammer -h /app diagrammer

WORKDIR /app

COPY --from=build --chown=diagrammer:diagrammer /out/app.jar /app/app.jar
COPY --chown=diagrammer:diagrammer docker/entrypoint.sh /app/entrypoint.sh
RUN chmod 755 /app/entrypoint.sh

# s3 is the production user store. local/test are rejected by the entrypoint.
# Heap stays under a 512 MB instance: 55% of the cgroup limit, plus capped
# metaspace and code cache. JAVA_TOOL_OPTIONS is read by the JVM itself.
ENV SPRING_PROFILES_ACTIVE=s3 \
    SERVER_ADDRESS=0.0.0.0 \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=55.0 -XX:InitialRAMPercentage=20.0 -XX:MaxMetaspaceSize=128m -XX:ReservedCodeCacheSize=64m -Xss256k -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/urandom"

EXPOSE 8080

USER diagrammer

ENTRYPOINT ["/app/entrypoint.sh"]
