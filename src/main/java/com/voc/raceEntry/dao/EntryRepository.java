package com.voc.raceEntry.dao;

import com.voc.raceEntry.Entity.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntryRepository extends JpaRepository<Entry, Long> {

    @Query("SELECT e FROM Entry e WHERE e.event.eventId = ?1")
    public List<Entry> entriesByEvent(Long eventId);
}
