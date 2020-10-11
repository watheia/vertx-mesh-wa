package watheia.vertx.mesh.website;

import io.reactivex.Completable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;

public class DefaultContactService extends AbstractVerticle {
    static final Logger logger = LoggerFactory.getLogger(DefaultContactService.class);

    // Convenience method so you can run it in your IDE
    public static void main(final String[] args) {
        final var options = new DeploymentOptions().setConfig(new JsonObject());
        Vertx.vertx(new VertxOptions()).deployVerticle(new DefaultContactService(), options);
    }

    @Override
    public Completable rxStart() {
        final var address = config().getString("address", "wa.service.contact");
        logger.info(String.format("Hello, Contact Service (%s)!", address));

        return Completable.complete();
    }
}
