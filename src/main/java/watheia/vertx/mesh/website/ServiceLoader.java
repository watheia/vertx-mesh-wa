package watheia.vertx.mesh.website;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;

public class ServiceLoader extends AbstractVerticle {

    static final Logger logger = LoggerFactory.getLogger(ServiceLoader.class);

    // Convenience method so you can run it in your IDE
    public static void main(final String[] args) {
        final var options = new DeploymentOptions().setConfig(new JsonObject());
        Vertx.vertx(new VertxOptions()).deployVerticle(new ServiceLoader(), options);
    }

    @Override
    public Completable rxStart() {
        logger.info("Hello, Services Verticle!");
        return Observable.fromIterable(config().fieldNames()).flatMapCompletable(verticle -> {
            final var config = config().getJsonObject(verticle, new JsonObject());
            return vertx.rxDeployVerticle(verticle, new DeploymentOptions().setConfig(config)).ignoreElement();
        });
    }
}
