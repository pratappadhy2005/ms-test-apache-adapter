package com.au.pratap;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.jms.Message;
import java.util.List;

@Component
//@RequiredArgsConstructor
@NoArgsConstructor
public class SampleListener {


    @Autowired
    @Qualifier("acctChgPublishJmsTemplateMq1")
    JmsTemplate jmsTemplate;

    @JmsListener(containerFactory="acctChgPublisherContainerFactoryMq1", destination="firstQueue")
    public void receiveStatusUpdateEventMq1(List<Message> messages) throws Exception {
        System.out.println(jmsTemplate);
        receiveStatusUpdate( messages);
    }

    private void receiveStatusUpdate( List<Message> messages) throws Exception {
        System.out.println("Inside receiveStatusUpdate");
    }
}
