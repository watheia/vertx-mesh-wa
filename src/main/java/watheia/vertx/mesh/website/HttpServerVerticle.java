/**
 *
 */
package watheia.vertx.mesh.website;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

import io.reactivex.Completable;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.JksOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;

/**
 * @author Aaron R Miller<aaron.miller@waweb.io>
 *
 */
public class HttpServerVerticle extends AbstractVerticle {
	static final Logger logger = LoggerFactory.getLogger(HttpServerVerticle.class);

	static final String CONFIG_SERVER_HOST = "server.host";
	static final String CONFIG_SERVER_PORT = "server.port";
	static final String CONFIG_SERVER_LOCALE = "server.locale";
	static final String CONFIG_SERVER_HTTP2 = "server.http2";
	static final String CONFIG_SSL_KEYSTORE = "ssl.keystore";
	static final String CONFIG_SSL_KEY = "ssl.key";

	// Convenience method so you can run it in your IDE
	public static void main(final String[] args) {
		Vertx.vertx(new VertxOptions())
				.deployVerticle(new HttpServerVerticle());
	}

	@Override
	public Completable rxStart() {
		logger.info("Hello, Http Server!");

		// Read Config Data
		////

		final var host = config().getString(CONFIG_SERVER_HOST, "localhost");
		final var port = config().getInteger(CONFIG_SERVER_PORT, 8080);
		final var http2 = config().getBoolean(CONFIG_SERVER_HTTP2, false);
		final var locale = config().getString(CONFIG_SERVER_LOCALE, "en");
		final var serverOptions = new HttpServerOptions().setPort(port).setHost(host);
		final var sslKey = config().getString(CONFIG_SSL_KEY, "");
		final var sslKeystore = config().getString(CONFIG_SSL_KEYSTORE, "conf/test.keystore");

		if (http2 || !sslKey.isEmpty()) {
			final var jksOptions = new JksOptions().setPath(sslKeystore).setPassword(sslKey);
			serverOptions.setSsl(true).setKeyStoreOptions(jksOptions).setUseAlpn(http2);
		}

		// Setup HTTP Routes
		////

		final var router = Router.router(vertx);

		// Redirect to default locale if none specified
		router.get("/").handler(RedirectHandler.create("/" + locale));

		// Simple greeting for health checks
		router.get("/:locale/greeting").handler(ctx -> {
			ctx.response()
					.putHeader(CONTENT_TYPE, "text/plain")
					.end("Hello, Vert.x Website!");
		});

		return vertx.createHttpServer(serverOptions)
				.requestHandler(router)
				.rxListen(port)
				.ignoreElement();
	}
}
