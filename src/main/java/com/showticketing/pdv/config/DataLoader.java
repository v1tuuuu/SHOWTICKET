package com.showticketing.pdv.config;

import com.showticketing.pdv.entity.Batch;
import com.showticketing.pdv.entity.Event;
import com.showticketing.pdv.entity.Sector;
import com.showticketing.pdv.enums.EventStatus;
import com.showticketing.pdv.repository.EventRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class DataLoader implements CommandLineRunner {

    private final EventRepository eventRepository;

    public DataLoader(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void run(String... args) {
        if (eventRepository.count() > 0) {
            return;
        }

        // Show 1: Rock Sessions
        Event rockEvent = new Event(
                "evt_rock2026",
                "Festival Rock Sessions 2026",
                "Titãs & Convidados",
                "Espaço Cultural Hall",
                "Curitiba",
                "PR",
                "Rua das Flores, 1500",
                LocalDateTime.now().plusDays(20).withHour(20).withMinute(0),
                LocalDateTime.now().plusDays(20).withHour(18).withMinute(0),
                16,
                EventStatus.ACTIVE
        );

        Sector pista = new Sector("sec_pista", "Pista Comum", 3000, 850);
        Batch batchPista1 = new Batch("bat_pista_lote_1", 1, BigDecimal.valueOf(140.00), 500);
        batchPista1.setSoldQuantity(500);
        batchPista1.setActive(false);
        Batch batchPista2 = new Batch("bat_pista_lote_2", 2, BigDecimal.valueOf(180.00), 850);
        batchPista2.setSoldQuantity(0);
        batchPista2.setActive(true);
        pista.addBatch(batchPista1);
        pista.addBatch(batchPista2);

        Sector camarote = new Sector("sec_camarote", "Camarote Open Bar", 500, 42);
        Batch batchCamarote = new Batch("bat_camarote_lote_1", 1, BigDecimal.valueOf(380.00), 500);
        batchCamarote.setSoldQuantity(458);
        batchCamarote.setActive(true);
        camarote.addBatch(batchCamarote);

        Sector vip = new Sector("sec_vip", "Front Stage VIP", 400, 150);
        Batch batchVip = new Batch("bat_vip_lote_1", 1, BigDecimal.valueOf(280.00), 400);
        batchVip.setSoldQuantity(250);
        batchVip.setActive(true);
        vip.addBatch(batchVip);

        rockEvent.addSector(pista);
        rockEvent.addSector(camarote);
        rockEvent.addSector(vip);

        eventRepository.save(rockEvent);

        // Show 2: Pop Brasil Night
        Event popEvent = new Event(
                "evt_pop2026",
                "Pop Brasil Night Tour",
                "Anitta & Gloria Groove",
                "Arena Anhembi",
                "São Paulo",
                "SP",
                "Av. Olavo Fontoura, 1209",
                LocalDateTime.now().plusDays(35).withHour(21).withMinute(0),
                LocalDateTime.now().plusDays(35).withHour(19).withMinute(0),
                18,
                EventStatus.ACTIVE
        );

        Sector pistaPremium = new Sector("sec_pista_prem", "Pista Premium Gold", 2000, 1200);
        Batch batchPop1 = new Batch("bat_pop_prem_1", 1, BigDecimal.valueOf(240.00), 2000);
        batchPop1.setSoldQuantity(800);
        batchPop1.setActive(true);
        pistaPremium.addBatch(batchPop1);

        Sector cadeira = new Sector("sec_cadeira", "Cadeira Coberta Nível 1", 1500, 900);
        Batch batchPop2 = new Batch("bat_pop_cad_1", 1, BigDecimal.valueOf(160.00), 1500);
        batchPop2.setSoldQuantity(600);
        batchPop2.setActive(true);
        cadeira.addBatch(batchPop2);

        popEvent.addSector(pistaPremium);
        popEvent.addSector(cadeira);

        eventRepository.save(popEvent);
    }
}
