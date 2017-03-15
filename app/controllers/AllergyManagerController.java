package controllers;


import models.Allergies;
import models.AllergyManager;
import models.Patient;
import models.Patient_Allergy;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

public class AllergyManagerController extends Controller {

    private final FormFactory formFactory;
    private final JPAApi jpaApi;

    @Inject
    public AllergyManagerController(FormFactory formFactory, JPAApi jpaApi) {
        this.formFactory = formFactory;
        this.jpaApi = jpaApi;
    }

    @Transactional(readOnly = true)
    public Result allergyManager()
    {
        List<AllergyManager> allergyManagerList = (List<AllergyManager>) jpaApi.em().createNativeQuery("select pa.PATIENT_ALLERGY_ID, pa.patient_id, pa.allergy_id, p.first_name, a.allergy_name from patient_allergy pa\n" +
                "join patient p on p.PATIENT_ID = pa.PATIENT_ID\n" +
                "join allergies a on pa.ALLERGY_ID = a.ALLERGY_ID", AllergyManager.class).getResultList();

        return ok(views.html.allergyManager.render(allergyManagerList));
    }

    @Transactional(readOnly = true)
    public Result newAllergy()
    {
        List<Allergies> allergy = (List<Allergies>) jpaApi.em().createNativeQuery("select a.allergy_ID, a.ALLERGY_NAME from Allergies a", Allergies.class).getResultList();

        List<Patient> patientList = (List<Patient>) jpaApi.em().createNativeQuery("select p.PATIENT_ID, p.FIRST_NAME from Patient p", Patient.class).getResultList();

        return ok(views.html.newAllergy.render(allergy, patientList));
    }

    @Transactional
    public Result addAllergy()
    {
        Patient_Allergy allergy = formFactory.form(Patient_Allergy.class).bindFromRequest().get();
        jpaApi.em().persist(allergy);
        return redirect(routes.AllergyManagerController.allergyManager());
    }

    @Transactional
    public Result deleteAllergy(Long patientAllergyID)
    {
        Patient_Allergy allergy = (Patient_Allergy) jpaApi.em().createQuery("select pa from Patient_Allergy pa where pa.patientAllergyID = :Id").setParameter("Id", patientAllergyID).getSingleResult();
        jpaApi.em().remove(allergy);
        return redirect(routes.AllergyManagerController.allergyManager());
    }
}
