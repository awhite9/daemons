package models;

import javax.persistence.*;
import java.sql.Time;

@Entity
public class SuperJoin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PATIENT_ID")
    public Long patientID;

    public String getFirstName()
    {
        return firstName;
    }

    @Column(name = "FIRST_NAME")
    public String firstName;

    @Column(name = "CELL_PHONE")
    public String cellPhone;

    public String getCellPhone()
    {
        return cellPhone;
    }

    @Column(name = "START_TIME")
    public Time startTime;


    @Column(name = "FREQUENCY")
    public Time frequency;

    @Column(name = "DOSAGE")
    public String dosage;

    @Column(name = "NAME")
    public String name;
}
