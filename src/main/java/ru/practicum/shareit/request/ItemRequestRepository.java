package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("select ir from ItemRequest ir where ir.requestor.id  = ?1 order by ir.created desc ")
    List<ItemRequest> findAllRequestWithItemsByUserId(Long userId);

    @Query("select ir from ItemRequest ir where ir.requestor.id  != ?1 order by ir.created desc ")
    Page<ItemRequest> findAllRequestWithItemsByNotUserId(Long userId, Pageable pageable);

}