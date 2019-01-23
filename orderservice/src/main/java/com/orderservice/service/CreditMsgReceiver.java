package com.orderservice.service;

import common.model.Credit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CreditMsgReceiver {

    static final Logger logger = LoggerFactory.getLogger(CreditMsgReceiver.class);

    public void messageHandler(Credit credit) {
        logger.info("CreditMsgReceiver : " + credit.toString());
    }
}
