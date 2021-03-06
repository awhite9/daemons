package controllers;

import models.*;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.persistence.Query;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static play.libs.Json.toJson;

public class ReminderController extends Controller {

    private final FormFactory formFactory;
    private final JPAApi jpaApi;

    @Inject
    public ReminderController(FormFactory formFactory, JPAApi jpaApi) {
        this.formFactory = formFactory;
        this.jpaApi = jpaApi;
    }

        @Transactional(readOnly = true)
        public Result getSuperJoin ()
        {
            List<SuperJoin> superJoin = (List<SuperJoin>) jpaApi.em().createNativeQuery("select pr.REMINDER_ID, p.PATIENT_ID, p.first_name, p.cell_phone, pr.NEXT_REMINDER, f.frequency, pre.DOSAGE, m.MEDICATION_NAME, f.FREQUENCY_ID, pre.PRESCRIPTION_ID from patient p\n" +
                    "join prescription_reminder pr on p.PATIENT_ID = pr.PATIENT_ID\n" +
                    "join prescription pre on pr.PRESCRIPTION_ID = pre.PRESCRIPTION_ID\n" +
                    "join medication m on pre.MEDICATION_ID = m.MEDICATION_ID\n" +
                    "join frequency f on pre.FREQUENCY_ID = f.FREQUENCY_ID", SuperJoin.class).getResultList();

            return ok(toJson(superJoin));
        }



        @Transactional(readOnly = true)
        public Result getGoogleChart()
        {

            List<VitalJoin> vitalJoins = (List<VitalJoin>) jpaApi.em().createNativeQuery("select x.value val1, y.value val2,x.date_taken from (select pv.PATIENT_VITAL_ID, v.vital_name, pv.value, pv.date_taken from patient p \n" +
                    "join patient_vital pv on p.PATIENT_ID = pv.PATIENT_ID\n" +
                    "join vitals v on pv.VITAL_ID = v.VITAL_ID where v.vital_NAME = 'weight') x join \n" +
                    "(select lp.LAB_PULLED_ID, l.lab_name, lp.value, lp.date_taken from patient p\n" +
                    "join lab_pulled lp on p.PATIENT_ID = lp.PATIENT_ID\n" +
                    "join lab l on lp.LAB_ID = l.LAB_ID where l.lab_name = 'Total cholesterol') y\n" +
                    "on x.date_taken = y.date_taken order by date_taken asc", VitalJoin.class).getResultList();


                System.out.println("google charts pulling from DB");

            return ok(views.html.googlecharts.render(vitalJoins));
        }

    @Transactional(readOnly = true)
    public Result getGoogleColumn()
    {
        List<BarChart> barChart = new ArrayList<>();


        List<VitalJoin> vitalJoins = (List<VitalJoin>) jpaApi.em().createNativeQuery("select x.value val1, y.value val2,x.date_taken from (select lp.LAB_PULLED_ID, l.lab_name, lp.value, lp.date_taken from patient p \n" +
                "join lab_pulled lp on p.PATIENT_ID = lp.PATIENT_ID\n" +
                "join lab l on lp.LAB_ID = l.LAB_ID where l.lab_name = 'Hemoglobin A1c') x join\n" +
                "(select lp.LAB_PULLED_ID, l.lab_name, lp.value, lp.date_taken from patient p\n" +
                "join lab_pulled lp on p.PATIENT_ID = lp.PATIENT_ID\n" +
                "join lab l on lp.LAB_ID = l.LAB_ID where l.lab_name = 'Glucose') y\n" +
                "on x.date_taken = y.date_taken\n" +
                "order by date_taken asc", VitalJoin.class).getResultList();

        for(VitalJoin join: vitalJoins)
        {
            BarChart barchartItem = new BarChart();
            String hem = join.val1.replace("%", "");
            float hemoglobin = Float.parseFloat(hem);
            float glucose = Float.parseFloat(join.val2);
            LocalDate dateTaken = join.dateTaken;
            System.out.println("hemoglobin: "+hemoglobin+" glucose: "+glucose);
            System.out.println(dateTaken);

            barChart.add(barchartItem);
            barchartItem.setGlucose(glucose);
            barchartItem.setHemoglobin(hemoglobin);
            barchartItem.setDateTaken(dateTaken);

            System.out.println(barchartItem);





        }

        System.out.println("google charts pulling from DB");

        return ok(views.html.columnchart.render(vitalJoins, barChart));
    }

