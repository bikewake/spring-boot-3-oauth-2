package org.bikewake.chat.controler;

import io.netty.util.CharsetUtil;
import org.bikewake.chat.model.ChatMessage;
import org.bikewake.chat.model.PostMessage;
import org.bikewake.chat.repository.ChatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@RestController
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final static String USER_NAME_ATTRIBUTE = "name";
    private final static String USER_EMAIL_ATTRIBUTE = "email";
    private final static Long NUMBER_OF_DATABASE_RECORDS = 42L;
    private final ChatRepository chatRepository;
    private final Sinks.Many<ChatMessage> chatSink;

    public ChatController(ChatRepository chatRepository,
                          Sinks.Many<ChatMessage> chatSink) {
        this.chatRepository = chatRepository;
        this.chatSink = chatSink;
    }

    @GetMapping(path = "/sse-chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ChatMessage>> chatMessages() {
        return chatSink.asFlux().map(message -> ServerSentEvent.builder(message).build());
    }


    @PostMapping(value = "/chat")
    public void postMessage(@ModelAttribute PostMessage message) {

        OAuth2User user = ((OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        ChatMessage userMessage = new ChatMessage();
        userMessage.setSender(HtmlUtils.htmlEscape(user.getAttribute(USER_NAME_ATTRIBUTE), CharsetUtil.UTF_8.displayName()));
        userMessage.setMessage(HtmlUtils.htmlEscape(message.getMessage(), CharsetUtil.UTF_8.displayName()));
        userMessage.setTimeStamp(System.currentTimeMillis());

        chatSink.tryEmitNext(userMessage);
        chatRepository.save(userMessage).subscribe();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void firstChatMessageAfterStartup() {
        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setSender("System");
        systemMessage.setMessage("Chat Started");
        systemMessage.setTimeStamp(System.currentTimeMillis());

        chatRepository.save(systemMessage).subscribe();
        logger.debug("System Chat Started");
        chatSink.asFlux().subscribe();
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent success) {

        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setSender(HtmlUtils.htmlEscape(((OAuth2User) success.getAuthentication()
                .getPrincipal()).getAttribute(USER_NAME_ATTRIBUTE), CharsetUtil.UTF_8.displayName()));
        systemMessage.setMessage(HtmlUtils.htmlEscape(((OAuth2User) success.getAuthentication()
                .getPrincipal()).getAttribute(USER_EMAIL_ATTRIBUTE), CharsetUtil.UTF_8.displayName()));
        systemMessage.setTimeStamp(System.currentTimeMillis());
        chatSink.tryEmitNext(systemMessage);
        chatRepository.save(systemMessage).subscribe();
    }

    @Scheduled(fixedRateString = "${chat.keep.alive}", initialDelay = 10000)
    public void periodicalSystemKeepAliveMessage() {
        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setSender("System");
        systemMessage.setMessage("");
        systemMessage.setTimeStamp(System.currentTimeMillis());
        chatSink.tryEmitNext(systemMessage);
        checkDeleteRecords();
        logger.debug("Number of chat sink subscriber {}", chatSink.currentSubscriberCount());
    }

    private void checkDeleteRecords() {
        chatRepository.count().subscribe(
                allRecordsCount -> {
                    if (allRecordsCount > NUMBER_OF_DATABASE_RECORDS) {
                        chatRepository.deleteOldRecords(allRecordsCount - NUMBER_OF_DATABASE_RECORDS).subscribe();
                    }
                }
        );
    }
}
