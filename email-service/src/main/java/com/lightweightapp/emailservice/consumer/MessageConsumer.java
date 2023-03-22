package com.lightweightapp.emailservice.consumer;

import com.lightweightapp.emailservice.configuration.MessageQueueConfig;
import com.lightweightapp.emailservice.model.EmailMessageModel;
import com.lightweightapp.emailservice.model.UserVerificationModel;
import com.lightweightapp.emailservice.services.EmailSenderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    @Autowired
    private EmailSenderService emailSenderService;
    @RabbitListener(queues = MessageQueueConfig.QUEUE)
    public void consumerMessageFromQueueForUserVerification(UserVerificationModel userVerificationModel)
    {
        System.out.println("Hi");
        String emailBody  = EmailMessageModel.emailMessage(userVerificationModel.getFirstName());
        String emailRecipient = userVerificationModel.getEmail();
        String emailSubject = "Verify Your Email";
        emailSenderService.sendEmail( emailRecipient, emailSubject,  emailBody);
    }
}
