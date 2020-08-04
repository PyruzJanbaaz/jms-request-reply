# Jms-Request-Reply(responder)
The JMS response application

- Spring 4.3.7.RELEASE
- Apache Activemq 5.6.0
- JavaSE 1.8
- Maven 3.3.9


# Usage

Just run the application and enjoy it ;)


# How it works...

My ([requester application](https://github.com/PyruzJanbaaz/Spring-Apache-Camel-RestletJmsRqerustReply)) places several requests onto a queue on our ActiveMQ Broker, and then it waits until all its outstanding requests are processed before finishing. In order to implement our responder application, I assemble JMS elements I did in the requester example; lets take a look at this in the following code:

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
        
        


As you can see, I create both a producer and consumer for response example once again; The MessageProducer object I created was assigned a null destination; this is called an anonymous producer. We can use the anonymous producer to send messages to any destination.
I'd rather not create a new MessageProducer object every time a message arrives since that would add more network traffic and load to our broker.
When a request message is received by the responder application, it queries the message for the JMSReplyTo destination to which it should send the response. Once the responder knows where to send its answer, it constructs the appropriate response message and assigns it the JMSCorrelationID method that the requester will use to identify the response prior to sending it back to the responder.

Now I can start to imagine how my request/response applications can scale as the number of responders can grow with the increasing workload. If I need a new responder, I can spin up another instance on a different machine; it will share the load with all the others by taking one request at a time off the shared request queue and eventually sending its response back when its done. If, for instance, each request takes about a minute to complete, my sample would finish in about ten minutes. But if I add another responder application, I can cut that time in half.


        
