package gov.goias.relatorios.producer.dto;

import java.time.LocalDateTime;

public record RelatorioDTO(String codgUsuario, String nome, LocalDateTime agendarPara) {}