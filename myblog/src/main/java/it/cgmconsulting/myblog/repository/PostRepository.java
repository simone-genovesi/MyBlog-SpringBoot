package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByPublicationDateIsNotNullAndPublicationDateBefore(LocalDate currentDate);
}
