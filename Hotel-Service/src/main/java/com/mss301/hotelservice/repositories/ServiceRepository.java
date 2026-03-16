// src/main/java/com/mss301/hotelservice/repositories/ServiceRepository.java
// src/main/java/com/mss301/hotelservice/repositories/ServiceRepository.java
package com.mss301.hotelservice.repositories;

import com.mss301.hotelservice.pojos.HotelService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<HotelService, Long> {
    Optional<HotelService> findByCode(String code);
    List<HotelService> findByHotelId(Long hotelId);
    boolean existsByCode(String code);

    @Query("SELECT COUNT(s) > 0 FROM HotelService s WHERE s.hotelId = :hotelId AND LOWER(s.name) = LOWER(:name) AND s.id != :excludeId")
    boolean existsByNameInHotelExcludingId(@Param("hotelId") Long hotelId, @Param("name") String name, @Param("excludeId") Long excludeId);

    @Query("SELECT COUNT(s) > 0 FROM HotelService s WHERE s.hotelId = :hotelId AND LOWER(s.name) = LOWER(:name)")
    boolean existsByNameInHotel(@Param("hotelId") Long hotelId, @Param("name") String name);
}