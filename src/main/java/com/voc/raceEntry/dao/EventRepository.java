package com.voc.raceEntry.dao;


import com.voc.raceEntry.Entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM" + " Event e WHERE e.visible = 1")
    public List<Event> openEvents();
}
