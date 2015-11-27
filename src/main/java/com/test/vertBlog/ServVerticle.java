package com.test.vertBlog;

import com.test.vertBlog.DTO.Whisky;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.impl.HttpServerImpl;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by pcjoshi on 11/11/15.
 */
public class ServVerticle extends AbstractVerticle {
	private Map<Integer, Whisky> products = new LinkedHashMap<>();

	public void start(Future<Void> future) {
		Router router = Router.router(vertx);
		createSomeData();
		router.route("/").handler(routingContext -> {
			HttpServerResponse response = routingContext.response();
			response.putHeader("content-type", "text/html").end("<h1>Hello from my firsts Vert.x 3 application</h1>");
		});
		router.get("/api/whiskies").handler(this::getAll);
		router.delete("/api/whiskies:id").handler(this::deleteOne);
		router.route("/assets/*").handler(StaticHandler.create("assets"));
		router.route("/api/whiskies*").handler(BodyHandler.create());
		router.post("/api/whiskies").handler(this::addOne);
		vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port", 8080),
				result -> {
					if (result.succeeded()) {
						System.out.println("Completed verticle deployment");
						future.complete();
					} else {
						future.fail(result.cause());
					}
				});
	}

	// Store our product

	// Create some product
	private void createSomeData() {
		Whisky bowmore = new Whisky("Bowmore 15 Years Laimrig", "Scotland, Islay");
		products.put(bowmore.getId(), bowmore);
		Whisky talisker = new Whisky("Talisker 57° North", "Scotland, Island");
		products.put(talisker.getId(), talisker);
	}

	private void addOne(RoutingContext context) {
		Whisky whisky = Json.decodeValue(context.getBodyAsString(), Whisky.class);
		System.out.println("Creating a new whiskey");
		products.put(whisky.getId(), whisky);
		context.response().setStatusCode(201).putHeader("content-type", "application/json")
				.end(Json.encodePrettily(whisky));
	}

	private void getAll(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json")
				.end(Json.encodePrettily(products.values()));
	}

	private void deleteOne(RoutingContext context){
        String id = context.request().getParam("id");
        if(id == null){
        	context.response().setStatusCode(400).end();
        }else{
        	Integer idAsInteger = Integer.valueOf(id);
        	products.remove(idAsInteger);
        }
    }
}
