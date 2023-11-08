package org.bikewake.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {

    @Id
    private Long id;

    private String sender;
    private String message;
    private Long timeStamp;
}
