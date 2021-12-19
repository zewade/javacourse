# Kafka集群K8S部署与开发实践
## 摘要
本文记录了Kafka在K8S上部署过程及Spring访问Kafka代码示例，包括了如下内容：

* 使用helm 3部署bitnami/kafka到Kubernetes 1.22环境
* 使用spring-kafka进行消息收发的简单示例
## Kafka集群部署
### 概述
首先使用helm 3部署3节点ZooKeeper和3节点Kafka集群。如果要开户集群的数据持久化，需要提前在K8S上建立可用的PVC，但由于我的环境中还未搭建好NFS，就暂时配置两者的存储为临时存储作为演示，后续通过更改部署配置，也可以方便地切换为持久化存储。同时，我们使用K8S的NodePort方式对外发布Kafka的节点，以供开发调试使用。

### 搭建ZooKeeper集群
部署3节点的zookeeper集群，关闭持久化存储，并设置服务发布方式为NodePort。NodePort是K8S一种服务发布方式，这样我们就可以通过任意K8S节点IP加指定端口的方式来访问对应的应用。

```bash
helm repo add bitnami https://charts.bitnami.com/bitnami
helm install zookeeper-cluster --set replicaCount=3,persistence.enabled=false,service.type=NodePort bitnami/zookeeper
```
部署成功后会出现访问服务的相关提示，例如可以通过如下命令调用zookeeper客户端查询zookeeper内部数据。

```bash
export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=zookeeper,app.kubernetes.io/instance=zookeeper-cluster,app.kubernetes.io/component=zookeeper" -o jsonpath="{.items[0].metadata.name}")
kubectl exec -it $POD_NAME -- zkCli.sh
```
如果后续需要删除zookeeper的话，就可以使用如下命令：

```bash
helm delete zookeeper-cluster
```


### 搭建Kafka集群
部署3节点的Kafka集群，关闭持久化存储，并设置服务发布方式为NodePort。关闭Kafka内置的ZooKeeper，配置为使用我们刚刚搭建ZooKeeper。

```bash
helm install kafka-cluster --set replicaCount=3,externalAccess.enabled=true,externalAccess.service.type=NodePort,externalAccess.service.port=9094,externalAccess.autoDiscovery.enabled=true,serviceAccount.create=true,persistence.enabled=false,zookeeper.enabled=false,externalZookeeper.servers=zookeeper-cluster:2181,rbac.create=true bitnami/kafka
```
部署成功后也会提示如何通过命令行来访问Kafka，例如通过如下命令可以得到一个Kafka客户端环境，然后在容器内对Kafka进行操作：

```bash
kubectl run kafka-cluster-client --restart='Never' --image docker.io/bitnami/kafka:2.8.1-debian-10-r73 --namespace default --command -- sleep infinity
kubectl exec --tty -i kafka-cluster-client --namespace default -- bash
```
查询Kafka部署状态

```bash
[root@k8s-master1 ~]# kubectl get pods | grep kafka
kafka-cluster-0                                1/1     Running   0          22h
kafka-cluster-1                                1/1     Running   0          22h
kafka-cluster-2                                1/1     Running   0          22h
```
## Spring-kafka代码示例
### 概述
接下去就演示一下SpringBoot下对Kafka的代码访问示例。

### 生成项目结构
可以去start.spring.io生成代码结构，添加Spring-Kafka的项目依赖。Pom文件如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.6.1</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>cn.zewade.course</groupId>
	<artifactId>kafka-demo</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>kafka-demo</name>
	<description>Demo project for Spring Boot</description>
	<properties>
		<java.version>1.8</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.kafka</groupId>
			<artifactId>spring-kafka</artifactId>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
		</dependency>

		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>fastjson</artifactId>
			<version>1.2.73</version>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.kafka</groupId>
			<artifactId>spring-kafka-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>

```
### 配置文件
application.properties

```Plain Text
spring.kafka.bootstrap-servers=${K8S_NODE_IP}:30361
spring.kafka.consumer.auto-offset-reset=latest
spring.kafka.consumer.max-poll-records=100
```
### 生产者代码
生产者相关代码，注入KafkaTemplate，生成NewTopic的Bean就会提前创建好Topic，这是为了避免后续消费者监听时报Topic不存在的警告。在生产环境中一般建议禁用自动创建Topic，改由管理员手工创建Topic。在sendUserMessage中，演示消息头和消息体的发送。

```java
@Component
public class MessagingService {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Bean
    public NewTopic topic1() {
        return TopicBuilder.name("user_sync")
                .partitions(3)
                .replicas(1)
                .compact()
                .build();
    }

    public void sendUserMessage(String topic, User user) throws IOException {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, JSON.toJSONString(user));
        producerRecord.headers().add("type", user.getClass().getName().getBytes(StandardCharsets.UTF_8));
        kafkaTemplate.send(producerRecord);
    }
}
```
User.java

```java
import lombok.Data;

@Data
public class User {

    private Integer id;
    private String userCode;
    private String userName;
}
```
### 消费者代码
通过KafkaListener监听消息，注意不同的groupId指定了不同的消费者组，它们独立消费消息，所以同一消息会被两组各自消费。

```java
@Component
@Slf4j
public class TopicMessageListener {

    @KafkaListener(topics = "user_sync", groupId = "group1")
    public void onLoginMessage(@Payload String message, @Header("type") String type) throws Exception {
        User user = JSON.parseObject(message, getType(type));
        log.info("received user message: {}, group: {}", user, "group1");
    }

    @KafkaListener(topics = "user_sync", groupId = "group2")
    public void processLoginMessage(@Payload String message, @Header("type") String type) throws Exception {
        User user = JSON.parseObject(message, getType(type));
        log.info("received user message: {}, group: {}", user, "group2");
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> getType(String type) {
        // TODO: use cache:
        try {
            return (Class<T>) Class.forName(type);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
```
## 总结
通过helm 3和Spring对Kakfa进行了初步的实践，后续将尝试持久化存储操作和Spring-kafka更加深入的使用。





























