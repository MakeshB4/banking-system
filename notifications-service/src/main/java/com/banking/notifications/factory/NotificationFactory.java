package com.banking.notifications.factory;

import com.banking.notifications.entity.NotificationType;
import com.banking.notifications.strategy.NotificationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class NotificationFactory {

    private final Map<NotificationType, NotificationStrategy> strategies;

    public NotificationFactory(List<NotificationStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        NotificationStrategy::getType,
                        Function.identity()
                ));
        
        log.info("NotificationFactory initialized with {} strategies", strategies.size());
        strategies.keySet().forEach(type -> 
            log.info("Registered strategy for type: {}", type)
        );
    }

    public NotificationStrategy getStrategy(NotificationType type) {
        NotificationStrategy strategy = strategies.get(type);
        
        if (strategy == null) {
            throw new IllegalArgumentException(
                "No notification strategy found for type: " + type
            );
        }
        
        return strategy;
    }
}