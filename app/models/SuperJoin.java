package models;

import javax.persistence.*;
import java.time.LocalTime;


@Entity
public class SuperJoin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="REMINDER_ID")
    public Long reminderID;

    @Column(name = "PATIENT_ID")
    public Long patientID;


    @Column(name = "FIRST_NAME")
    public String firstName;

    @Column(name = "CELL_PHONE")
    public String cellPhone;


    @Column(name = "NEXT_REMINDER")
    public LocalTime nextReminder;

    @Column(name = "FREQUENCY")
    public int frequency;

    @Column(name = "DOSAGE")
    public String dosage;

    @Column(name = "NAME")
    public String name;


}
