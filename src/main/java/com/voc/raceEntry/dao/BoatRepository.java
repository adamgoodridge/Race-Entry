package com.voc.raceEntry.dao;

import com.voc.raceEntry.Entity.Boat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoatRepository extends JpaRepository<Boat, Long> {
}
