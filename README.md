# vertx-mesh-website (*WIP*)

Simple Website with Vert.x and Gentics Mesh on GraalVM


## Project Goals

This project was originally indented to get a simple website quickly up and running for a startup. We then saw it was not too much additional effort to make the deployment generic and configurable, so that it may be used as a starting point for other such projects.

There are many website bundles to choose from, and there is nothing particularly special about this one unless you happen to have a specific interest in getting a system up and running with Vert.x and 
Gentics Mesh Headless CMS. This project may be used as an example to set up your systems in such a case.

## Getting Started

First, update the docker image name in `build.gradle` to point to your own Docker registry,
then run `gradlew build deploy` to build the image and push it out to your
own registry.

## Roadmap

### v0.1 (*complete*)

- Basic hello-world for vertx-web
    - Starts an http2 and http1 server on different ports, with and without SSL configured respectively
    - Enables basic functionality such as logging, eventbus, static handler, etc.
    - Loads dependent "service verticles" on the fly from a configuration file
    - Provides an example service that accepts `FormData` sent to the Event Bus

- DevOps
    - Single Gradle entry point for all (most?) tasks
    - Builds a container with GraalVM `native-image`, `llvm-toolchain`, and `wasm` support
    - Provides Kubernetes configurations for dependent services

### `v0.2` (*in progress*)

- Mesh Integration

### `v1.0` (*TBD*)
