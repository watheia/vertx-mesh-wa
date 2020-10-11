package watheia.vertx.mesh.website;

import java.nio.file.Path;
import java.nio.file.Paths;

import io.reactivex.Completable;
import io.reactivex.Single;
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

	static final String SERVICES_VERTICLE = "watheia.vertx.mesh.website.ServicesVerticle";

	static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

	static final Path http1ConfigPath = Paths.get("conf", "wa.http1.properties");

	static final Path http2ConfigPath = Paths.get("conf", "wa.http2.properties");

	static final Path serviceConfigPath = Paths.get("conf", "wa.services.json");

	// Convenience method so you can run it in your IDE
	public static void main(final String[] args) {
		Vertx.vertx(new VertxOptions()).deployVerticle(new MainVerticle());
	}

	@Override
	public Completable rxStart() {
		logger.info("Hello, Main Verticle!");
		return Completable.mergeArray(startServices(serviceConfigPath.toString()),
				startHttpServer(http1ConfigPath.toString()), startHttpServer(http2ConfigPath.toString()));
	}

	private Completable startServices(final String propsFile) {
		return loadJson(propsFile).flatMapCompletable(config -> {
			final var options = new DeploymentOptions().setConfig(config);
			return vertx.rxDeployVerticle(SERVICES_VERTICLE, options).ignoreElement();
		});
	}

	private Completable startHttpServer(final String propsFile) {
		return loadProperties(propsFile).flatMapCompletable(config -> {
			final var options = new DeploymentOptions().setConfig(config);
			return vertx.rxDeployVerticle(HTTP_SERVER_VERTICLE, options).ignoreElement();
		});
	}

	private Single<JsonObject> loadProperties(final String path) {
		final var fileStore = new ConfigStoreOptions().setType("file").setFormat("properties")
				.setConfig(new JsonObject().put("path", path));
		return ConfigRetriever.create(vertx, new ConfigRetrieverOptions().addStore(fileStore)).rxGetConfig();
	}

	private Single<JsonObject> loadJson(final String path) {
		final var fileStore = new ConfigStoreOptions().setType("file").setConfig(new JsonObject().put("path", path));
		return ConfigRetriever.create(vertx, new ConfigRetrieverOptions().addStore(fileStore)).rxGetConfig();
	}
}
