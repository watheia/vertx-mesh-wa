FROM oraclelinux:7-slim

# Note: If you are behind a web proxy, set the build variables for the build:
#       E.g.:  docker build --build-arg "https_proxy=..." --build-arg "http_proxy=..." --build-arg "no_proxy=..." ...

RUN yum update -y oraclelinux-release-el7 \
    && yum install -y oraclelinux-developer-release-el7 oracle-softwarecollection-release-el7 \
    && yum-config-manager --enable ol7_developer \
    && yum-config-manager --enable ol7_developer_EPEL \
    && yum-config-manager --enable ol7_optional_latest \
    && yum install -y bzip2-devel ed gcc gcc-c++ gcc-gfortran gzip file fontconfig less libcurl-devel make openssl openssl-devel readline-devel tar vi which xz-devel zlib-devel \
    && yum install -y glibc-static libcxx libcxx-devel libstdc++-static zlib-static certbot \
    && rm -rf /var/cache/yum

RUN fc-cache -f -v

ARG GRAALVM_VERSION=20.2.0
ARG JAVA_VERSION=java11
ARG GRAALVM_PKG=https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-$GRAALVM_VERSION/graalvm-ce-$JAVA_VERSION-GRAALVM_ARCH-$GRAALVM_VERSION.tar.gz
ARG TARGETPLATFORM=linux/amd64

ENV LANG=en_US.UTF-8 \
    JAVA_HOME=/opt/graalvm-ce-$JAVA_VERSION-$GRAALVM_VERSION/

ADD gu-wrapper.sh /usr/local/bin/gu
RUN set -eux \
    && if [ "$TARGETPLATFORM" == "linux/amd64" ]; then GRAALVM_PKG=${GRAALVM_PKG/GRAALVM_ARCH/linux-amd64}; fi \
    && if [ "$TARGETPLATFORM" == "linux/arm64" ]; then GRAALVM_PKG=${GRAALVM_PKG/GRAALVM_ARCH/linux-aarch64}; fi \
    && curl --fail --silent --location --retry 3 ${GRAALVM_PKG} \
    | gunzip | tar x -C /opt/ \
    # Set alternative links
    && mkdir -p "/usr/java" \
    && ln -sfT "$JAVA_HOME" /usr/java/default \
    && ln -sfT "$JAVA_HOME" /usr/java/latest \
    && for bin in "$JAVA_HOME/bin/"*; do \
    base="$(basename "$bin")"; \
    [ ! -e "/usr/bin/$base" ]; \
    alternatives --install "/usr/bin/$base" "$base" "$bin" 20000; \
    done \
    && chmod +x /usr/local/bin/gu \
    && gu install native-image llvm-toolchain wasm


RUN useradd -M watheia --shell /bin/false \
    && usermod -L watheia

ADD --chown=watheia:watheia test.keystore /wa/conf/test.keystore
ADD --chown=watheia:watheia production.keystore /wa/conf/production.keystore
ADD --chown=watheia:watheia wa.http1.properties /wa/conf/wa.http1.properties
ADD --chown=watheia:watheia wa.http2.properties /wa/conf/wa.http2.properties
ADD --chown=watheia:watheia wa.services.json /wa/conf/wa.services.json
ADD --chown=watheia:watheia vertx-mesh-website-shadow-0.1.0/bin /wa/bin
ADD --chown=watheia:watheia vertx-mesh-website-shadow-0.1.0/lib /wa/lib

EXPOSE 8080
EXPOSE 8443

RUN java --version

WORKDIR /wa
CMD ["/wa/bin/vertx-mesh-website", "watheia.vertx.mesh.website", "--launcher-class=io.vertx.core.Launcher"]
