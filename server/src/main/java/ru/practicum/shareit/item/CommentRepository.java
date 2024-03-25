package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(" select c from Comment c where c.item.id = :itemId ")
    List<Comment> findAllCommentsByItemId(@Param("itemId") Long itemId);
}
