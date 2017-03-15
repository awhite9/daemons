package models;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class MedicalHistoryManager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="MEDICAL_HISTORY_ID")
    public Long medicalHistoryID;

    @Column(name = "DATE_DIAGNOSED")
    public LocalDate dateDiagnosed;

    @Column(name = "PATIENT_ID")
    public Long patientID;

    @Column(name = "DATE_RESOLVED")
    public LocalDate dateResolved;

    @Column(name = "MEDICAL_CONDITION_ID")
    public Long medicalConditionID;

    @Column(name = "MC_NAME")
    public String mcName;
}
