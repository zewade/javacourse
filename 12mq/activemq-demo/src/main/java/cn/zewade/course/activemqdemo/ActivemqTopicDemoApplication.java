package cn.zewade.course.activemqdemo;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class ActivemqTopicDemoApplication {

    private static final String BROKER_URL = "tcp://192.168.2.136:61616";
    private static final int SEND_NUM = 100; // 定义发送的消息数量

    public static void main(String[] args) {
        // 连接
        Connection connection = null;
        // 会话，接收或者发送消息的线程
        Session session = null;

        try {
            // 连接工厂，用来生产Connection
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
            // 通过连接工厂获取连接
            connection = connectionFactory.createConnection();
            // 启动连接
            connection.start();
            // 获取Session
            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            // 创建消息队列，名为FirstQueue1
            Destination destination = session.createTopic("FirstQueue1");
            // 创建消息消费者
            MessageConsumer messageConsumer = session.createConsumer(destination);
            //注册消息监听
            messageConsumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        System.out.println("收到的消息：" + ((TextMessage)message).getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });
            // 创建消息生产者
            MessageProducer messageProducer = session.createProducer(destination);
            // 发送消息
            sendMessage(session, messageProducer);
            // 加了事务，所以要commit
            // TODO:加了事务控制会显示有消息Pending,待排查
//            session.commit();
            // 等待消息处理完
            Thread.sleep(10000);
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (session != null) {
                    session.close();
                }
                if(connection != null){
                    connection.close();
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendMessage(Session session, MessageProducer messageProducer) throws JMSException {
        for(int i = 1; i <= SEND_NUM; i++) {
            TextMessage message = session.createTextMessage("ActiveMQ 发送的消息" + i);
            System.out.println("ActiveMQ 发送的消息" + i);
            messageProducer.send(message);
        }
    }
}
