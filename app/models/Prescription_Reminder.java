package models;

import javax.persistence.*;
import java.time.LocalTime;


@Entity
public class Prescription_Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="REMINDER_ID")
    public Long reminderID;

    @Column(name = "PATIENT_ID")
    public Long patientID;

    @Column(name = "PRESCRIPTION_ID")
    public Long prescriptionID;

    @Column(name = "NEXT_REMINDER")
    public LocalTime nextReminder;

}
