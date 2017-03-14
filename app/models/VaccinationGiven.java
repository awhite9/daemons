package models;


import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class VaccinationGiven {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="VACCINATION_GIVEN_ID")
    public Long vaccinationGivenID;

    @Column(name = "DATE")
    public LocalDate date;

    @Column(name = "DOCUMENTATION")
    public String documentation;

    @Column(name = "PATIENT_ID")
    public Long patientID;

    @Column(name = "VACCINE_ID")
    public Long vaccineID;

    @Column(name = "DOCTOR_ID")
    public Long doctorID;
}
