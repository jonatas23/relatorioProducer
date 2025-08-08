package gov.goias.relatorios.producer.solicitacaoRelatorio.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

public record SolicitacaoRelatorioRequest(
    @NotBlank(message = "Tipo de relatório é obrigatório")
    String tipoRelatorio,

    @NotBlank(message = "Usuário é obrigatório")
    String usuario,

    @NotBlank(message = "Sistema é obrigatório")
    String sistema,

    @Future(message = "Data de agendamento deve ser no futuro")
    Instant agendarPara
) {

    public LocalDateTime getAgendarParaLocal() {
        return Objects.isNull(agendarPara) ? null : agendarPara.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}