package com.makeprojects.ewallet.shared.kafka.error;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.FixedBackOff;

import javax.sql.rowset.serial.SerialException;

@Component
@Slf4j
public class KafkaConsumerErrorHandler extends DefaultErrorHandler {

    public KafkaConsumerErrorHandler() {
        // Retry 3 times with a 5-second delay
        super(new FixedBackOff(5000L, 3));
    }

//    @Override
//    public Object handleError(Message<?> message, ListenerExecutionFailedException exception, Consumer<?, ?> consumer) {
//        Throwable cause = exception.getCause();
//
//        if (cause instanceof SerialException) {
//            log.error("Kafka Deserialization error: {}", cause.getMessage());
//            consumer.seek(consumer.assignment().iterator().next(), consumer.position(consumer.assignment().iterator().next()) + 1);
//            return null;    // Skip the corrupt message
//        }
//
//        log.error("Kafka Consumer Error: {}", exception.getMessage());
//        return null;
//    }

    @Override
    public void handleOtherException(Exception thrownException, Consumer<?, ?> consumer, MessageListenerContainer container, boolean batchListener) {
        if (thrownException instanceof SerializationException) {
            log.error("Serialization error: {}", thrownException.getMessage());
            // Skip the corrupted message by moving to the next offset
            consumer.seek(consumer.assignment().iterator().next(),
                    consumer.position(consumer.assignment().iterator().next()) + 1);
        } else {
            log.error("Kafka Consumer Exception: {}", thrownException.getMessage());
        }
    }
}
