package watheia.vertx.mesh.website;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;

/**
 * @author Aaron R Miller<aaron.miller@waweb.io>
 */
@ExtendWith(VertxExtension.class)
class MainVerticleTest {

	@Test
	void hello_verticle(final Vertx vertx, final VertxTestContext ctx) {
		vertx.rxDeployVerticle(new MainVerticle())
				.subscribe(id -> ctx.completeNow(), err -> ctx.failNow(err));
	}
}
