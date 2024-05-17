package it.cgmconsulting.banner.repository;

import it.cgmconsulting.banner.entity.Counter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CounterRepository extends JpaRepository<Counter, Integer> {
}
