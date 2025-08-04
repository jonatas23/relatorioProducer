package gov.goias.relatorios.producer.dto;

import gov.goias.relatorios.producer.enuns.StatusRelatorio;
import gov.goias.relatorios.producer.enuns.TipoRelatorio;

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