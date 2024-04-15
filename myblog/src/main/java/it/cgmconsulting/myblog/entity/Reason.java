package it.cgmconsulting.myblog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Reason {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private ReasonId reasonId;

    private LocalDate endDate;

    private int severity;

    public Reason(ReasonId reasonId, int severity) {
        this.reasonId = reasonId;
        this.severity = severity;
    }
}