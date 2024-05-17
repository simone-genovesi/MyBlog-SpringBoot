package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Reason;
import it.cgmconsulting.myblog.entity.ReasonId;
import it.cgmconsulting.myblog.exception.ResourceNotFoundException;
import it.cgmconsulting.myblog.repository.ReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReasonService {

    private final ReasonRepository reasonRepository;


    public String addReason(String reason, LocalDate startDate, int severity) {
        // verificare che non esista già un'altra reason con quel nome
        if(reasonRepository.existsByReasonIdReasonAndEndDateIsNull(reason))
            return null;
        Reason r = new Reason(
                new ReasonId(reason, startDate),
                severity
        );
        reasonRepository.save(r);
        return "Reason " + reason + " successfully created";
    }

    public String deleteReason(String reason, LocalDate now){
        //verificare che ci sia una reason da invalidare, ovvero che abbia endDate a null
        List<Reason> validReasons = reasonRepository.getValidReason(reason, now);
        if(validReasons.isEmpty())
            return "No reason to invalidate";
        else if (validReasons.size() == 1) {
            validReasons.getFirst().setEndDate(now);
        }
        else {
            for (Reason r : validReasons){
                if (r.getEndDate() != null)
                    r.setEndDate(now);
                else
                    reasonRepository.delete(r);
            }
        }
        return "Reason " + reason + " invalidated";
    }

    public List<ReasonId> getValidReasons() {
        List<ReasonId> validReasons = reasonRepository.getValidReasons(LocalDate.now());
        List<ReasonId> newValidReasons = new ArrayList<>();
        for (ReasonId r : validReasons) {
            if(r.getStartDate().isBefore(LocalDate.now().plusDays(1)))
                newValidReasons.add(r);
        }
        return newValidReasons;
    }

    public Reason findReasonById(ReasonId reasonId) {
        return reasonRepository.findById(new ReasonId(reasonId.getReason(), reasonId.getStartDate())).orElseThrow(
                () -> new ResourceNotFoundException("Reason", "Id", new ReasonId(reasonId.getReason(), reasonId.getStartDate())));
    }

/*
INSULTI RAZZIALI        2023-01-01  null
LINGUAGGIO NON CONSONO  2024-01-01  2024-12-31
LINGUAGGIO NON CONSONO  2025-01-01  null
LINK                    2023-01-01  null
TROLLING                2024-01-01  null
*/
/*
INSULTI RAZZIALI        2023-01-01  null
LINGUAGGIO NON CONSONO  2024-01-01  2024-12-31
LINK                    2023-01-01  null
TROLLING                2024-01-01  null
*/
}
