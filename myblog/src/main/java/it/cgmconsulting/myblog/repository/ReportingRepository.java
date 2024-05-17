package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.Reporting;
import it.cgmconsulting.myblog.entity.ReportingId;
import it.cgmconsulting.myblog.entity.enumeration.ReportingStatus;
import it.cgmconsulting.myblog.payload.response.ReportingDetailResponse;
import it.cgmconsulting.myblog.payload.response.ReportingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReportingRepository extends JpaRepository<Reporting, ReportingId> {

    @Query(value = "SELECT new it.cgmconsulting.myblog.payload.response.ReportingResponse(" +
            "r.reportingId.commentId.id, " +
            "r.status, " +
            "r.reason.reasonId.reason, " +
            "r.createdAt, " +
            "r.updateAt" +
            ") FROM Reporting r " +
            "WHERE r.status = :status")
    Page<ReportingResponse> getReportings(ReportingStatus status, Pageable pageable);

    @Query(value = "SELECT new it.cgmconsulting.myblog.payload.response.ReportingDetailResponse(" +
            "r.reportingId.commentId.id, " +
            "r.status, " +
            "r.reason.reasonId.reason, " +
            "r.reportingId.commentId.comment, " +
            "r.reporter.username, " +
            "r.createdAt, " +
            "r.updateAt" +
            ") FROM Reporting r " +
            "WHERE r.reportingId.commentId.id = :commentId")
    ReportingDetailResponse getReportDetail(int commentId);

    /**
     *     private int commentId;
     *     private ReportingStatus status;
     *     private String reason;
     *     private String comment;
     *     private String reporter;
     *     private LocalDateTime createdAt;
     *     private LocalDateTime updateAt;
     */
}
