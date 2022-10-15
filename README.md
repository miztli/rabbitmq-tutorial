

### Start RabbitMQ instance

```shell
docker run -d --hostname my-rabbit --name miztli-rabbit-mq-server -p 5672:5672 rabbitmq:3
```

Now let's test rabbitmq is listening for incoming connections:
```shell
ssh -p 5672 localhost
```

Default username: `guest`

Default password: `guest`

### Start producer instance

```shell
mvn spring-boot:run
```

Test producer api with:

```shell
curl --location --request POST 'http://localhost:8080/publish' \
--header 'Content-Type: application/json' \
--data-raw '{
    "message":"Hello world from exchange!"
}'
```

### Start consumer instance(s)

```shell
mvn spring-boot:run
```

If we need to simulate several instances, we need to override the `application.properties server.port` to avoid collisions

```shell
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8082
```

## Working queues

### Auto-acknowledge
- Demo commit: `6e94987cc34555771df9be78edfb683720952407`
- Setup: 

producer **->** _exchange:_ default ("")

consumer 1 **->** _queue:_ events-queue

consumer 2 **->** _queue:_ events-queue

_NOTES:_ 
- Queues are by default bound to the default exchange, since we didn't specify bindings at configuration time
- Server will use [round-robin](https://www.rabbitmq.com/tutorials/tutorial-two-java.html) dispatching strategy to deliver messages.
- Notice we're using `autoAck=true` every time we consume a message

### Manual-acknowledge
- Demo commit: `222adb572c36c69fe62ae7020df2777ea7e13249`

_NOTES_
- With our current code, once RabbitMQ delivers a message to the consumer it immediately marks it for deletion. In this case, if you kill a worker we will lose the message it was just processing.
- Manual acknowledgments are useful to let the server know we have successfully processed a message and it's free to delete it.
- If server doesn't receive an acknowledgment message, then it'll re-queue the message and deliver it to the next available consumer. A timeout (30 minutes by default) is enforced on consumer delivery acknowledgment. How to modify the default timeout can be consulted [here](https://www.rabbitmq.com/consumers.html#acknowledgement-timeout)
- Set property `consumer.processingTimeInSeconds=30` and stop the consumer to see message is redelivered to the other instance of our application: `mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081 --consumer.processingTimeInSeconds=30 --instanceId=1"`

### Pre-fetch
- Demo commit: `19a2c185014175f3870d728f46537cf01d082090`

_NOTES_
- Allows the consumer to tell the server to send not more than `n` number of messages at a time, before the server receives the acknowledgment from the client.
- Be aware about the que size in this case, because it may become full if there's no enough consumers for all the messages

### Exchanges & Bindings

- Demo commit: `a6b44834e572872021c59d28c5ba98dd7bce3e1b`

_NOTES_
- Is a concept introduced by rabbitMQ to decouple producers and consumers. Producers will publish messages on the exchange, and the exchange will deliver this message to the queues. In this way it decouples the producers from the consumers, meaning that producers doesn't even know if the message is delivered to a queue. Message will not be delivered to any queue unless we define the bindings, if not defined, message is discarded or sent to dead letter queue.
- Exchange types: direct, topic, headers, fanout

  - fanout: Broadcasts all the messages it receives to all the queues it knows.
- The relationship between a queue and an exchange is called: BINDING