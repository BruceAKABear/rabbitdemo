package pro.dengyi.rabbitmqdemo.producer.config;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * 发送到交换机回调
 *
 * @author dengy
 */
public class SendConfirmCallback implements RabbitTemplate.ConfirmCallback {
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            System.out.println("发送到exchange");
        } else {
            System.err.println("未发送到交换机");
        }
    }
}
