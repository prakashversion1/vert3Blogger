package com.test.vertBlog;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.ServerSocket;

/**
 * Created by pcjoshi on 11/11/15.
 */
@RunWith(VertxUnitRunner.class)
public class ServVerticleTest {
    private Vertx vertx;
    private int port;
    @Before
    public void setUp(TestContext testContext){
        try{
            ServerSocket socket = new ServerSocket(0);
            port = socket.getLocalPort();
            socket.close();
        }catch (Exception ex){
            ex.printStackTrace();
            port = 8081;
        }
        vertx = Vertx.vertx();
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", port)
                );
        vertx.deployVerticle(ServVerticle.class.getName(),options,testContext.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context){
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testMyApplication(TestContext context){
        final Async async = context.async();
        vertx.createHttpClient().getNow(port,"localhost","/",response -> {
            response.handler(body ->{
                context.assertTrue(body.toString().contains("Hello"));
                async.complete();
            });
        });
    }
}
