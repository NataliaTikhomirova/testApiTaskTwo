# language: ru
Функционал: Негативное тестирование API сервиса погоды (API Errors)

  Структура сценария: Проверка ошибки API если <description>
    Дано заглушка настроена на возврат ошибки "<description>" с HTTP статусом <httpStatus> и сообщением "<expectedErrorMessage>" для ключа "<apiKey>" и города "<city>"
    Когда я отправляю запрос к API погоды с ключом "<apiKey>" и городом "<city>"
    Тогда статус ответа должен быть <httpStatus>, а ошибка в теле ответа должна содержать сообщение "<expectedErrorMessage>"

    Примеры:
      | description       | apiKey       | city           | httpStatus | expectedErrorMessage                     |
      | Ключ не передан   |              | Moscow         | 401        | API key not provided.                    |
      | Город не передан  | valid_key    |                | 400        | Parameter 'q' not provided.              |
      | Город не найден   | valid_key    | UnknownCity999 | 400        | No location found matching parameter 'q' |
      | Ключ заблокирован | disabled_key | London         | 403        | API key has been disabled.               |