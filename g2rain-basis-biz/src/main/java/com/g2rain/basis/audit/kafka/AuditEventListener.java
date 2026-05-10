package com.g2rain.basis.audit.kafka;

import com.g2rain.basis.dto.AuditEventDto;
import com.g2rain.basis.service.AuditEventService;
import com.g2rain.common.json.JsonCodec;
import com.g2rain.common.json.JsonCodecFactory;
import com.g2rain.common.utils.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 消费网关侧审计主题消息，将报文反序列化为 {@link AuditEventDto} 后写入审计流水。
 */
@Slf4j
@Component
@EnableKafka
@RequiredArgsConstructor
@ConditionalOnProperty(name = "g2rain.audit.event.enabled", havingValue = "true")
public class AuditEventListener {
    private static final JsonCodec JSON = JsonCodecFactory.instance();

    private final AuditEventService auditEventService;

    @KafkaListener(
        topics = "${g2rain.audit.event.topic:gateway.exchange.event}",
        groupId = "${g2rain.audit.event.group-id:g2rain-basis-audit}"
    )
    public void onGatewayExchange(String payload) {
        if (Strings.isBlank(payload)) {
            return;
        }
        try {
            AuditEventDto dto = JSON.str2obj(payload, AuditEventDto.class);
            if (Objects.isNull(dto)) {
                return;
            }

            auditEventService.save(dto);
        } catch (Exception e) {
            log.warn("消费网关审计消息失败且跳过本条: {}", e.getMessage(), e);
        }
    }
}
