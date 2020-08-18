package pro.dengyi.rabbitmqdemo.consumer.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MqListener {

    private AtomicInteger flag = new AtomicInteger(1);

    /**
     * 队列不存在时，需要创建一个队列，并且与exchange绑定
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "directQueueName", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "directExchangeName", type = ExchangeTypes.DIRECT),
            key = "routkey"),containerFactory = "customContainerFactory")
    public void listenQueueDirect(String data, Message message, Channel channel) {
        System.err.println(Thread.currentThread().getName());
//        System.err.println("message" + message);
//        System.err.println("channel" + channel);
        System.err.println("接收到数据" + data);
    }
}
