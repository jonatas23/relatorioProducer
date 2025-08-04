package gov.goias.relatorios.producer.entity;

import gov.goias.relatorios.producer.enuns.StatusRelatorio;
import gov.goias.relatorios.producer.enuns.TipoRelatorio;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.*;

@Entity
@Table(name = "solicitacao_relatorio")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SolicitacaoRelatorio {

    @Id
    private String idSolicitacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoRelatorio tipoRelatorio;

    @Column(nullable = false)
    private String usuario;

    @Column(nullable = false)
    private String sistema;

    @Column(name = "agendar_para")
    private LocalDateTime agendarPara;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusRelatorio status;

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao;

    @Column(name = "data_inicio_execucao")
    private LocalDateTime dataInicioExecucao;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @Column(name = "progresso")
    private Integer progresso = 0;

    @Column(name = "mensagem_status")
    private String mensagemStatus;

    @Column(name = "caminho_arquivo")
    private String caminhoArquivo;

    @Column(name = "tamanho_arquivo")
    private Long tamanhoArquivo;

    public void gerarIdSolicitacao() {
        this.idSolicitacao = "rel-" + dataSolicitacao.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}