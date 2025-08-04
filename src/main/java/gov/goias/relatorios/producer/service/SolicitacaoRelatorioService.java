// br.gov.go.financeiro.ipof.service.SolicitacaoRelatorioService
package gov.goias.relatorios.producer.service;

import gov.goias.relatorios.producer.dto.RelatorioDTO;
import gov.goias.relatorios.producer.dto.RelatorioStatusResponse;
import gov.goias.relatorios.producer.dto.SolicitacaoRelatorioRequest;
import gov.goias.relatorios.producer.dto.SolicitacaoRelatorioResponse;
import gov.goias.relatorios.producer.entity.SolicitacaoRelatorio;
import gov.goias.relatorios.producer.enuns.StatusRelatorio;
import gov.goias.relatorios.producer.enuns.TipoRelatorio;
import gov.goias.relatorios.producer.job.RelatorioQuartzJob;
import gov.goias.relatorios.producer.repository.SolicitacaoRelatorioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class SolicitacaoRelatorioService {

    private final SolicitacaoRelatorioRepository repository;
    private final KafkaProducerService kafkaProducer;
    private final Scheduler scheduler;

    public SolicitacaoRelatorioResponse criarSolicitacao(SolicitacaoRelatorioRequest request) {
        var solicitacao = SolicitacaoRelatorio.builder()
                .tipoRelatorio(TipoRelatorio.valueOf(request.tipoRelatorio()))
                .usuario(request.usuario())
                .sistema(request.sistema())
                .agendarPara(request.agendarPara())
                .dataSolicitacao(LocalDateTime.now())
                .status(StatusRelatorio.AGENDADO)
                .build();

        solicitacao.gerarIdSolicitacao();
        repository.save(solicitacao);

        if (solicitacao.getAgendarPara() == null) {
            kafkaProducer.enviarSolicitacaoRelatorio(solicitacao);
        } else {
            this.agendar(request);
        }

        return new SolicitacaoRelatorioResponse(
                solicitacao.getIdSolicitacao(),
                "Solicitação registrada com sucesso"
        );
    }

    @Transactional(readOnly = true)
    public Optional<RelatorioStatusResponse> buscarPorId(String idSolicitacao) {
        return repository.findById(idSolicitacao)
            .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<RelatorioStatusResponse> listarPorUsuario(String usuario) {
        return repository.findByUsuarioOrderByDataSolicitacaoDesc(usuario)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public Page<RelatorioStatusResponse> listarPorUsuarioComPaginacao(String usuario, Pageable pageable) {
        return repository.findByUsuarioOrderByDataSolicitacaoDesc(usuario, pageable)
            .map(this::toResponse);
    }

    public RelatorioStatusResponse atualizarStatus(
        String idSolicitacao,
        StatusRelatorio novoStatus,
        String mensagem,
        Integer progresso
    ) {
        var solicitacao = repository.findById(idSolicitacao)
            .orElseThrow(() -> new RuntimeException("Solicitação não encontrada: " + idSolicitacao));

        solicitacao.setStatus(novoStatus);
        solicitacao.setMensagemStatus(mensagem);
        if (progresso != null) solicitacao.setProgresso(progresso);

        // Atualiza timestamps com pattern matching (Java 21 style switch)
        switch (novoStatus) {
            case EM_EXECUCAO -> {
                if (solicitacao.getDataInicioExecucao() == null) {
                    solicitacao.setDataInicioExecucao(LocalDateTime.now());
                }
            }
            case CONCLUIDO, FALHA, CANCELADO -> {
                if (solicitacao.getDataConclusao() == null) {
                    solicitacao.setDataConclusao(LocalDateTime.now());
                }
            }
        }

        var atualizada = repository.save(solicitacao);
        kafkaProducer.enviarNotificacaoStatus(atualizada);

        return toResponse(atualizada);
    }

    private void agendar(SolicitacaoRelatorioRequest request) {
        try {
            String jobId = UUID.randomUUID().toString();

            JobDetail jobDetail = JobBuilder.newJob(RelatorioQuartzJob.class)
                    .withIdentity(jobId, "relatorios")
                    .usingJobData("tipoRelatorio", request.tipoRelatorio())
                    .usingJobData("usuario", request.usuario())
                    .usingJobData("sistema", request.sistema())
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

    public void cancelarSolicitacao(String idSolicitacao) {
        var solicitacao = repository.findById(idSolicitacao)
            .orElseThrow(() -> new RuntimeException("Solicitação não encontrada: " + idSolicitacao));

        if (Stream.of(StatusRelatorio.EM_EXECUCAO, StatusRelatorio.CONCLUIDO).anyMatch(solicitacao.getStatus()::equals)) {
            throw new RuntimeException(
                switch (solicitacao.getStatus()) {
                    case EM_EXECUCAO -> "Não é possível cancelar relatório em execução";
                    case CONCLUIDO -> "Não é possível cancelar relatório já concluído";
                    default -> "Operação não permitida";
                }
            );
        }

        atualizarStatus(idSolicitacao, StatusRelatorio.CANCELADO, "Cancelado pelo usuário", null);
    }

    public void excluirSolicitacao(String idSolicitacao) {
        var solicitacao = repository.findById(idSolicitacao)
            .orElseThrow(() -> new RuntimeException("Solicitação não encontrada: " + idSolicitacao));

        if (solicitacao.getStatus() == StatusRelatorio.EM_EXECUCAO) {
            throw new RuntimeException("Não é possível excluir relatório em execução");
        }

        repository.delete(solicitacao);
    }

    private RelatorioStatusResponse toResponse(SolicitacaoRelatorio s) {
        return new RelatorioStatusResponse(
            s.getIdSolicitacao(),
            s.getTipoRelatorio(),
            s.getUsuario(),
            s.getSistema(),
            s.getAgendarPara(),
            s.getStatus(),
            s.getDataSolicitacao(),
            s.getDataInicioExecucao(),
            s.getDataConclusao(),
            s.getProgresso(),
            s.getMensagemStatus(),
            s.getCaminhoArquivo(),
            s.getTamanhoArquivo()
        );
    }
}