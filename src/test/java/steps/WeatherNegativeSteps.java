package steps;

import io.cucumber.java.ru.Дано;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import io.restassured.response.Response;
import steps.hook.GetResponse;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static api.utils.Finals.url;

public class WeatherNegativeSteps {

    private static final Logger LOG = Logger.getLogger(WeatherNegativeSteps.class.getName());
    private Response response;

    @Дано("заглушка настроена на возврат ошибки {string} с HTTP статусом {int} и сообщением {string} для ключа {string} и города {string}")
    public void setupErrorStub(String caseDescription, int httpStatus, String errorMessage, String apiKey, String city) {
        // Формируем JSON ошибки в соответствии со спецификацией WeatherAPI
        String errorJson = String.format(
                "{\"error\": {\"code\": %d, \"message\": \"%s\"}}", httpStatus, errorMessage,
                errorMessage.replace("\"", "\\\"")
        );

        GetResponse.getStubByParams(url, apiKey, city, httpStatus, errorJson);
    }

    @Когда("я отправляю запрос к API погоды с ключом {string} и городом {string}")
    public void sendRequestWithParams(String apiKey, String city) {
        response = GetResponse.getRestAssuredByParams(apiKey, city, url);
    }

    @Тогда("статус ответа должен быть {int}, а ошибка в теле ответа должна содержать сообщение {string}")
    public void verifyErrorResponse(int expectedHttpStatus, String expectedErrorMessage) {
        int actualHttpStatus = response.getStatusCode();
        String actualErrorMessage = response.jsonPath().getString("error.message");

        LOG.info(String.format(
                "Проверка ошибки -> HTTP: %d (ожидался %d) | Message: '%s'",
                actualHttpStatus, expectedHttpStatus, actualErrorMessage
        ));

        assertEquals(expectedHttpStatus, actualHttpStatus, "Неверный HTTP-статус ответа");
        assertEquals(expectedErrorMessage, actualErrorMessage, "Неверное текст-сообщение об ошибке (error.message)");
    }
}
