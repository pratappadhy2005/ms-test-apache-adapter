package com.au.pratap.config;

import com.au.pratap.factory.BatchJmsListenerContainerFactory;
import com.au.pratap.model.StripFileTransaction;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;

@EnableJms
@Configuration
public class SpringBatchJmsConfig {
//public class SpringBatchJmsConfig{


    public static final Logger logger = LoggerFactory.getLogger(SpringBatchJmsConfig.class.getName());

    @Bean
    public ActiveMQConnectionFactory receiverActiveMQConnectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory =
                new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL("tcp://localhost:61616");
        return activeMQConnectionFactory;
    }


    @Bean(name="acctChgPublisherContainerFactoryMq1")
    public BatchJmsListenerContainerFactory acctChgPublisherContainerFactoryMq1() {
        // System.out.println("Inside acctChgPublisherContainerFactoryMq1 factory");
        BatchJmsListenerContainerFactory factory = new BatchJmsListenerContainerFactory();
        factory.setConnectionFactory(receiverActiveMQConnectionFactory());
        return factory;
    }


    @Bean(name="acctChgPublisherContainerFactoryMq2")
    public DefaultJmsListenerContainerFactory acctChgPublisherContainerFactoryMq2() {
        // System.out.println("Inside acctChgPublisherContainerFactoryMq1 factory");
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(receiverActiveMQConnectionFactory());
        return factory;
    }

    @Bean
    public MessageConverter messageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @Bean(name="acctChgPublishJmsTemplateMq1")
    public JmsTemplate acctPublishJmsTemplateMq1() throws JMSException {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDeliveryMode(DeliveryMode.PERSISTENT);
        jmsTemplate.setConnectionFactory(receiverActiveMQConnectionFactory());
        //System.out.println("Inside Jms Template bean");
        return jmsTemplate;
    }



}

