package models;


import javax.persistence.*;

@Entity
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="DOCTOR_ID")
    public Long doctorID;

    @Column(name = "DOC_NAME")
    public String docName;
}
