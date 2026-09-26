package com.eventbooking.event_booking_platform.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.eventbooking.event_booking_platform.dto.BookingCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class KafkaProducerConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Bean
	public ProducerFactory<String, BookingCreatedEvent> bookingProducerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

		return new DefaultKafkaProducerFactory<>(configProps, new StringSerializer(),
				new JsonSerializer<>(objectMapper));
	}

	@Bean
	public KafkaTemplate<String, BookingCreatedEvent> kafkaTemplate(
			ProducerFactory<String, BookingCreatedEvent> bookingProducerFactory) {
		return new KafkaTemplate<>(bookingProducerFactory);
	}

}
