package pro.dengyi.rabbitmqdemo.producer.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitmq配置类
 * <p>
 * 生产者中主要做：1.队列的声明 2. 交换机的声明 3. 绑定关系
 *
 * @author dengy
 */
@Configuration
public class RabbitConfig {


    //定义队列名
    private String directQueueName = "directQueueName";
    private String topicQueueName = "topicQueueName";
    private String fanoutQueueName = "fanoutQueueName";
    //定义交换机名
    private String directExchangeName = "directExchangeName";
    private String topicExchangeName = "topicExchangeName";
    private String fanoutExchangeName = "fanoutExchangeName";


    /*
     * ----------------------------------------
     * 申明队列
     * <p>
     * ----------------------------------------
     */


    //1. 声明队列,其实对于队列来说没有模式的区别
    // 1.1. durable:是否持久化
    // 1.2 exclusive 有两个作用:
    // 一：当连接关闭时connection.close()该队列是否会自动删除
    // 二：该队列是否是私有的private，如果不是排外的，可以使用两个消费者都访问同一个队列，没有任何问题
    // 如果是排外的，会对当前队列加锁，其他通道channel是不能访问的，如果强制访问会报异常：com.rabbitmq.client.ShutdownSignalException
    //一般等于true的话用于一个队列只能有一个消费者来消费的场景
    // 1.3 autoDelete：是否自动删除，当最后一个消费者断开连接之后队列是否自动被删除
    public Queue directQueue() {
        return new Queue(directQueueName, true, false, false);
    }

    public Queue topicQueue() {
        return new Queue(topicQueueName, true, false, false);
    }

    public Queue fanoutQueue() {
        return new Queue(fanoutQueueName, true, false, false);
    }

    /*
     * ------------------------------------------
     * 声明交换机,事实上各种模式是通过交换机来实现，因此交换机异常重要
     *
     * ------------------------------------------
     */

    /**
     * direct模式也就是点对点模式，通过在绑定队列时置顶的routingkey来路由消息
     *
     * @return
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(directExchangeName, true, false);
    }

    /**
     * topic模式其实是通配符模式*只匹配一级#可以匹配多级
     *
     * @return
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(topicExchangeName, true, false);
    }

    /**
     * fanout模式其实就是广播模式，但是这个广播模式是针对于多个queue来说的，而不是一个queue在多个服务中同时使用
     * <p>
     * 如果一个queue在多个服务中使用，其实一次只能某一个服务中的queue获取到消息
     * 和active中的队列要做好区分，概念不一样
     *
     * @return
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(fanoutExchangeName, true, false);
    }
    //3. 绑定关系

    @Bean
    public Binding bindDirect() {
        //链式写法，绑定交换机和队列，并设置匹配键
        return BindingBuilder
                //绑定队列
                .bind(directQueue())
                //到交换机
                .to(directExchange())
                //并设置匹配键
                .with("routkey");
    }

    @Bean
    public Binding bindTopic() {
        //链式写法，绑定交换机和队列，并设置匹配键
        return BindingBuilder
                //绑定队列
                .bind(topicQueue())
                //到交换机
                .to(topicExchange())
                //*只匹配一级#可以可以匹配多级
                .with("a.b.*");
    }

    @Bean
    public Binding bindFanout() {
        //链式写法，绑定交换机和队列，并设置匹配键
        return BindingBuilder
                //绑定队列
                .bind(fanoutQueue())
                //到交换机
                .to(fanoutExchange());
    }


    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback( new SendConfirmCallback());
        rabbitTemplate.setReturnCallback( new SendReturnCallback());
        return rabbitTemplate;
    }





    /**
     * 配置rabbitadmin使得生产者在启动时自动创建交换机和队列
     *
     * @param rabbitTemplate 模板
     * @return
     */
    @Bean
    public RabbitAdmin rabbitAdmin(RabbitTemplate rabbitTemplate) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitTemplate);
        rabbitAdmin.declareExchange(directExchange());
        rabbitAdmin.declareExchange(topicExchange());
        rabbitAdmin.declareExchange(fanoutExchange());
        //创建队列
        rabbitAdmin.declareQueue(directQueue());
        rabbitAdmin.declareQueue(topicQueue());
        rabbitAdmin.declareQueue(fanoutQueue());
        //声明绑定
        rabbitAdmin.declareBinding(bindDirect());
        rabbitAdmin.declareBinding(bindTopic());
        rabbitAdmin.declareBinding(bindFanout());

        return rabbitAdmin;
    }


}
