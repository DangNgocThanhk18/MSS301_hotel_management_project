package com.mss301.roomservice.repositories;

import com.mss301.roomservice.pojos.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
}
