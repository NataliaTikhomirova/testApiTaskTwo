package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import lombok.SneakyThrows;
import api.model.WeatherResponse;
import org.junit.jupiter.api.Assertions;
import steps.hook.GetResponse;
import api.utils.Utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static api.utils.Finals.url;
import static api.utils.Finals.weatherTemplateFile;

public class WeatherSteps {

    private static final Logger LOG = Logger.getLogger(WeatherSteps.class.getName());
    private static final String API_KEY = "12b0f9823bc24b99ae1212755261311";

    // Имитация базы данных "реального" сервиса для заглушки
    private static final Map<String, MockWeatherData> MOCK_SERVER_DATA = Map.of(
            "Moscow", new MockWeatherData("Russia", 4.5, "Overcast", 82),
            "London", new MockWeatherData("United Kingdom", 9.2, "Light rain", 87),
            // Для Токио и Нью-Йорка в заглушке зашиты отличные от ожидаемых данные, чтобы сработал лог расхождений
            "Tokyo", new MockWeatherData("Japan", 16.4, "Sunny", 60),
            "New York", new MockWeatherData("United States of America", 12.5, "Partly cloudy", 65)
    );

    private WeatherResponse actualResponse;
    private String currentCity;

    @SneakyThrows
    @Given("заглушка настроенная для города {string}")
    public void setupStubForCity(String city) {
        this.currentCity = city;
        MockWeatherData data = MOCK_SERVER_DATA.get(city);
        // Читаем шаблон из файла
        String template = Files.readString(Path.of(weatherTemplateFile));

        // Подставляем реальные значения вместо всех ${...}
        String readyJson = template
                .replace("${city}", city)
                .replace("${country}", data.country())
                .replace("${tempC}", String.valueOf(data.tempC()))
                .replace("${humidity}", String.valueOf(data.humidity()))
                .replace("${condition}", data.condition());

        GetResponse.getStubByParams(url, API_KEY, city, 200, readyJson);
    }

    @When("я запрашиваю текущую погоду для города {string}")
    public void requestWeatherForCity(String city) {
        actualResponse = GetResponse.getRestAssuredByParams(API_KEY, city, url)
                .then()
                .statusCode(200)
                .extract()
                .as(WeatherResponse.class);
    }

    @Then("я сверяю ответ со страной {string}, температурой {double}, погодой {string}, влажностью {int} и логирую расхождения")
    public void verifyAndLogDiscrepancies(String expectedCountry, double expectedTemp, String expectedCondition, int expectedHumidity) {
        List<String> discrepancies = new ArrayList<>();

        Utils.checkField(discrepancies, "location.name", currentCity, actualResponse.getLocation().getName());
        Utils.checkField(discrepancies, "location.country", expectedCountry, actualResponse.getLocation().getCountry());
        Utils.checkField(discrepancies, "current.temp_c", expectedTemp, actualResponse.getCurrent().getTempC());
        Utils.checkField(discrepancies, "current.condition.text", expectedCondition, actualResponse.getCurrent().getCondition().getText());
        Utils.checkField(discrepancies, "current.humidity", expectedHumidity, actualResponse.getCurrent().getHumidity());

        if (discrepancies.isEmpty()) {
            String okMessage = String.format("[OK] Город '%s': все поля полностью совпали с ожидаемыми.", currentCity);
            LOG.info(okMessage);
            Allure.step(okMessage, Status.PASSED);
        } else {
            String errorHeader = String.format("По городу '%s' найдено расхождений: %d", currentCity, discrepancies.size());
            LOG.warning(errorHeader);

            Allure.step(errorHeader, Status.FAILED);
            discrepancies.forEach(item -> {
                LOG.warning(item);
                Allure.step(item, Status.FAILED);
            });

            Assertions.fail(errorHeader + "\n" + String.join("\n", discrepancies));
        }


    }

    private record MockWeatherData(String country, double tempC, String condition, int humidity) {}

}