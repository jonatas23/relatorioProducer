package gov.goias.relatorios.producer.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoRelatorio {
    FOLHA_PAGAMENTO("Folha de Pagamento"),
    DESPESAS_ORCAMENTARIAS("Despesas Orçamentárias"),
    RECEITAS_TRIBUTARIAS("Receitas Tributárias"),
    BALANCO_PATRIMONIAL("Balanço Patrimonial"),
    DEMONSTRATIVO_RESULTADOS("Demonstrativo de Resultados");

    private final String descricao;
}