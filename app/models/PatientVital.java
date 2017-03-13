package models;


import javax.persistence.*;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class PatientVital {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PATIENT_VITAL_ID")
    public Long patientVitalID;

    @Column(name = "VITAL_NAME")
    public String vitalName;

    @Column(name = "VALUE")
    public String value;

    @Column(name = "DATE_TAKEN")
    public java.sql.Date dateTaken;



}
