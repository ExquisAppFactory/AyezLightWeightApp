package com.lightweightapp.walletservice.consumer;

import com.lightweightapp.walletservice.configuration.MessageQueueConfig;
import com.lightweightapp.walletservice.dbResource.Wallet;
import com.lightweightapp.walletservice.model.UserWalletCreationModel;
import com.lightweightapp.walletservice.repository.WalletRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class MessageConsumer {

    private WalletRepository walletRepository;

    public MessageConsumer(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }


    @RabbitListener(queues = MessageQueueConfig.QUEUE)
    public void consumeMessageFromQueueForWalletCreation (UserWalletCreationModel userWalletCreationModel)
    {
        //Perform Wallet Creation
        Wallet checkForCreatedWallet = walletRepository.findById(userWalletCreationModel.getUserId());
        if(checkForCreatedWallet == null)
        {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            walletRepository.save(new Wallet(userWalletCreationModel.getUserId(), 1000,  timestamp));
        }
    }
}
