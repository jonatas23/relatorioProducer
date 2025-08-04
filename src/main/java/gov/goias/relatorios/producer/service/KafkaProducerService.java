package gov.goias.relatorios.producer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.goias.relatorios.producer.entity.SolicitacaoRelatorio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.relatorio.notificacao}")
    private String topicNotificacao;

    @Value("${kafka.topic.relatorio.solicitacao}")
    private String topicSolicitacao;

    public void enviarSolicitacaoRelatorio(SolicitacaoRelatorio solicitacao) {
        try {
            Message<SolicitacaoRelatorio> message = MessageBuilder.withPayload(solicitacao)
                    .setHeader(KafkaHeaders.KEY, solicitacao.getIdSolicitacao())
                    .setHeader(KafkaHeaders.TOPIC, topicSolicitacao)
                    .build();

            CompletableFuture<SendResult<String, Object>> send =  kafkaTemplate.send(message);

            send.whenComplete((sendResult, throwable) -> {
                if (Objects.isNull(throwable)) {
                    log.info("Mensagem enviada com sucesso para o topico: {}, key {}", topicSolicitacao, solicitacao.getIdSolicitacao());
                } else {
                    log.error("Erro ao enviar Mensagempara o topico: {}, key {}", topicSolicitacao, solicitacao.getIdSolicitacao(), throwable);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar solicitação para Kafka", e);
        }
    }

    public void enviarNotificacaoStatus(SolicitacaoRelatorio solicitacao) {
        try {
            var notificacao = new NotificacaoStatus(
                solicitacao.getIdSolicitacao(),
                solicitacao.getUsuario(),
                solicitacao.getStatus().name(),
                solicitacao.getProgresso(),
                solicitacao.getMensagemStatus(),
                null
            );

            kafkaTemplate.send(topicNotificacao, solicitacao.getUsuario(), notificacao);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar notificação para Kafka", e);
        }
    }

    private record NotificacaoStatus(
        String reportId,
        String userId,
        String status,
        Integer progress,
        String message,
        String timestamp
    ) {
        public NotificacaoStatus {
            timestamp = java.time.LocalDateTime.now().toString();
        }
    }
}