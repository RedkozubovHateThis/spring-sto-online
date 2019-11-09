package io.swagger.service.impl;

import io.swagger.response.api.SMSAPIResponse;
import io.swagger.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    private final String SEND_API_URL = "https://smsc.ru/sys/send.php";

    @Value("${sms.login}")
    private String login;
    @Value("${sms.password}")
    private String password;
    @Value("${sms.debug}")
    private Boolean useDebug;
    @Value("${sms.senderName}")
    private String senderName;

    private RestTemplate restTemplate;

    {
        restTemplate = new RestTemplate();

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

        converter.setSupportedMediaTypes( Collections.singletonList(MediaType.ALL) );
        messageConverters.add(converter);

        restTemplate.setMessageConverters(messageConverters);
    }

    @Async
    @Override
    public void sendSmsAsync(String phone, String message) {
        UriComponentsBuilder query = buildParams(phone, message, 0, "utf-8"); //По умолчанию, без транслита, и в кодировке utf-8
        sendRequest(query);
    }
    @Override
    public SMSAPIResponse sendSms(String phone, String message) {
        UriComponentsBuilder query = buildParams(phone, message, 0, "utf-8"); //По умолчанию, без транслита, и в кодировке utf-8
        return sendRequest(query);
    }

    /**
     * Метод, собирающий и отправляющий запрос
     * @param phone     Номер, или несколько, разделенных запятыми, или точками с запятой, номеров
     * @param message   Сообщение
     * @param translit  Использовать транслит (0 - не использовать, 1 - translit, 2 - mpaHc/Ium)
     * @param charset   Кодировка
     */
    @Async
    @Override
    public void sendSmsAsync(String phone, String message, Integer translit, String charset) {
        UriComponentsBuilder params = buildParams(phone, message, translit, charset);
        sendRequest(params);
    }
    @Override
    public SMSAPIResponse sendSms(String phone, String message, Integer translit, String charset) {
        UriComponentsBuilder params = buildParams(phone, message, translit, charset);
        return sendRequest(params);
    }

    /**
     * Метод для отправки запроса
     * Составляет header сообщения, отправляет запрос и получает ответ
     * Ответ представляет в удобно читаемом виде
     *
     * @param params    Параметры запроса (массив с данными)
     */
    private SMSAPIResponse sendRequest(UriComponentsBuilder params) {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setAccept( Collections.singletonList(MediaType.APPLICATION_JSON_UTF8) );

        HttpEntity<?> request = new HttpEntity<>(headers);

        try {
            logger.info(">");
            logger.info("Sending request to [ {} ]\n", SEND_API_URL);

            String query = params.build().toString();

            if ( useDebug )
                logger.info("Request: {}", query);

            ResponseEntity<SMSAPIResponse> response = restTemplate.exchange( query, HttpMethod.GET, request, SMSAPIResponse.class);

            logger.info("Response [ STATUS ] : {}", response.getStatusCode().toString() );

            if ( response.getBody() != null ) {

                SMSAPIResponse SMSAPIResponse = response.getBody();

                if ( SMSAPIResponse.getError() == null && SMSAPIResponse.getErrorCode() == null ) {
                    logger.info("API response [ ID ] : {}", SMSAPIResponse.getId() );

                    if ( useDebug ) {
                        logger.info("API response [ COUNT ] : {}", SMSAPIResponse.getCnt() );
                        logger.info("API response [ COST ] : {}", SMSAPIResponse.getCost() );
                        logger.info("API response [ BALANCE ] : {}", SMSAPIResponse.getBalance() );
                    }
                }
                else {
                    if ( SMSAPIResponse.getId() != null ) {
                        logger.error("API response [ ID ] : {}", SMSAPIResponse.getId() );
                    }
                    logger.error("API response [ ERROR ] : {}", SMSAPIResponse.getError() );

                    if ( useDebug ) {
                        logger.error("API response [ ERROR_CODE ] : {}", SMSAPIResponse.getErrorCode() );
                    }
                }

                return SMSAPIResponse;

            }

            return null;
        }
        catch(Exception e) {
            logger.error( "Exchange with API [ FAILED ] Reason: " + e.getMessage() );
            return null;
        }

    }

    /**
     * Метод для построения запросов
     * Собирает и возвращает запрос с параметрами
     *
     * @param phone     Номер, или несколько, разделенных запятыми или точками с запятой, номеров
     * @param message   Сообщение
     * @param translit  Использовать транслит (0 - не использовать, 1 - translit, 2 - mpaHc/Ium)
     * @param charset   Кодировка
     * @return          Готовый для отправки запрос с адресом и набором параметров
     */
    private UriComponentsBuilder buildParams(String phone, String message, Integer translit, String charset) {
        UriComponentsBuilder params = UriComponentsBuilder.fromHttpUrl(SEND_API_URL);

        params.queryParam( "login", login );
        params.queryParam( "psw", password );
        params.queryParam( "sender", senderName );
        params.queryParam( "phones", phone );
        params.queryParam( "cost", 3 ); //Всегда ждем полный ответ
        params.queryParam( "translit", translit );
        params.queryParam( "charset", charset );
        params.queryParam( "fmt", 3 ); //Для возврата ответа в формате json
        params.queryParam( "mes", message );

        return params;
    }
}
