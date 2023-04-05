package com.voc.raceEntry.event;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM" + " Event e WHERE e.visible = 1")
    List<Event> openEvents();
}
