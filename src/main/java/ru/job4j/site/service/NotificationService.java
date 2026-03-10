package ru.job4j.site.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.job4j.site.dto.*;
import ru.job4j.site.events.SubscribeEvent;
import ru.job4j.site.util.RestAuthCall;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class NotificationService {

    private final EurekaUriProvider uriProvider;
    private final RestAuthCall restAuthCall;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String SERVICE_ID = "notification";

    public void addSubscribeCategory(int userId, int categoryId) {
        var event = new SubscribeEvent<>(
            new SubscribeCategory(userId, categoryId),
            SubscribeEvent.Action.ADD
        );
        kafkaTemplate.send("notification-subscribe", event);
    }

    public void deleteSubscribeCategory(int userId, int categoryId) {
        var event = new SubscribeEvent<>(
            new SubscribeCategory(userId, categoryId),
            SubscribeEvent.Action.DELETE
        );
        kafkaTemplate.send("notification-subscribe", event);
    }

    public void addSubscribeTopic(int userId, int topicId) {
        var event = new SubscribeEvent<>(
            new SubscribeTopicDTO(userId, topicId),
            SubscribeEvent.Action.ADD
        );
        kafkaTemplate.send("notification-subscribe", event);
    }

    public void deleteSubscribeTopic(int userId, int topicId) {
        var event = new SubscribeEvent<>(
            new SubscribeTopicDTO(userId, topicId),
            SubscribeEvent.Action.DELETE
        );
        kafkaTemplate.send("notification-subscribe", event);
    }

    public void notifyAboutInterviewCreation(CategoryWithTopicDTO dto) {
        kafkaTemplate.send("notification-interviews", dto);
    }

    public void sendSubscribeTopic(InterviewNotifyDTO dto) {
        kafkaTemplate.send("notification-interviews", dto);
    }

    public void sendParticipateAuthor(WisherNotifyDTO dto) {
        kafkaTemplate.send("notification-interviews", dto);
    }

    public void sendParticipateCancelInterview(CancelInterviewNotificationDTO dto) {
        kafkaTemplate.send("notification-interviews", dto);
    }

    public void sendParticipantIsDismissed(List<WisherDismissedDTO> dtoList) {
        kafkaTemplate.send("notification-interviews", dtoList);
    }

    public void sendFeedBackMessage(InnerMessageDTO dto) {
        kafkaTemplate.send("notification-messages", dto);
    }

    public void sendFeedbackNotification(FeedbackNotificationDTO dto) {
        kafkaTemplate.send("notification-messages", dto);
    }

    public void approvedWisher(WisherApprovedDTO dto) {
        kafkaTemplate.send("notification-wishers", dto);
    }

    public Optional<UserDTO> findCategoriesByUserId(int id) {
        var mapper = new ObjectMapper();
        try {
            var url = String
                .format("%s/subscribeCategory/%d", uriProvider.getUri(SERVICE_ID), id);
            var text = restAuthCall.get(url);
            List<Integer> list = mapper.readValue(text, new TypeReference<>() {
            });
            return Optional.of(new UserDTO(id, list));
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<UserTopicDTO> findTopicByUserId(int id) {
        var mapper = new ObjectMapper();
        try {
            var url = String
                .format("%s/subscribeTopic/%d", uriProvider.getUri(SERVICE_ID), id);
            var text = restAuthCall.get(url);
            List<Integer> list = mapper.readValue(text, new TypeReference<>() {
            });
            return Optional.of(new UserTopicDTO(id, list));
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public List<InnerMessageDTO> findBotMessageByUserId(String token, int id) {
        var url = String
            .format("%s/messages/actual/%d", uriProvider.getUri(SERVICE_ID), id);
        var mapper = new ObjectMapper();
        try {
            var text = restAuthCall.get(url, token);
            return mapper.readValue(text, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("API notification not found, error: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}