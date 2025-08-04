package gov.goias.relatorios.producer.controller;

import gov.goias.relatorios.producer.dto.RelatorioStatusResponse;
import gov.goias.relatorios.producer.dto.SolicitacaoRelatorioRequest;
import gov.goias.relatorios.producer.dto.SolicitacaoRelatorioResponse;
import gov.goias.relatorios.producer.enuns.StatusRelatorio;
import gov.goias.relatorios.producer.service.SolicitacaoRelatorioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin("*")
@RequiredArgsConstructor
public class RelatorioController {

    private final SolicitacaoRelatorioService service;

    @PostMapping
    public ResponseEntity<SolicitacaoRelatorioResponse> solicitarRelatorio(@Valid @RequestBody SolicitacaoRelatorioRequest request) {
        var response = service.criarSolicitacao(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{idSolicitacao}")
    public ResponseEntity<RelatorioStatusResponse> buscarRelatorio(@PathVariable String idSolicitacao) {
        return service.buscarPorId(idSolicitacao)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<RelatorioStatusResponse>> listarRelatorios(@RequestParam String usuario) {
        return ResponseEntity.ok(service.listarPorUsuario(usuario));
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<RelatorioStatusResponse>> listarRelatoriosPaginado(
            @RequestParam String usuario,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.listarPorUsuarioComPaginacao(usuario, pageable));
    }

    @PutMapping("/{idSolicitacao}/status")
    public ResponseEntity<RelatorioStatusResponse> atualizarStatus(
            @PathVariable String idSolicitacao,
            @RequestParam StatusRelatorio status,
            @RequestParam(required = false) String mensagem,
            @RequestParam(required = false) Integer progresso
    ) {
        try {
            var response = service.atualizarStatus(idSolicitacao, status, mensagem, progresso);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{idSolicitacao}/cancelar")
    public ResponseEntity<Void> cancelarSolicitacao(@PathVariable String idSolicitacao) {
        try {
            service.cancelarSolicitacao(idSolicitacao);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{idSolicitacao}")
    public ResponseEntity<Void> excluirSolicitacao(@PathVariable String idSolicitacao) {
        try {
            service.excluirSolicitacao(idSolicitacao);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}