package org.bikewake.chat.config;

import org.bikewake.chat.model.ChatMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

@Configuration
public class ChatConfiguration {

    @Bean
    public Sinks.Many<ChatMessage> chatSink() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }
}