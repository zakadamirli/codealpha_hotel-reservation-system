package com.zekademirli.hostify.repository;

import com.zekademirli.hostify.entities.Property;
import com.zekademirli.hostify.enums.RoomCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    @EntityGraph(attributePaths = {"owner", "posts", "reservations"})
    Optional<Property> findDetailedById(Long id);


    @EntityGraph(attributePaths = {"posts", "reservations"})
    Page<Property> findAllByOwnerId(@Param("id") Long id, Pageable pageable);

    @EntityGraph(attributePaths = {"posts", "reservations"})
    Page<Property> findAllByCategory(RoomCategory category, Pageable pageable);


}
