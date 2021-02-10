package io.kentec.SaccoMobile.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CbsServiceImpl implements CbsService{

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Environment environment;

    private static final Logger LOGGER = LoggerFactory.getLogger(CbsServiceImpl.class);

    @Override
    public String makeCbsCall(String url, String requestBody) throws Exception {
        try {
            String response = "";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(requestBody, headers);
            LOGGER.info("POST {} payload: {}", url, request);
            response = restTemplate.postForObject(url, request, String.class);
            LOGGER.info("POST | response: {}",response);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("POST | response: {}",e.getMessage());
            throw new Exception("Error posting withdrawal request to cbs " + e.getMessage());
        }
    }

}
