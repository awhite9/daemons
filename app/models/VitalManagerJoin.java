package models;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class VitalManagerJoin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="PATIENT_VITAL_ID")
    public Long patientVitalID;

    @Column(name = "VITAL_NAME")
    public String vitalName;

    @Column(name = "VALUE")
    public String value;

    @Column(name = "DATE_TAKEN")
    public LocalDate dateTaken;
}
