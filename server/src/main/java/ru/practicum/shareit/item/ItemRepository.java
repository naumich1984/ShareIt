package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(" select it from Item  it where it.request.id in :itemRequestIds ")
    List<Item> findAllByRequestIdList(@Param("itemRequestIds") List<Long> itemRequestIds);

    @Query(" select it " +
            " from Item as it " +
            " join it.owner as u " +
            " where u.id = :userId order by it.id ")
    List<Item> findAllByOwner(@Param("userId") Long userId);

    @Query(" select it " +
            " from Item as it " +
            " join it.owner as u " +
            " where it.available = true " +
            " and (" +
            "  lower(it.description) like concat('%', lower(:pattern), '%') " +
            "  or lower(it.name) like concat('%', lower(:pattern), '%') " +
            ") ")
    List<Item> findAllBySearch(@Param("pattern") String pattern);
}