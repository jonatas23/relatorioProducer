package gov.goias.relatorios.producer.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusRelatorio {
    AGENDADO("Agendado"),
    EM_EXECUCAO("Em Execução"),
    CONCLUIDO("Concluído"),
    FALHA("Falha"),
    CANCELADO("Cancelado");

    private final String descricao;
}