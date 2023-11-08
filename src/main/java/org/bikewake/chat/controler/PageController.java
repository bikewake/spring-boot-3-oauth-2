package org.bikewake.chat.controler;

import org.bikewake.chat.repository.ChatRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    private final ChatRepository chatRepository;

    public PageController(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;

    }

    @RequestMapping("/")
    public String index(final Model model) {
        model.addAttribute("chats", chatRepository.findAll().collectList().block());
        return "index";
    }
}
