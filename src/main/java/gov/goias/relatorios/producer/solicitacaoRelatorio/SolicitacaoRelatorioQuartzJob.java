package gov.goias.relatorios.producer.solicitacaoRelatorio;

import gov.goias.relatorios.producer.solicitacaoRelatorio.entity.SolicitacaoRelatorio;
import gov.goias.relatorios.producer.solicitacaoRelatorio.entity.enuns.TipoRelatorio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class SolicitacaoRelatorioQuartzJob implements Job {

    private final SolicitacaoRelatorioProducerService kafkaProducer;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        var dataMap = context.getMergedJobDataMap();
        var dataSolicitacao = dataMap.getString("dataSolicitacao");
        var idSolicitacao = dataMap.getString("idSolicitacao");
        var tipoRelatorio = dataMap.getString("tipoRelatorio");
        var usuario = dataMap.getString("usuario");
        var sistema = dataMap.getString("sistema");
        var agendarPara = dataMap.getString("agendarPara");
        log.info("[QUARTZ] Executando job para gerar relatório: {}", tipoRelatorio);

        var solicitacao = SolicitacaoRelatorio.builder()
                .idSolicitacao(idSolicitacao)
                .tipoRelatorio(TipoRelatorio.valueOf(tipoRelatorio))
                .usuario(usuario)
                .sistema(sistema)
                .agendarPara(LocalDateTime.parse(agendarPara.replace("Z", "")))
                .dataSolicitacao(LocalDateTime.parse(dataSolicitacao.replace("Z", "")))
                .build();

        kafkaProducer.enviarSolicitacaoRelatorio(solicitacao);
    }
}