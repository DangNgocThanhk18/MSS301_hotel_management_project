package com.mss301.hotelservice.repositories;

import com.mss301.hotelservice.pojos.HotelAmenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelAmenityRepository extends JpaRepository<HotelAmenity, Long> {
    List<HotelAmenity> findByHotelId(Long hotelId);
}