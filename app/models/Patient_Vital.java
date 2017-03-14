package models;


import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Patient_Vital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="PATIENT_VITAL_ID")
    public Long patientVitalID;

    @Column(name = "PATIENT_ID")
    public Long patientID;

    @Column(name = "VITAL_ID")
    public Long vitalID;

    @Column(name = "VALUE")
    public String value;

    @Column(name = "DATE_TAKEN")
    public LocalDate dateTaken;
}
