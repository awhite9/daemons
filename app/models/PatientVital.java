package models;


import javax.persistence.*;
import java.util.Date;

@Entity
public class PatientVital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PATIENT_ID")
    public Long patientID;

    @Column(name = "NAME")
    public String name;

    @Column(name = "VALUE")
    public String value;

    @Column(name = "DATE_TAKEN")
    public Date dateTaken;
}
