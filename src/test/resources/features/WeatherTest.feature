# language: ru
Функционал: Позитивное тестирование API текущей погоды

Структура сценария: Проверка текущей погоды для города <city>

Дано заглушка настроенная для города "<city>"
Когда я запрашиваю текущую погоду для города "<city>"
Тогда я сверяю ответ со страной "<expectedCountry>", температурой <expectedTemp>, погодой "<expectedCondition>", влажностью <expectedHumidity> и логирую расхождения

  Примеры:

    | city     | expectedCountry          | expectedTemp | expectedCondition | expectedHumidity |
    | Moscow   | Russia                   | 4,5          | Overcast          | 82               |
    | London   | United Kingdom           | 9,2          | Light rain        | 87               |
    | Tokyo    | Japan                    | 18,0         | Sunny             | 51               |
    | New York | United States of America | 12,5         | Cloudy            | 65               |
