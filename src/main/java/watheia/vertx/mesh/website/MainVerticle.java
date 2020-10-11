package watheia.vertx.mesh.website;

import java.nio.file.Path;
import java.nio.file.Paths;

import io.reactivex.Completable;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;

/**
 * @author Aaron R Miller<aaron.miller@waweb.io>
 */
public class MainVerticle extends AbstractVerticle {

	static final String HTTP_SERVER_VERTICLE = "watheia.vertx.mesh.website.HttpServer";

	static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

	static final Path http1Props = Paths.get("conf", "wa.http1.properties");

	static final Path http2Props = Paths.get("conf", "wa.http2.properties");

	// Convenience method so you can run it in your IDE
	public static void main(final String[] args) {
		Vertx.vertx(new VertxOptions()).deployVerticle(new MainVerticle());
	}

	@Override
	public Completable rxStart() {
		logger.info("Hello, Main Verticle!");
		return Completable.mergeArray(startHttpServer(http1Props.toString()), startHttpServer(http2Props.toString()));
	}

	private Completable startHttpServer(final String propsFile) {
		return configRetriever(propsFile).rxGetConfig().flatMapCompletable(config -> {
			final var options = new DeploymentOptions().setConfig(config);
			return vertx.rxDeployVerticle(HTTP_SERVER_VERTICLE, options).ignoreElement();
		});
	}

	private ConfigRetriever configRetriever(final String path) {
		final var fileStore = new ConfigStoreOptions().setType("file").setFormat("properties")
				.setConfig(new JsonObject().put("path", path));
		return ConfigRetriever.create(vertx, new ConfigRetrieverOptions().addStore(fileStore));
	}
}
