package gov.goias.relatorios.producer.relatorio;

import java.time.LocalDateTime;

public record RelatorioDTO(String codgUsuario, String nome, LocalDateTime agendarPara) {}