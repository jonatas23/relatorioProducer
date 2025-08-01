package gov.goias.relatorios.producer.relatorio;

import gov.goias.relatorios.producer.producer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final KafkaProducer producer;
    private final Scheduler scheduler;

    @Value("${kafka.topic}")
    private String topicRelatorio;

    public void processar(RelatorioDTO request) {
        if (request.agendarPara() == null) {
            log.info("[API] Publicando relatório imediatamente: {}", request.nome());
            producer.publicar(topicRelatorio, request);
        } else {
            agendar(request);
        }
    }

    private void agendar(RelatorioDTO request) {
        try {
            String jobId = UUID.randomUUID().toString();

            JobDetail jobDetail = JobBuilder.newJob(RelatorioQuartzJob.class)
                    .withIdentity(jobId, "relatorios")
                    .usingJobData("nome", request.nome())
                    .usingJobData("codgUsuario", request.codgUsuario())
                    .usingJobData("agendarPara", request.agendarPara().toString())
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger-" + jobId, "relatorios")
                    .startAt(java.util.Date.from(request.agendarPara().atZone(ZoneId.systemDefault()).toInstant()))
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
            log.info("[QUARTZ] Relatório agendado para: {}", request.agendarPara());
        } catch (SchedulerException e) {
            log.error("Erro ao agendar relatório", e);
        }
    }
}