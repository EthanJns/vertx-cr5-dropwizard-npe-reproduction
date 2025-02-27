package com.example.starter;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.VerticleBase;
import io.vertx.core.VertxOptions;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.dropwizard.MetricsService;

public class MainVerticle extends AbstractVerticle {

  MetricsService dropWizardMetricsService;
  WebClient webclient;

  public static void main(String[] args) {
    VertxOptions vertxOptions = new VertxOptions();
    DropwizardMetricsOptions dropwizardMetricsOptions = new DropwizardMetricsOptions().setEnabled(true);
    vertxOptions.setMetricsOptions(dropwizardMetricsOptions);

    Vertx vertx = Vertx.vertx(vertxOptions);
    vertx.deployVerticle(new MainVerticle());
  }

  @Override
  public void start() {
    boolean metricsEnabled = vertx.isMetricsEnabled();
    if (metricsEnabled) {
      dropWizardMetricsService = MetricsService.create(vertx);
    }

    WebClientOptions webOptions = new WebClientOptions();
    webOptions.setSsl(true);
    webOptions.setTrustAll(true);
    webOptions.setVerifyHost(true);
    webOptions.setKeepAlive(true);

    this.webclient = WebClient.create(vertx, webOptions);

    this.startHttpServer().onComplete(u -> makeHttpRequest());
  }

  public Future<?> startHttpServer() {
    return vertx.createHttpServer().requestHandler(req -> {
      req.response()
          .putHeader("content-type", "text/plain")
          .end("Hello from Vert.x!");
    }).listen(8080).onSuccess(http -> {
      System.out.println("HttpServer Now Listening on port 8080");
    });
  }

  public Future<?> makeHttpRequest() {
    HttpRequest<Buffer> request = this.webclient.getAbs("https://localhost:8443/");
    request.putHeader(HttpHeaders.CONTENT_LENGTH.toString(), Integer.toString(0));
    request.putHeader(HttpHeaders.CONTENT_TYPE.toString(), "application/json");
    return request.send().onComplete(ar -> {
      if (ar.succeeded()) {
        System.out.println("This should fail as nothing is running on that port");
      } else {
        System.out.println("In my testing/example, this is what we should see when the NPE isnt thrown");
      }
    });
  }
}
