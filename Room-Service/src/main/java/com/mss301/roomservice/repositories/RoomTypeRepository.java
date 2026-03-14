package com.mss301.roomservice.repositories;

import com.mss301.roomservice.pojos.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {
    Optional<RoomType> findByCode(String code);
    List<RoomType> findByHotelId(Long hotelId);
    boolean existsByCode(String code);
}