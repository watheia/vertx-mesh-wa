/**
 *
 */
package watheia.vertx.mesh.website;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static io.vertx.core.http.impl.HttpUtils.normalizePath;
import static io.vertx.ext.web.handler.TemplateHandler.DEFAULT_TEMPLATE_DIRECTORY;

import io.reactivex.Completable;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.FaviconHandler;
import io.vertx.reactivex.ext.web.handler.LoggerHandler;
import io.vertx.reactivex.ext.web.handler.StaticHandler;
import io.vertx.reactivex.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.reactivex.ext.web.templ.handlebars.HandlebarsTemplateEngine;

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
		final var sslKey = config().getString(CONFIG_SSL_KEY, "");
		final var sslKeystore = config().getString(CONFIG_SSL_KEYSTORE, "conf/test.keystore");
		final var indexTemplate = DEFAULT_TEMPLATE_DIRECTORY + normalizePath("index.hbs");

		final var httpOptions = new HttpServerOptions().setPort(port).setHost(host);
		final var templateEngine = HandlebarsTemplateEngine.create(vertx);
		final var sockJSHandler = SockJSHandler.create(vertx);
		final var router = Router.router(vertx);

		// Enable MessageBus
		////

		sockJSHandler.bridge(new SockJSBridgeOptions(), event -> {
			if (event.type() == BridgeEventType.SOCKET_CREATED) {
				logger.info("Socket Created: " + event);
				// You can also optionally provide a handler like this which will be passed any
				// events that occur on the bridge
				// You can use this for monitoring or logging, or to change the raw messages
				// in-flight.
				// It can also be used for fine grained access control.
			}

			// This signals that it's ok to process the message
			event.complete(true);
		});

		// Setup Routes
		////

		router.route().handler(LoggerHandler.create());
		router.route().handler(BodyHandler.create());

		// Redirect /assets to static handler
		router.get().handler(FaviconHandler.create("/favicon.ico"));
		router.get("/assets/*").handler(StaticHandler.create()
				.setAllowRootFileSystemAccess(true)
				.setAlwaysAsyncFS(true)
				.setCachingEnabled(true)
				.setEnableFSTuning(true)
				.setFilesReadOnly(true));

		// Redirect top-level to default locale
		router.get("/").handler(RedirectHandler.create(locale));

		// Route top level page to the index template
		router.get("/en").handler(ctx -> {
			final var data = new JsonObject().put("locale", locale);
			templateEngine.rxRender(data, indexTemplate).subscribe(res -> {
				ctx.response().putHeader(CONTENT_TYPE, "text/html").end(res);
			}, err -> ctx.fail(err));
		});

		// Start HTTP Server
		////

		// Configure SSL if needed
		if (http2 || !sslKey.isEmpty()) {
			final var jksOptions = new JksOptions().setPath(sslKeystore).setPassword(sslKey);
			httpOptions.setSsl(true).setKeyStoreOptions(jksOptions).setUseAlpn(http2);
		}

		// Start listening for HTTP requests
		return vertx.createHttpServer(httpOptions)
				.requestHandler(router)
				.rxListen(port)
				.ignoreElement();
	}
}
