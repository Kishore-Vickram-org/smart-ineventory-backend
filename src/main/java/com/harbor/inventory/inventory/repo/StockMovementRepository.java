package com.harbor.inventory.inventory.repo;

import com.harbor.inventory.inventory.domain.MovementType;
import com.harbor.inventory.inventory.domain.StockMovement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByItemIdOrderByOccurredAtDesc(Long itemId, Pageable pageable);
    List<StockMovement> findAllByOrderByOccurredAtDesc(Pageable pageable);

    @Modifying
    @Transactional
    @Query("delete from StockMovement m where m.item.id = :itemId")
    void deleteByItemId(@Param("itemId") long itemId);

        @Query("""
                        select m from StockMovement m
                        where (:itemId is null or m.item.id = :itemId)
                            and (:type is null or m.type = :type)
                            and (
                                :locationId is null
                                or (m.fromLocation is not null and m.fromLocation.id = :locationId)
                                or (m.toLocation is not null and m.toLocation.id = :locationId)
                            )
                        order by m.occurredAt desc
                        """)
        List<StockMovement> search(@Param("itemId") Long itemId,
                                                            @Param("type") MovementType type,
                                                            @Param("locationId") Long locationId,
                                                            Pageable pageable);
}
