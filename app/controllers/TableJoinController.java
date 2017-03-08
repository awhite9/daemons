package controllers;

import models.PatientVital;
import models.SuperJoin;
import models.VitalJoin;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.persistence.Query;
import java.util.List;

import static play.libs.Json.toJson;

public class TableJoinController extends Controller {

    private final FormFactory formFactory;
    private final JPAApi jpaApi;

    @Inject
    public TableJoinController(FormFactory formFactory, JPAApi jpaApi) {
        this.formFactory = formFactory;
        this.jpaApi = jpaApi;
    }

        @Transactional(readOnly = true)
        public Result getSuperJoin ()
        {
            List<SuperJoin> superJoin = (List<SuperJoin>) jpaApi.em().createNativeQuery("select p.PATIENT_ID, p.first_name, p.cell_phone, pr.NEXT_REMINDER, f.frequency, pre.DOSAGE, m.NAME from patient p join prescription_reminder pr on p.PATIENT_ID = pr.PATIENT_ID join frequency f on pr.FREQUENCY_ID = f.FREQUENCY_ID join prescription pre on pr.PRESCRIPTION_ID = pre.PRESCRIPTION_ID join medication m on pre.MEDICATION_ID = m.MEDICATION_ID", SuperJoin.class).getResultList();

            return ok(toJson(superJoin));
        }



        @Transactional(readOnly = true)
        public Result getGoogleChart()
        {
            List<PatientVital> patientVitals = (List<PatientVital>) jpaApi.em().createNativeQuery(
                    "select pv.PATIENT_VITAL_ID, v.name, pv.value, pv.date_taken from patient p \n" +
                    "join patient_vital pv on p.PATIENT_ID = pv.PATIENT_ID \n" +
                    "join vitals v on pv.VITAL_ID = v.VITAL_ID where v.NAME = \'weight\'", PatientVital.class).getResultList();

            List<VitalJoin> vitalJoins = (List<VitalJoin>) jpaApi.em().createNativeQuery("select x.value val1, y.value val2,x.date_taken from (select pv.PATIENT_VITAL_ID, v.name, pv.value, pv.date_taken from patient p \n" +
                    "join patient_vital pv on p.PATIENT_ID = pv.PATIENT_ID \n" +
                    "join vitals v on pv.VITAL_ID = v.VITAL_ID where v.NAME = 'weight') x join \n" +
                    "(select lp.LAB_PULLED_ID, l.lab_name, lp.value, lp.date_taken from patient p \n" +
                    "join lab_pulled lp on p.PATIENT_ID = lp.PATIENT_ID \n" +
                    "join lab l on lp.LAB_ID = l.LAB_ID where l.lab_name = \"Total cholesterol\") y\n" +
                    "on x.date_taken = y.date_taken order by date_taken asc", VitalJoin.class).getResultList();


                System.out.println("google charts pulling from DB");

            return ok(views.html.googlecharts.render(patientVitals, vitalJoins));
        }

    @Transactional(readOnly = true)
    public Result getGoogleColumn()
    {

        List<VitalJoin> vitalJoins = (List<VitalJoin>) jpaApi.em().createNativeQuery("select x.value val1, y.value val2,x.date_taken from (select lp.LAB_PULLED_ID, l.lab_name, lp.value, lp.date_taken from patient p \n" +
                "join lab_pulled lp on p.PATIENT_ID = lp.PATIENT_ID\n" +
                "join lab l on lp.LAB_ID = l.LAB_ID where l.lab_name = \"Hemoglobin A1c\") x join\n" +
                "(select lp.LAB_PULLED_ID, l.lab_name, lp.value, lp.date_taken from patient p\n" +
                "join lab_pulled lp on p.PATIENT_ID = lp.PATIENT_ID\n" +
                "join lab l on lp.LAB_ID = l.LAB_ID where l.lab_name = \"Gucose\") y\n" +
                "on x.date_taken = y.date_taken\n" +
                "order by date_taken asc", VitalJoin.class).getResultList();


        System.out.println("google charts pulling from DB");

        return ok(views.html.columnchart.render(vitalJoins));
    }




    }

