package controllers;



import models.*;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Result;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

public class MedicalHistoryController {

    private final FormFactory formFactory;
    private final JPAApi jpaApi;

    @Inject
    public MedicalHistoryController(FormFactory formFactory, JPAApi jpaApi) {
        this.formFactory = formFactory;
        this.jpaApi = jpaApi;
    }

    @Transactional(readOnly = true)
    public Result medicalHistoryManager()
    {
        List<MedicalHistoryManager> currentMedicalHistory = (List<MedicalHistoryManager>) jpaApi.em().createNativeQuery("select mh.medical_history_id, mh.date_diagnosed, mh.date_resolved, mh.patient_id, mh.medical_condition_id, mc.mc_name from medical_history mh\n" +
                "join medical_condition mc on mh.MEDICAL_CONDITION_ID = mc.MEDICAL_CONDITION_ID\n" +
                "where mh.DATE_RESOLVED is null", MedicalHistoryManager.class).getResultList();

        List<MedicalHistoryManager> pastMedicalHistory = (List<MedicalHistoryManager>) jpaApi.em().createNativeQuery("select mh.medical_history_id, mh.date_diagnosed, mh.date_resolved, mh.patient_id, mh.medical_condition_id, mc.mc_name from medical_history mh\n" +
                "join medical_condition mc on mh.MEDICAL_CONDITION_ID = mc.MEDICAL_CONDITION_ID\n" +
                "where mh.DATE_RESOLVED is not null", MedicalHistoryManager.class).getResultList();

        return ok(views.html.medicalHistoryManager.render(currentMedicalHistory, pastMedicalHistory));

    }

    @Transactional(readOnly = true)
    public Result newMedicalHistory()
    {
        List<Patient> patientList = (List<Patient>) jpaApi.em().createNativeQuery("select p.PATIENT_ID, p.FIRST_NAME from Patient p", Patient.class).getResultList();

        List<Medical_Condition> medCondition = (List<Medical_Condition>) jpaApi.em().createQuery("select mc from Medical_Condition mc").getResultList();

        return ok(views.html.newMedicalHistory.render(patientList, medCondition));
    }

    @Transactional
    public Result addMedicalHistory()
    {
        Medical_History newCurrent = formFactory.form(Medical_History.class).bindFromRequest().get();
        newCurrent.dateDiagnosed = LocalDate.now();
        newCurrent.dateResolved = null;
        jpaApi.em().persist(newCurrent);
        return redirect(routes.MedicalHistoryController.medicalHistoryManager());
    }

    @Transactional
    public Result resolveCondition(Long medicalHistoryID)
    {
        Medical_History current = (Medical_History) jpaApi.em().createQuery("select mh from Medical_History mh where mh.medicalHistoryID = :Id").setParameter("Id", medicalHistoryID).getSingleResult();
        current.dateResolved = LocalDate.now();
        jpaApi.em().persist(current);
        return redirect(routes.MedicalHistoryController.medicalHistoryManager());
    }

    @Transactional
    public Result deleteCondition(Long medicalHistoryID)
    {
        Medical_History condition = (Medical_History) jpaApi.em().createQuery("select mh from Medical_History mh where mh.medicalHistoryID = :Id").setParameter("Id", medicalHistoryID).getSingleResult();
        jpaApi.em().remove(condition);
        return redirect(routes.MedicalHistoryController.medicalHistoryManager());
    }
}
