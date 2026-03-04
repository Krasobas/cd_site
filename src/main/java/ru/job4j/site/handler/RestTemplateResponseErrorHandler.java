package ru.job4j.site.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import ru.job4j.site.exception.*;

import java.io.IOException;

@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
            || httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus status = (HttpStatus) response.getStatusCode();

        String body = new String(response.getBody().readAllBytes());

        switch (status.series()) {
            case CLIENT_ERROR -> {
                if (status == HttpStatus.UNAUTHORIZED) {
                    throw new UnauthorizedException("Доступ отклонен: " + body);
                }
                if (status == HttpStatus.NOT_FOUND) {
                    throw new IdNotFoundException("Ресурс не найден: " + body);
                }
                if (status == HttpStatus.BAD_REQUEST) {
                    throw new ValidationException("Ошибка валидации: " + body);
                }
                throw new UnknownException("Ошибка клиента: " + status + " " + body);
            }
            case SERVER_ERROR -> {
                throw new UnknownException("Удалённый сервис вернул ошибку: " + status);
            }
        }
    }
}
