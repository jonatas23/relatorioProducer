// br.gov.go.financeiro.ipof.dto.request.SolicitacaoRelatorioRequest
package gov.goias.relatorios.producer.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record SolicitacaoRelatorioRequest(
    @NotBlank(message = "Tipo de relatório é obrigatório")
    String tipoRelatorio,

    @NotBlank(message = "Usuário é obrigatório")
    String usuario,

    @NotBlank(message = "Sistema é obrigatório")
    String sistema,

    @Future(message = "Data de agendamento deve ser no futuro")
    LocalDateTime agendarPara
) {}