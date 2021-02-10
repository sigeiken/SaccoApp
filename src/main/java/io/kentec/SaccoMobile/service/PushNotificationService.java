package io.kentec.SaccoMobile.service;

import io.kentec.SaccoMobile.entity.SystemMessages;
import io.kentec.SaccoMobile.repository.SystemMessagesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PushNotificationService {

    @Autowired
    private SystemMessagesRepository systemMessagesRepository;

    @Autowired
    private SmsSenderService smsSenderService;

    private static final Logger LOGGER = LoggerFactory.getLogger(PushNotificationService.class);

    @Scheduled(fixedDelay =3000)
    public void sendNotification(){
        try {
            List<SystemMessages> messages = systemMessagesRepository.findMessagesByStatus("0");
            if (messages != null){
                LOGGER.info("{} pending Message(s)", messages.size());
                for (SystemMessages msg : messages) {
                    if (msg.getMsgType().equals("SMS")){
                        //Call Twilio
                        smsSenderService.sendSMS(msg.getRecepient(), msg.getMessage());
                        msg.setStatus("1");
                        msg.setDeliveryStatus("DELIVERED"); //if message is sent successfully by twilio
                    }else { //Send email

                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error("Error sending notification " + e.getMessage());
        }
    }
}
