package com.test.vertBlog;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.impl.HttpServerImpl;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * Created by pcjoshi on 11/11/15.
 */
public class ServVerticle extends AbstractVerticle {

    public void start(Future<Void> future ){
        Router router = Router.router(vertx);

        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type","text/html")
                    .end("<h1>Hello from my firsts Vert.x 3 application</h1>");
        });

        router.route("/assets/*").handler(StaticHandler.create("assets"));

        vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port",8080),result -> {
            if (result.succeeded()){
                System.out.println("Completed verticle deployment");
                future.complete();
            }else {
                future.fail(result.cause());
            }
        });
    }
}
