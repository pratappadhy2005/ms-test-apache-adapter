package com.au.pratap.factory;

import org.springframework.jms.connection.ConnectionFactoryUtils;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.jms.listener.adapter.MessagingMessageListenerAdapter;
import org.springframework.jms.support.JmsUtils;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.transaction.TransactionStatus;

import javax.jms.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Listener Container that allows batch consumption of messages. Works only with transacted sessions
 * Original : https://github.com/sanjayvacharya/sleeplessinslc/tree/master/messageproxy
 */
public class BatchMessageListenerContainer extends DefaultMessageListenerContainer {
    public static final int DEFAULT_BATCH_SIZE = 20;

    private int batchSize = DEFAULT_BATCH_SIZE;

    public BatchMessageListenerContainer() {
        super();
        setSessionTransacted(true);
    }

    /**
     * @return The batch size on this container
     */
    public int getBatchSize() {
        return batchSize;
    }

    /**
     * @param batchSize The batchSize of this container
     */
    public void setBatchSize(int batchSize) {
        this.batchSize = 20;
    }

    /**
     * The doReceiveAndExecute() method has to be overriden to support multiple-message receives.
     */
    @Override
    protected boolean doReceiveAndExecute(Object invoker, Session session, MessageConsumer consumer,
                                          TransactionStatus status) throws JMSException {
        Connection conToClose = null;
        MessageConsumer consumerToClose = null;
        Session sessionToClose = null;


        // System.out.println("Getting the container");
        //String containerMethod = container.handlerMethod.method.getName();

        try {
            Session sessionToUse = session;
            MessageConsumer consumerToUse = consumer;

            if (sessionToUse == null) {
                Connection conToUse = null;
                if (sharedConnectionEnabled()) {
                    conToUse = getSharedConnection();
                } else {
                    conToUse = createConnection();
                    conToClose = conToUse;
                    conToUse.start();
                }
                sessionToUse = createSession(conToUse);
                sessionToClose = sessionToUse;
            }

            if (consumerToUse == null) {
                consumerToUse = createListenerConsumer(sessionToUse);
                consumerToClose = consumerToUse;
            }

            List<Message> messages = new ArrayList<Message>();

            int count = 0;
            Message message = null;
            // Attempt to receive messages with the consumer
            do {
                message = receiveMessage(consumerToUse);
                if (message != null) {
                    messages.add(message);
                }
            }
            // Exit loop if no message was received in the time out specified, or
            // if the max batch size was met
            while ((message != null) && (++count < batchSize));

            if (messages.size() > 0) {
                // Only if messages were collected, notify the listener to consume the same.
                try {
                    doExecuteListener(sessionToUse, messages);
                    sessionToUse.commit();
                } catch (Throwable ex) {
                    handleListenerException(ex);
                    if (ex instanceof JMSException) {
                        throw (JMSException) ex;
                    }
                }
                return true;
            }

            // No message was received for the period of the timeout, return false.
            noMessageReceived(invoker, sessionToUse);
            return false;
        } finally {
            JmsUtils.closeMessageConsumer(consumerToClose);
            JmsUtils.closeSession(sessionToClose);
            ConnectionFactoryUtils.releaseConnection(conToClose, getConnectionFactory(), true);
        }
    }

    protected void doExecuteListener(Session session, List<Message> messages) throws JMSException {
        System.out.println("Message Size inside container:" + messages.size());
        if (!isAcceptMessagesWhileStopping() && !isRunning()) {
            if (logger.isWarnEnabled()) {
                logger.warn("Rejecting received messages because of the listener container "
                        + "having been stopped in the meantime: " + messages);
            }
            rollbackIfNecessary(session);
            throw new JMSException("Rejecting received messages as listener container is stopping");
        }
        //Delegate

        // How to pass this list of message to the method which has annotation as @JMSListener
        MessagingMessageListenerAdapter container = (MessagingMessageListenerAdapter)getMessageListener();
        Method method = null;
        String methodName = null;

        System.out.println("MY Queue destination######" +messages.get(0).getJMSDestination());
        try {
            if(messages.size()>0) {
                method = container.getClass().getDeclaredMethod("getHandlerMethod");
                method.setAccessible(true);
                InvocableHandlerMethod methodNameObject = (InvocableHandlerMethod)method.invoke(container);
                methodName = methodNameObject.getMethod().getName();
                System.out.println("Message size is " + messages.size() + " in Queue " +messages.get(0).getJMSDestination() + " and the handler method is " + methodName);
                Class.forName("com.au.pratap."+"SampleListener").getMethod(methodName, List.class).invoke(Class.forName("com.au.pratap."+"SampleListener").newInstance(), messages);

            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();



        }/* catch (JMSException ex) {
            rollbackOnExceptionIfNecessary(session, ex);
            throw ex;
        }*/ catch (RuntimeException ex) {
            rollbackOnExceptionIfNecessary(session, ex);
            throw ex;
        } catch (Error err) {
            rollbackOnExceptionIfNecessary(session, err);
            throw err;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void validateConfiguration() {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("Property batchSize must be a value greater than 0");
        }
    }

    public void setSessionTransacted(boolean transacted) {
        if (!transacted) {
            throw new IllegalArgumentException("Batch Listener requires a transacted Session");
        }
        super.setSessionTransacted(transacted);
    }
}

