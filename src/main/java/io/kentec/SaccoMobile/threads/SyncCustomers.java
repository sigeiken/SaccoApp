package io.kentec.SaccoMobile.threads;

import io.kentec.SaccoMobile.entity.Customers;
import io.kentec.SaccoMobile.exceptions.NonRollBackException;
import io.kentec.SaccoMobile.model.CustomerResponseModel;
import io.kentec.SaccoMobile.repository.CustomerRepository;
import io.kentec.SaccoMobile.shared.SharedFunctions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SyncCustomers {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private Environment environment;

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncCustomers.class);

//    @Scheduled(fixedDelay = 3000)
    public void checkSchedule() throws NonRollBackException {
        try {

            Customers newCustomer = new Customers();
            String response = "";
            response = restTemplate.getForEntity(environment.getProperty("cbs.url") + "all-customers", String.class).getBody();
            JSONObject jsonObject = new JSONObject(response);
            String responseCode = jsonObject.getString("responseCode");
            if (responseCode.equals("00")){
                JSONArray customers = jsonObject.getJSONArray("response");
                for (int i = 0; i<customers.length(); i++){
                    JSONObject item = customers.getJSONObject(i);
                    Customers customerExist = customerRepository.findByIdNo(item.getInt("idNo"));
                    if (customerExist ==null){
                        int randomPIN = (int) ((Math.random()*9000) + 1000);
                        String pin = ""+randomPIN;
                        LOGGER.info("Pin is " + pin);

                        newCustomer.setFirstName(item.getString("firstName"));
                        newCustomer.setLastName(item.getString("lastName"));
                        newCustomer.setIdNo(item.getInt("idNo"));
                        newCustomer.setMsisdn(item.getString("msisdn"));
                        newCustomer.setPin(SharedFunctions.encryptPassword(pin));
                        newCustomer.setStatus("ACTIVE");
                        newCustomer.setCreatedAt(Instant.now());
                        newCustomer.setUpdatedAt(Instant.now());

                        Customers c = customerRepository.save(newCustomer);
                        //TODO: Send generated pin to the user
                        Map<String, Object> request = new HashMap<>();
                        request.put("idNo", c.getIdNo());
                        LOGGER.info("Activating mobile banking for {}", c.getIdNo());
                        restTemplate.postForEntity(environment.getProperty("cbs.url") + "activate-mobile", request, String.class);
                    }else {
//                        LOGGER.info("Customers table is upto date");
                    }
                }

            }else {
//                LOGGER.warn("No response from cbs");
            }

        }catch (Exception e){
            LOGGER.error("Error occurred. Failed to Synchronize customers from cbs");
            throw new NonRollBackException("Failed to Synchronize customers from cbs");
        }

    }

}
