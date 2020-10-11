package watheia.vertx.mesh.website;

import static io.vertx.core.http.HttpHeaders.LOCATION;

import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;;

/**
 * @author Aaron R Miller<aaron.miller@waweb.io>
 *
 */
public class RedirectHandler implements Handler<RoutingContext> {
	public static Handler<RoutingContext> create(final String toPath) {
		return new RedirectHandler(toPath);
	}

	private final String toPath;

	private RedirectHandler(final String toPath) {
		this.toPath = toPath;
	}

	@Override
	public void handle(final RoutingContext context) {
		context.response()
				.setStatusCode(302)
				.putHeader(LOCATION, toPath)
				.end();
	}
}
