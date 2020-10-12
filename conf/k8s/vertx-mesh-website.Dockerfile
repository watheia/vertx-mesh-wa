FROM graalvm-ce-java11

ADD wa /wa
RUN useradd -M watheia --shell /bin/false \
    && usermod -L watheia \
    && chown -R watheia.watheia /wa

CMD ["/wa/bin/vertx-mesh-website", "watheia.vertx.mesh.website", "--launcher-class=io.vertx.core.Launcher"]