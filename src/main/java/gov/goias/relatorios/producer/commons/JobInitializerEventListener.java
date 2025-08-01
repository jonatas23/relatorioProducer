package gov.goias.relatorios.producer.commons;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobInitializerEventListener {

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        try {
            log.info("[JOB INITIALIZER] Aplicação iniciada - Carregando jobs de relatórios agendados...");
            this.initializeRelatorioJobs();
        } catch (Exception e) {
            log.error("[JOB INITIALIZER] Erro ao inicializar jobs de relatórios", e);
        }
    }

    private void initializeRelatorioJobs() throws SchedulerException {

    }

}