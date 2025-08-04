package gov.goias.relatorios.producer.job;

import gov.goias.relatorios.producer.entity.SolicitacaoRelatorio;
import gov.goias.relatorios.producer.enuns.TipoRelatorio;
import gov.goias.relatorios.producer.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RelatorioQuartzJob implements Job {

    private final KafkaProducerService kafkaProducer;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        var dataMap = context.getMergedJobDataMap();
        var tipoRelatorio = dataMap.getString("tipoRelatorio");
        var usuario = dataMap.getString("usuario");
        var sistema = dataMap.getString("sistema");
        var agendarPara = dataMap.getString("agendarPara");
        log.info("[QUARTZ] Executando job para gerar relatório: {}", tipoRelatorio);

        var solicitacao = SolicitacaoRelatorio.builder()
                .tipoRelatorio(TipoRelatorio.valueOf(tipoRelatorio))
                .usuario(usuario)
                .sistema(sistema)
                .agendarPara(LocalDateTime.parse(agendarPara))
                .build();

        kafkaProducer.enviarSolicitacaoRelatorio(solicitacao);
    }
}