package gov.goias.relatorios.producer.relatorio;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin("*")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    @PostMapping
    public ResponseEntity<String> gerar(@RequestBody RelatorioDTO request) {
        relatorioService.processar(request);
        return ResponseEntity.ok("Solicitação recebida");
    }
}
