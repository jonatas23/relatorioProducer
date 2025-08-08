package gov.goias.relatorios.producer.solicitacaoRelatorio.entity.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusRelatorio {
    EM_FILA("Em Fila"),
    AGENDADO("Agendado"),
    EM_EXECUCAO("Em Execução"),
    CONCLUIDO("Concluído"),
    FALHA("Falha"),
    CANCELADO("Cancelado");

    private final String descricao;
}