    @Transactional(readOnly = true)
    public Result getReminderPage()
    {
        List<SuperJoin> superJoin = (List<SuperJoin>) jpaApi.em().createNativeQuery("select pr.REMINDER_ID, p.PATIENT_ID, p.first_name, p.cell_phone, pr.NEXT_REMINDER, f.frequency, pre.DOSAGE, m.MEDICATION_NAME, f.FREQUENCY_ID, pre.PRESCRIPTION_ID from patient p\n" +
                "join prescription_reminder pr on p.PATIENT_ID = pr.PATIENT_ID\n" +
                "join prescription pre on pr.PRESCRIPTION_ID = pre.PRESCRIPTION_ID\n" +
                "join medication m on pre.MEDICATION_ID = m.MEDICATION_ID\n" +
                "join frequency f on pre.FREQUENCY_ID = f.FREQUENCY_ID", SuperJoin.class).getResultList();

        return ok(views.html.reminderPage.render(superJoin));

    }

    @Transactional
    public Result deleteReminder(Long reminderID)
    {
        Prescription_Reminder prescription_reminder = (Prescription_Reminder) jpaApi.em().createQuery("select pr from Prescription_Reminder pr where pr.reminderID = :Id").setParameter("Id", reminderID).getSingleResult();
        jpaApi.em().remove(prescription_reminder);
        return redirect(routes.ReminderController.getReminderPage());
    }

    @Transactional(readOnly = true)
    public Result newReminder()
    {

        List<Patient> patientList = (List<Patient>) jpaApi.em().createNativeQuery("select p.PATIENT_ID, p.FIRST_NAME from Patient p", Patient.class).getResultList();

        List<PrescriptionJoin> prescriptionJoinList = (List<PrescriptionJoin>) jpaApi.em().createNativeQuery("select p.prescription_id,  p.dosage, m.MEDICATION_NAME, f.frequency from prescription p\n" +
                "join medication m on m.MEDICATION_ID = p.MEDICATION_ID\n" +
                "join frequency f on f.frequency_ID = p.FREQUENCY_ID", PrescriptionJoin.class).getResultList();

        return ok(views.html.newReminder.render( patientList, prescriptionJoinList));
    }

    @Transactional
    public Result addReminder()
    {
        //date time picker
        Prescription_Reminder prescriptionReminder = formFactory.form(Prescription_Reminder.class).bindFromRequest().get();
        prescriptionReminder.nextReminder = LocalTime.now();
        jpaApi.em().persist(prescriptionReminder);
        return redirect(routes.ReminderController.getReminderPage());

    }

    @Transactional(readOnly = true)
    public Result editReminder(Long reminderID)
    {
        SuperJoin currentReminder = (SuperJoin) jpaApi.em().createNativeQuery("select pr.REMINDER_ID, p.PATIENT_ID, p.first_name, p.cell_phone, pr.NEXT_REMINDER, f.frequency, pre.DOSAGE, m.MEDICATION_NAME, f.FREQUENCY_ID, pre.PRESCRIPTION_ID from patient p\n" +
                "join prescription_reminder pr on p.PATIENT_ID = pr.PATIENT_ID\n" +
                "join prescription pre on pr.PRESCRIPTION_ID = pre.PRESCRIPTION_ID\n" +
                "join medication m on pre.MEDICATION_ID = m.MEDICATION_ID\n" +
                "join frequency f on pre.FREQUENCY_ID = f.FREQUENCY_ID where pr.REMINDER_ID = :Id", SuperJoin.class).setParameter("Id", reminderID).getSingleResult();




        List<Patient> patientList = (List<Patient>) jpaApi.em().createNativeQuery("select p.PATIENT_ID, p.FIRST_NAME from Patient p", Patient.class).getResultList();

        List<PrescriptionJoin> prescriptionJoinList = (List<PrescriptionJoin>) jpaApi.em().createNativeQuery("select p.prescription_id,  p.dosage, m.MEDICATION_NAME, f.frequency from prescription p\n" +
                "join medication m on m.MEDICATION_ID = p.MEDICATION_ID\n" +
                "join frequency f on f.frequency_ID = p.FREQUENCY_ID", PrescriptionJoin.class).getResultList();

        return ok(views.html.editReminder.render( patientList, prescriptionJoinList, currentReminder));
    }

    @Transactional
    public Result updateReminder()
    {
        DynamicForm postedForm = formFactory.form().bindFromRequest();
        Long reminderID = new Long(postedForm.get("reminderID"));
        Long patientID = new Long(postedForm.get("patientID"));
        Long prescriptionID = new Long(postedForm.get("prescriptionID"));
        Prescription_Reminder reminder = (Prescription_Reminder) jpaApi.em().createQuery("select pr from Prescription_Reminder pr where pr.reminderID = :Id").setParameter("Id", reminderID).getSingleResult();

        reminder.patientID = patientID;
        reminder.prescriptionID = prescriptionID;
        reminder.nextReminder = LocalTime.now();

        jpaApi.em().persist(reminder);
        return redirect(routes.ReminderController.getReminderPage());
    }






    }

