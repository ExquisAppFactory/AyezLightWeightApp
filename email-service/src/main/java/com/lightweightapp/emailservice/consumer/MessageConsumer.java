package com.lightweightapp.emailservice.consumer;

import com.lightweightapp.emailservice.configuration.MessageQueueConfig;
import com.lightweightapp.emailservice.model.UserVerificationModel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    @RabbitListener(queues = MessageQueueConfig.QUEUE)
    public void consumerMessageFromQueue(UserVerificationModel userVerificationModel)
    {
        System.out.println("Hi");
    }
}
