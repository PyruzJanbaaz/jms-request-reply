package com.pyruz.rpc;

import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.concurrent.Callable;

@Component
public class CallableThread implements Callable<String> {

    TextMessage response;
    Message message;
    MessageProducer replyProducer;
    Destination destination;
    Session session;

    public CallableThread(TextMessage response,
                          Message message,
                          MessageProducer replyProducer,
                          Destination destination,
                          Session session) {
        this.response = response;
        this.message = message;
        this.replyProducer = replyProducer;
        this.destination = destination;
        this.session = session;
    }

    @Override
    public String call() throws Exception {
        process();
        return "Done !";
    }

    private void process() throws JMSException {
        // You can Write your own code, this block is a simple example that shows how to you can fetch and process request and return response
        TextMessage txtMsg = (TextMessage) message;
        try {
            if (message instanceof TextMessage) {
                String message = txtMsg.getText();
                response.setText(message + " RESPONSE!");
                System.out.println(response.getText());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" ERROR MESSAGE IS : " + e.getMessage());
            System.out.println(txtMsg.getText());
        } finally {
            response.setJMSCorrelationID(txtMsg.getJMSCorrelationID());
            this.replyProducer.send(txtMsg.getJMSReplyTo(), response);
        }
    }

}


