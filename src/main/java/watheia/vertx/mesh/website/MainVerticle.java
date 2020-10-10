package watheia.vertx.mesh.website;

import io.reactivex.Completable;
import io.vertx.core.VertxOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;

/**
 * @author Aaron R Miller<aaron.miller@waweb.io>
 */
public class MainVerticle extends AbstractVerticle {

	static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

	// Convenience method so you can run it in your IDE
	static void main(final String[] args) {
		final var options = new VertxOptions();
		final var vertx = Vertx.vertx(options);
		vertx.rxDeployVerticle(new MainVerticle());
	}

	@Override
	public Completable rxStart() {
		logger.info("Hello, Verticle!");

		return Completable.complete();
	}
}
