# Jms-Request-Reply
The JMS request application

My ([simpler requester application](https://github.com/PyruzJanbaaz/Spring-Apache-Camel-RestletJmsRqerustReply)) places several requests onto a queue on our ActiveMQ Broker, and then it waits until all its outstanding requests are processed before finishing.In order to implement our simple responder application, I assemble many of the same JMS elements I did in the requester example; lets take a look at this in the following code:

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
        
        
        
        
