package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.Reason;
import it.cgmconsulting.myblog.entity.ReasonId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReasonRepository extends JpaRepository<Reason, ReasonId> {

    boolean existsByReasonIdReasonAndEndDateIsNull(String reason);

    @Query(value = "SELECT r FROM Reason r " +
            "WHERE r.reasonId.reason = :reason " +
            "AND (r.endDate IS NULL " +
            "OR (:now BETWEEN r.reasonId.startDate AND r.endDate))")
    List<Reason> getValidReason(String reason, LocalDate now);

    @Query(value = "SELECT r.reasonId FROM Reason r " +
            "WHERE (r.endDate IS NULL " +
            "OR (:now BETWEEN r.reasonId.startDate AND r.endDate))")
    List<ReasonId> getValidReasons(LocalDate now);
}
