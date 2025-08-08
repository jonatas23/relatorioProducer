package gov.goias.relatorios.producer.solicitacaoRelatorio.dto;

import gov.goias.relatorios.producer.solicitacaoRelatorio.entity.enuns.StatusRelatorio;
import gov.goias.relatorios.producer.solicitacaoRelatorio.entity.enuns.TipoRelatorio;

import java.time.LocalDateTime;

public record RelatorioStatusResponse(
        String idSolicitacao,
        TipoRelatorio tipoRelatorio,
        String usuario,
        String sistema,
        LocalDateTime agendarPara,
        StatusRelatorio status,
        LocalDateTime dataSolicitacao,
        LocalDateTime dataInicioExecucao,
        LocalDateTime dataConclusao,
        Integer progresso,
        String mensagemStatus,
        String caminhoArquivo,
        Long tamanhoArquivo
) {
}