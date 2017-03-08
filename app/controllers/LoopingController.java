package controllers;


import models.Prescription_Reminder;
import models.SuperJoin;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.time.LocalTime;
import java.util.List;

public class LoopingController extends Controller {

    private final FormFactory formFactory;
    private final JPAApi jpaApi;

    @Inject
    public LoopingController(FormFactory formFactory, JPAApi jpaApi) {
        this.formFactory = formFactory;
        this.jpaApi = jpaApi;
    }

    @Transactional
    public Result checkDB()
    {

        System.out.println("In the checkDB function");
            List<SuperJoin> superJoin = (List<SuperJoin>) jpaApi.em().createNativeQuery("select pr.REMINDER_ID, p.PATIENT_ID, p.first_name, p.cell_phone, pr.NEXT_REMINDER, f.frequency, pre.DOSAGE, m.NAME from patient p join prescription_reminder pr on p.PATIENT_ID = pr.PATIENT_ID join frequency f on pr.FREQUENCY_ID = f.FREQUENCY_ID join prescription pre on pr.PRESCRIPTION_ID = pre.PRESCRIPTION_ID join medication m on pre.MEDICATION_ID = m.MEDICATION_ID", SuperJoin.class).getResultList();

            for(SuperJoin loop : superJoin)
            {
                int r =LocalTime.now().compareTo(loop.nextReminder);
                System.out.println("int R from comparison function: "+r);
                if(r>0)
                {
                    System.out.println("Time has passed!! do something!!");
                    System.out.println("current reminder time: "+loop.nextReminder);
                    System.out.println("current time: "+LocalTime.now());
                    LocalTime newNextReminder = LocalTime.now().plusSeconds(loop.frequency);
                    System.out.println("New Reminder time: "+newNextReminder);


                    Long reminderID = loop.reminderID;
                    Prescription_Reminder prescriptionReminder = (Prescription_Reminder) jpaApi.em().createQuery("select pr from Prescription_Reminder pr where reminderID = :id").setParameter("id", reminderID).getSingleResult();
                    prescriptionReminder.nextReminder = newNextReminder;
                    jpaApi.em().persist(prescriptionReminder);



                    System.out.println("next Scheduled time after db update: "+prescriptionReminder.nextReminder);

                }
                else
                {
                    System.out.println("not time yet...");
                }
            }



        return redirect(routes.LoopingController.buttonPage());
    }

    public Result buttonPage()
    {
        return ok(views.html.button.render());
    }

}
