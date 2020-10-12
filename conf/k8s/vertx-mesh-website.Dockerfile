FROM graalvm-ce-java11

# S
RUN useradd -M watheia --shell /bin/false \
    && usermod -L watheia
ADD --chown=watheia:watheia wa /wa

EXPOSE 8080
EXPOSE 8443

WORKDIR /wa
ENTRYPOINT ["/wa/bin/vertx-mesh-website", "watheia.vertx.mesh.website", "--launcher-class=io.vertx.core.Launcher"]
CMD ["-c"]