

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