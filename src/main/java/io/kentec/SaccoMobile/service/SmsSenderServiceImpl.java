package io.kentec.SaccoMobile.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class SmsSenderServiceImpl implements SmsSenderService{

    @Autowired
    private Environment environment;

    private static final Logger LOGGER = LoggerFactory.getLogger(SmsSenderServiceImpl.class);

    @Override
    public void sendSMS(String recipient, String message) throws Exception {
        try {
            Twilio.init(environment.getRequiredProperty("twilio.ACCOUNT_SID"), environment.getRequiredProperty("twilio.AUTH_TOKEN"));
            Message msg = Message
                    .creator(new PhoneNumber("+" + recipient), // to
                            new PhoneNumber("+15153258821"), // from
                            message)
                    .create();
            LOGGER.info("Message sent to Phone Number {}, Message {}", recipient, message);
        }catch (Exception e){
            throw new Exception("Error occured " + e.getMessage());
        }
    }
}
