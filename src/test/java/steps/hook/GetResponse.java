package steps.hook;

import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class GetResponse {

    public static StubMapping getStubByParams(String url, String apiKey, String city, int httpStatus, String readyJson) {
        return Hooks.wireMockServer.stubFor(get(urlPathEqualTo(url))
                .withQueryParam("key", equalTo(apiKey))
                .withQueryParam("q", equalTo(city))
                .willReturn(aResponse()
                        .withStatus(httpStatus)
                        .withHeader("Content-Type", "application/json")
                        .withBody(readyJson)));
    }

    public static Response getRestAssuredByParams(String apiKey, String city, String url) {
        return RestAssured.given()
                .queryParam("key", apiKey)
                .queryParam("q", city)
                .when()
                .get(url);
    }

}
