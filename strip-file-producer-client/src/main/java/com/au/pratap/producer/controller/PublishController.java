package com.au.pratap.producer.controller;


import com.au.pratap.model.StripFileTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
/**
 * @author Pratap
 */
@RestController
public class PublishController {
    protected int numberOfMessagesForQueue1 = 30;
    protected int numberOfMessagesForQueue2 = 50;
    protected int numberOfMessagesForQueue3 = 100;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Bean
    public MessageConverter messageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @PostMapping("/publishMessagetoQueue1")
    public ResponseEntity<String> publishMessage(@RequestBody StripFileTransaction systemMessage) {
        try {
            for (int i = 0; i < numberOfMessagesForQueue1; ++i) {
                final int count = i;
                StripFileTransaction person = new StripFileTransaction("MessageCode"+count, "MessageText"+count, "333333"+count, "1472523"+count, "5345345"+count);
                jmsTemplate.convertAndSend("firstQueue", person);
            }

            return new ResponseEntity<>("Sent.", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/publishMessagetoQueue2")
    public ResponseEntity<String> publishMessagetoQueue2(@RequestBody StripFileTransaction systemMessage) {
        try {
            for (int i = 0; i < numberOfMessagesForQueue2; ++i) {
                final int count = i;
                StripFileTransaction person = new StripFileTransaction("MessageCode"+count, "MessageText"+count, "333333"+count, "1472523"+count, "5345345"+count);
                jmsTemplate.convertAndSend("secondQueue", person);
            }

            return new ResponseEntity<>("Sent.", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/publishMessagetoQueue3")
    public ResponseEntity<String> publishMessagetoQueue3(@RequestBody StripFileTransaction systemMessage) {
        try {
            for (int i = 0; i < numberOfMessagesForQueue3; ++i) {
                final int count = i;
                StripFileTransaction person = new StripFileTransaction("MessageCode"+count, "MessageText"+count, "333333"+count, "1472523"+count, "5345345"+count);
                jmsTemplate.convertAndSend("thirdQueue", person);
            }

            return new ResponseEntity<>("Sent.", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
