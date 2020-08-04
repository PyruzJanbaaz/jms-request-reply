package com.pyruz.rpc;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.jms.*;

@Component
public class Server implements MessageListener, ExceptionListener {

    private static int ackMode;
    private static String inboundQueue;
    private static String messageBrokerUrl;
    private Session session;
    private boolean transacted = false;
    private MessageProducer messageProducer;
    private static volatile Server instance = null;
    private Connection connection;
    private ActiveMQConnectionFactory connectionFactory;
    private ThreadPoolConfiguration threadPoolConfiguration = new ThreadPoolConfiguration();
    private ThreadPoolTaskExecutor threadPool = threadPoolConfiguration.taskExecutor();

    static {
        ackMode = Session.AUTO_ACKNOWLEDGE;
    }

    public static Server getInstance() {
        if (instance == null) {
            synchronized (Server.class) {
                instance = new Server();
            }
        }
        return instance;
    }

    public Server() {
        try {
            getBrokerConnection();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    private void getBrokerConnection() throws JMSException {
        connectionFactory = new ActiveMQConnectionFactory(messageBrokerUrl);
        connection = connectionFactory.createConnection();
        connection.start();
        this.session = connection.createSession(this.transacted, ackMode);
        connection.setExceptionListener(this);
        this.setupMessageQueueConsumer();
    }


    public void setupMessageQueueConsumer() {
        try {
            Destination tcpQueue = this.session.createQueue(inboundQueue);
            this.messageProducer = this.session.createProducer(null);
            this.messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            MessageConsumer consumer = this.session.createConsumer(tcpQueue);
            consumer.setMessageListener(this);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void onMessage(Message message) {
        try {
            TextMessage response = this.session.createTextMessage();
            CallableThread callableTask = new CallableThread(response, message, messageProducer, null, this.session);
            threadPool.submit(callableTask);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        init();
        Server.getInstance();
    }


    public static void init() {
        messageBrokerUrl = "failover://tcp://127.0.0.1:61616";
        inboundQueue = "inbound?consumer.priority=10";
    }

    @Override
    public void onException(JMSException exception) {
        exception.printStackTrace();
    }


}







