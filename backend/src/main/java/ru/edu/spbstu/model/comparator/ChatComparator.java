package ru.edu.spbstu.model.comparator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.edu.spbstu.dao.MessageRepository;
import ru.edu.spbstu.model.Chat;
import ru.edu.spbstu.model.jpa.MessageJpa;

import java.util.Comparator;

@AllArgsConstructor
@Component
public class ChatComparator implements Comparator<Chat> {

    private final MessageRepository messageRepository;

    @Override
    public int compare(Chat o1, Chat o2) {
        MessageJpa message1 = messageRepository.getNewestMessageByChatId(o1.getId()).get(0);
        MessageJpa message2 = messageRepository.getNewestMessageByChatId(o2.getId()).get(0);
        if (message1.getDate().before(message2.getDate())) {
            return 1;
        }
        else if (message2.getDate().before(message1.getDate())) {
            return -1;
        }
        return 0;
    }
}
