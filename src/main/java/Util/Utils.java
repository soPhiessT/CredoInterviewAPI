package Util;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


public class Utils {
    private static WireMockServer wireMockServer;

    public static void startWireMockServer() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor("localhost", 8080);
    }

    public static void stopWireMockServer() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    public static void configureStubs() {
        stubFor(get(urlPathEqualTo("/users"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                  { "id": 1, "name": "Alice", "age": 30, "gender": "female" },
                                  { "id": 2, "name": "Bob", "age": 25, "gender": "male" },
                                  { "id": 3, "name": "Manuchari", "age": 50, "gender": "male" },
                                  { "id": 4, "name": "Lela", "age": 45, "gender": "female" },
                                  { "id": 5, "name": "Rostomi", "age": 77, "gender": "male" },
                                  { "id": 6, "name": "Sopio", "age": 31, "gender": "female" },
                                  { "id": 7, "name": "Gogi", "age": 77, "gender": "male" }
                                ]
                                """)));

        stubFor(get(urlPathEqualTo("/users"))
                .withQueryParam("age", equalTo("30"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                  { "id": 1, "name": "Alice", "age": 30, "gender": "female" }
                                ]
                                """)));

        stubFor(get(urlPathEqualTo("/users"))
                .withQueryParam("age", equalTo("77"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                  { "id": 5, "name": "Rostomi", "age": 77, "gender": "male" },
                                  { "id": 7, "name": "Gogi", "age": 77, "gender": "male" }
                                ]
                                """)));

        stubFor(get(urlPathEqualTo("/users"))
                .withQueryParam("age", equalTo("18"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));

        stubFor(get(urlPathEqualTo("/users"))
                .withQueryParam("gender", equalTo("male"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                  { "id": 2, "name": "Bob", "age": 25, "gender": "male" },
                                  { "id": 3, "name": "Manuchari", "age": 50, "gender": "male" },
                                  { "id": 5, "name": "Rostomi", "age": 77, "gender": "male" },
                                  { "id": 7, "name": "Gogi", "age": 77, "gender": "male" }
                                ]
                                """)));

        stubFor(get(urlPathEqualTo("/users"))
                .withQueryParam("gender", equalTo("female"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                  { "id": 1, "name": "Alice", "age": 30, "gender": "female" },
                                  { "id": 4, "name": "Lela", "age": 45, "gender": "female" },
                                  { "id": 6, "name": "Sopio", "age": 31, "gender": "female" }                                ]
                                """)));

        stubFor(get(urlPathEqualTo("/users"))
                .withQueryParam("age", equalTo("-1"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                { "error": "Invalid age parameter" }
                                """)));

        stubFor(get(urlPathEqualTo("/users"))
                .withHeader("return-error", equalTo("true"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                { "error": "Internal Server Error" }
                                """)));

        stubFor(get(urlPathEqualTo("/users"))
                .withQueryParam("gender", equalTo("unknown"))
                .willReturn(aResponse()
                        .withStatus(422)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));
    }
}
