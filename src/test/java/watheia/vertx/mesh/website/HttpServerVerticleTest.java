/**
 *
 */
package watheia.vertx.mesh.website;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.codec.BodyCodec;

/**
 * @author Aaron R Miller<aaron.miller@waweb.io>
 *
 */
@ExtendWith(VertxExtension.class)
public class HttpServerVerticleTest {

	@Test
	void default_greeting(final Vertx vertx, final VertxTestContext ctx) {
		vertx.deployVerticle(new HttpServer(), ar -> {
			if (ar.succeeded()) {
				WebClient.create(vertx).get("http://localhost:8080/en/greeting")
						.as(BodyCodec.string())
						.send(ctx.succeeding(response -> ctx.verify(() -> {
							assertThat(response.body(), equalTo("Hello, Vert.x Website!"));
							ctx.completeNow();
						})));
			}
		});
	}
}
