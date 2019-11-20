一个生产者，对应三个消费者，这里不定义队列，仅仅定义交换机


## 创建生产者容器

    docker run -di --name interviewjob-rabbitmq-producer -p 45000:45000 -v /var/log/interviewjob/producer:/var/log/interviewjob -e PORT=45000 -e logdir=/var/log/interviewjob -e rabbitmqIP=192.168.72.142 -e rabbitmqPORT=5672 interviewjob-rabbitmq-producer:latest
