package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(" select it " +
            " from Item as it " +
            " join it.owner as u " +
            " where u.id = ?1 ")
    List<Item> findAllByOwner(Long userId);


    @Query(" select it " +
            " from Item as it " +
            " join it.owner as u " +
            " where it.available = true " +
            " and (" +
            "  lower(it.description) like concat('%', lower(?1), '%') " +
            "  or lower(it.name) like concat('%', lower(?1), '%') " +
            ") ")
    List<Item> findAllBySearch(String pattern, Long userId);
}