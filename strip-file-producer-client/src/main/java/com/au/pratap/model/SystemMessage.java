package com.au.pratap.model;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.activemq.command.ActiveMQObjectMessage;

import javax.jms.JMSException;

/**
 * @author Pratap
 */
@ToString
public class SystemMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private String source;

    @Getter
    @Setter
    private String messageText;

    @Getter
    @Setter
    private String bsb;

    @Getter
    @Setter
    private String accountNumber;

    @Getter
    @Setter
    private String accountId;
}
