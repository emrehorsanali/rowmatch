package com.dreamgames.rowmatch.task;

import java.util.Date;

import com.dreamgames.rowmatch.service.TournamentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
    private static final Logger LOG = LoggerFactory.getLogger(ScheduledTasks.class);
    private final TournamentService tournamentService;

    public ScheduledTasks (final TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "${tournament.check-time}")
    public void endAndCreateTournament() {
        tournamentService.endAndCreateTournament();
        LOG.info("Method executed at every 20:00. Current time is = " + new Date());
    }
}