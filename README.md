

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

### Start consumer instance(s)

```shell
mvn spring-boot:run
```

If we need to simulate several instances, we need to override the `application.properties server.port` to avoid collisions

```shell
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8082
```

## Working queues
- Demo commit: `87d9947670344812519e0757ea347ca5672b597f`
- Setup: 

producer **->** _exchange:_ default ("")

consumer 1 **->** _queue:_ events-queue

consumer 2 **->** _queue:_ events-queue

_NOTES:_ 
- Queues are by default bound to the default exchange, since we didn't specify bindings at configuration time
- Server will use [round-robin](https://www.rabbitmq.com/tutorials/tutorial-two-java.html) dispatching strategy to deliver messages.