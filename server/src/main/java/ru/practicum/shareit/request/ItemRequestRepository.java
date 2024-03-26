package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("select ir from ItemRequest ir where ir.requestor.id  = :userId order by ir.created desc ")
    List<ItemRequest> findAllRequestWithItemsByUserId(@Param("userId") Long userId);

    @Query("select ir from ItemRequest ir where ir.requestor.id  != :userId order by ir.created desc ")
    Page<ItemRequest> findAllRequestWithItemsByNotUserId(@Param("userId") Long userId, Pageable pageable);

}