package com.voc.raceEntry.dao;

import com.voc.raceEntry.Entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long> {
}
