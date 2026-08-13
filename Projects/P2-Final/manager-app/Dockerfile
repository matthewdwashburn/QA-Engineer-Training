# Three stages: compile the jar, build a JRE containing only the modules this
# app uses, then assemble a runtime image from the two.
#
# Produces ~61MB vs ~90MB for a stock Alpine JRE. See ./Dockerfile.jre for the
# untrimmed fallback and when to reach for it.

# ---------- Stage 1: build the jar ----------
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /build

# pom.xml before src/ so editing a .java file reuses the cached dependency
# download instead of resolving the whole tree again.

COPY manager-app/pom.xml .
RUN mvn -B dependency:go-offline

COPY manager-app/src ./src
RUN mvn -B clean package -DskipTests

# ---------- Stage 2: build a custom runtime ----------
# Needs the JDK, not a JRE: jdeps and jlink are JDK tools. Must be an Alpine
# JDK so the runtime it emits is musl-linked to match stage 3's base -- mixing
# musl and glibc yields "not found" on a binary that plainly exists.

FROM eclipse-temurin:17-jdk-alpine AS jre-build

WORKDIR /build
COPY --from=build /build/target/manager-app-1.0-SNAPSHOT.jar app.jar

# jdeps reads bytecode, so it cannot see reflectively loaded classes. The three
# hand-added modules cover that blind spot:
#   jdk.crypto.ec   - TLS elliptic-curve, loaded as a service provider
#   jdk.unsupported - sun.misc.Unsafe, used by Jetty underneath Javalin
#   java.naming     - JNDI, pulled in by logging config
# --ignore-missing-deps is required for a shaded jar, whose dependencies
# reference optional classes that were never packaged.

RUN MODULES=$(jdeps --ignore-missing-deps --print-module-deps --multi-release 17 -q app.jar) && \
    echo "jdeps detected: ${MODULES}" && \
    jlink \
      --add-modules "${MODULES},jdk.crypto.ec,jdk.unsupported,java.naming" \
      --strip-debug --no-man-pages --no-header-files --compress=2 \
      --output /javaruntime

# ---------- Stage 3: runtime ----------
FROM alpine:3.20

# The JVM is C++; bare Alpine ships no C++ standard library.
RUN apk add --no-cache libstdc++

ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"
COPY --from=jre-build /javaruntime ${JAVA_HOME}

WORKDIR /app
COPY --from=build /build/target/manager-app-1.0-SNAPSHOT.jar app.jar

EXPOSE 7001

CMD ["java", "-jar", "app.jar"]
