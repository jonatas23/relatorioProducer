package gov.goias.relatorios.producer.relatorio;

import gov.goias.relatorios.producer.producer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RelatorioQuartzJob implements Job {

    private final KafkaProducer producer;

    @Value("${kafka.topic}")
    private String topicRelatorio;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        var dataMap = context.getMergedJobDataMap();
        var nome = dataMap.getString("nome");
        var codgUsuario = dataMap.getString("codgUsuario");
        var agendarPara = dataMap.getString("agendarPara");
        log.info("[QUARTZ] Executando job para gerar relatório: {}", nome);

        producer.publicar(topicRelatorio, new RelatorioDTO(codgUsuario, nome, LocalDateTime.parse(agendarPara)));
    }
}