package org.bikewake.chat.repository;

import org.bikewake.chat.model.ChatMessage;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ChatRepository  extends ReactiveCrudRepository<ChatMessage, Long> {

    @Modifying
    @Query("DELETE FROM CHAT_MESSAGE WHERE id IN (SELECT id FROM CHAT_MESSAGE LIMIT :recordCount)")
    Mono<Void> deleteOldRecords(Long recordCount);
}
