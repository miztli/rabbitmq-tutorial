

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