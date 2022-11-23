package model.comparator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.edu.spbstu.dao.MessageRepository;
import ru.edu.spbstu.model.Chat;
import ru.edu.spbstu.model.comparator.ChatComparator;
import ru.edu.spbstu.model.jpa.MessageJpa;

import java.util.Date;
import java.util.List;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ChatComparatorTest {

    @InjectMocks
    private ChatComparator chatComparator;

    @Mock
    private MessageRepository messageRepository;

    @Test
    public void compareTo_test() {
        Chat chat1 = new Chat(1L, "name1");
        Chat chat2 = new Chat(2L, "name2");
        Chat chat3 = new Chat(3L, "name3");
        Date date1 = new Date(100L);
        Date date2 = new Date(101L);
        MessageJpa messageJpa1 = new MessageJpa();
        messageJpa1.setDate(date1);
        MessageJpa messageJpa2 = new MessageJpa();
        messageJpa2.setDate(date2);

        given(messageRepository.getNewestMessageByChatId(chat1.getId())).willReturn(List.of(messageJpa1));
        given(messageRepository.getNewestMessageByChatId(chat2.getId())).willReturn(List.of(messageJpa2));
        given(messageRepository.getNewestMessageByChatId(chat3.getId())).willReturn(List.of(messageJpa1));

        Assertions.assertEquals(1, chatComparator.compare(chat1, chat2));
        Assertions.assertEquals(0, chatComparator.compare(chat1, chat3));
        Assertions.assertEquals(-1, chatComparator.compare(chat2, chat1));
    }
}
