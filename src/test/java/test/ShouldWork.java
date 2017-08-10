package test;


import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class ShouldWork {

    protected Vertx vertx;
    protected final int PORT = 9988;

    @Before
    public void setupFakeServer(TestContext ctx) {
        vertx = Vertx.vertx();
        vertx
                .createHttpServer()
                .requestHandler(req -> req.response().end("unexpected"))
                .listen(PORT, ctx.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext ctx) {
        vertx.close(ctx.asyncAssertSuccess());
    }

    @Test
    public void httpBodyHandler(TestContext ctx) {
        Async async = ctx.async();
        client().getNow("/", resp -> {
            ctx.assertEquals(200, resp.statusCode());
            resp.bodyHandler(buff -> {
                buff.toJsonObject(); // will obviously fail
                async.complete();
            });
        });
    }

    @Test
    public void simplyThrow(TestContext ctx) throws Exception {
        Async async = ctx.async();
        throw new RuntimeException("ouch");
        //async.complete();
    }

    private HttpClient client() {
        final HttpClientOptions opts = new HttpClientOptions().setDefaultPort(PORT);
        return vertx.createHttpClient(opts);
    }

}
