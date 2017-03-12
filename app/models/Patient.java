package models;


import javax.persistence.*;

@Entity
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="PATIENT_ID")
    public Long patientID;

    @Column(name ="FIRST_NAME")
    public String firstName;


}
