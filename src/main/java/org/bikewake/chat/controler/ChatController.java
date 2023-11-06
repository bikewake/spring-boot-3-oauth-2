package org.bikewake.chat.controler;

import org.bikewake.chat.model.ChatMessage;
import org.bikewake.chat.model.PostMessage;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Date;

@RestController
public class ChatController {

    private final Sinks.Many<ChatMessage> chatSink;

    public ChatController(Sinks.Many<ChatMessage> chatSink) {
        this.chatSink = chatSink;
    }

    @GetMapping(path = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ChatMessage>> chatMessages() {
        return chatSink.asFlux().map(message -> ServerSentEvent.builder(message).build());
    }


    @PostMapping(value = "/chat")
    public void postMessage(@ModelAttribute PostMessage message) {

        OAuth2User user = ((OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        ChatMessage userMessage = new ChatMessage();
        userMessage.setSender(user.getAttribute("email"));
        userMessage.setMessage(message.getMessage());
        userMessage.setTimeStamp(new Date());

        chatSink.tryEmitNext(userMessage);
    }
}
