package controllers;


import models.*;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Result;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

public class VaccineManagerController {

    private final FormFactory formFactory;
    private final JPAApi jpaApi;

    @Inject
    public VaccineManagerController(FormFactory formFactory, JPAApi jpaApi) {
        this.formFactory = formFactory;
        this.jpaApi = jpaApi;
    }

    @Transactional(readOnly = true)
    public Result vaccinationManager()
    {
        List<VaccinationManager> vaccinationManagers = (List<VaccinationManager>) jpaApi.em().createNativeQuery("select v.booster_required, vg.vaccination_given_id, vg.date, vg.doctor_id, vg.patient_id, p.first_name, vg.vaccine_id, v.vaccine_name, d.doc_name from vaccination_given vg\n" +
                "join vaccination v on vg.VACCINE_ID = v.VACCINE_ID\n" +
                "join doctor d on vg.DOCTOR_ID = d.DOCTOR_ID\n" +
                "join patient p on vg.PATIENT_ID = p.PATIENT_ID",VaccinationManager.class).getResultList();

        return ok(views.html.vaccinationManager.render(vaccinationManagers));
    }

    @Transactional(readOnly = true)
    public Result newVaccination()
    {
        List<VaccinationManager> vaccinationManagers = (List<VaccinationManager>) jpaApi.em().createNativeQuery("select v.booster_required, vg.vaccination_given_id, vg.date, vg.doctor_id, vg.patient_id, p.first_name, vg.vaccine_id, v.vaccine_name, d.doc_name from vaccination_given vg\n" +
                "join vaccination v on vg.VACCINE_ID = v.VACCINE_ID\n" +
                "join doctor d on vg.DOCTOR_ID = d.DOCTOR_ID\n" +
                "join patient p on vg.PATIENT_ID = p.PATIENT_ID",VaccinationManager.class).getResultList();

        List<Vaccination> vaccination = (List<Vaccination>)jpaApi.em().createQuery("select v from Vaccination v").getResultList();

        List<Patient> patientList = (List<Patient>) jpaApi.em().createNativeQuery("select p.PATIENT_ID, p.FIRST_NAME from Patient p", Patient.class).getResultList();

        List<Doctor> doctorList = (List<Doctor>) jpaApi.em().createNativeQuery("select d.doctor_id, d.doc_name from doctor d", Doctor.class).getResultList();

        return ok(views.html.newVaccination.render(vaccinationManagers, vaccination, patientList, doctorList));
    }

    @Transactional
    public Result addVaccination()
    {
        Vaccination_Given vaccination = formFactory.form(Vaccination_Given.class).bindFromRequest().get();
        vaccination.date = LocalDate.now();
        jpaApi.em().persist(vaccination);
        return redirect(routes.VaccineManagerController.vaccinationManager());
    }

    @Transactional
    public Result deleteVaccination(Long vaccinationGivenID)
    {
        Vaccination_Given vaccination = (Vaccination_Given) jpaApi.em().createQuery("select vg from Vaccination_Given vg where vg.vaccinationGivenID = :Id").setParameter("Id", vaccinationGivenID).getSingleResult();
        jpaApi.em().remove(vaccination);
        return redirect(routes.VaccineManagerController.vaccinationManager());
    }

    @Transactional(readOnly = true)
    public Result editVaccination(Long vaccinationGivenID)
    {
        VaccinationManager vaccinationManager = (VaccinationManager)jpaApi.em().createNativeQuery("select v.booster_required, vg.vaccination_given_id, vg.date, vg.doctor_id, vg.patient_id, p.first_name, vg.vaccine_id, v.vaccine_name, d.doc_name from vaccination_given vg\n" +
                "join vaccination v on vg.VACCINE_ID = v.VACCINE_ID\n" +
                "join doctor d on vg.DOCTOR_ID = d.DOCTOR_ID\n" +
                "join patient p on vg.PATIENT_ID = p.PATIENT_ID where vg.vaccination_given_ID = :Id", VaccinationManager.class).setParameter("Id", vaccinationGivenID).getSingleResult();

        List<Vaccination> vaccination = (List<Vaccination>)jpaApi.em().createQuery("select v from Vaccination v").getResultList();

        List<Patient> patientList = (List<Patient>) jpaApi.em().createNativeQuery("select p.PATIENT_ID, p.FIRST_NAME from Patient p", Patient.class).getResultList();

        List<Doctor> doctorList = (List<Doctor>) jpaApi.em().createNativeQuery("select d.doctor_id, d.doc_name from doctor d", Doctor.class).getResultList();

        return ok(views.html.editVaccination.render(vaccinationManager, vaccination, patientList, doctorList));
    }

    @Transactional
    public Result updateVaccination()
    {
        DynamicForm postedForm = formFactory.form().bindFromRequest();
        Long vaccinationGivenID = new Long(postedForm.get("vaccinationGivenID"));
        Long patientID = new Long(postedForm.get("patientID"));
        Long vaccineID = new Long(postedForm.get("vaccineID"));
        Long doctorID = new Long(postedForm.get("doctorID"));

        Vaccination_Given vaccination = (Vaccination_Given) jpaApi.em().createQuery("select vg from Vaccination_Given vg where vg.vaccinationGivenID = :Id").setParameter("Id", vaccinationGivenID).getSingleResult();

        vaccination.patientID = patientID;
        vaccination.vaccineID = vaccineID;
        vaccination.doctorID = doctorID;

        jpaApi.em().persist(vaccination);
        return redirect(routes.VaccineManagerController.vaccinationManager());
    }
}
