package controllers;



import com.fasterxml.jackson.databind.ObjectMapper;
import models.*;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionManagerController extends Controller
{
    private final FormFactory formFactory;
    private final JPAApi jpaApi;

    @Inject
    public PrescriptionManagerController(FormFactory formFactory, JPAApi jpaApi) {
        this.formFactory = formFactory;
        this.jpaApi = jpaApi;
    }

    @Transactional(readOnly = true)
    public Result prescriptionManager()
    {
        List<PrescriptionManager> prescriptionManagerList = (List<PrescriptionManager>) jpaApi.em().createNativeQuery("select pre.PRESCRIPTION_ID, m.MEDICATION_ID, p.PATIENT_ID, p.FIRST_NAME, m.medication_name, pre.dosage, f.frequency, pre.frequency_id, d.doc_name, d.doctor_id, pharm.pharmacy_id, pharm.pharm_name, pre.date from patient p\n" +
                "join prescription pre on p.PATIENT_ID = pre.PATIENT_ID\n" +
                "join medication m on pre.MEDICATION_ID = m.MEDICATION_ID\n" +
                "join pharmacy pharm on pre.PHARMACY_ID = pharm.PHARMACY_ID\n" +
                "join doctor d on pre.DOCTOR_ID = d.DOCTOR_ID\n" +
                "join frequency f on pre.FREQUENCY_ID = f.FREQUENCY_ID", PrescriptionManager.class).getResultList();

        return ok(views.html.prescriptionManager.render(prescriptionManagerList));
    }

    @Transactional(readOnly = true)
    public Result newPrescription()
    {
        List<Patient> patientList = (List<Patient>) jpaApi.em().createNativeQuery("select p.PATIENT_ID, p.FIRST_NAME from Patient p", Patient.class).getResultList();

        List<Frequency> frequencyList = (List<Frequency>) jpaApi.em().createNativeQuery("select f.frequency_id, f.frequency from frequency f", Frequency.class).getResultList();

        List<MedicationName> medicationList = (List<MedicationName>) jpaApi.em().createNativeQuery("select m.medication_id, m.medication_name from medication m", MedicationName.class).getResultList();

        List<Doctor> doctorList = (List<Doctor>) jpaApi.em().createNativeQuery("select d.doctor_id, d.doc_name from doctor d", Doctor.class).getResultList();

        List<Pharmacy> pharmacyList = (List<Pharmacy>) jpaApi.em().createNativeQuery("select p.pharmacy_id, p.pharm_name from pharmacy p", Pharmacy.class).getResultList();

        return ok(views.html.newPrescription.render(patientList, frequencyList, medicationList, doctorList, pharmacyList));
    }

    @Transactional
    public Result addPrescription()
    {
        Prescription prescription = formFactory.form(Prescription.class).bindFromRequest().get();
        prescription.date = LocalDate.now();
        jpaApi.em().persist(prescription);
        return redirect(routes.PrescriptionManagerController.prescriptionManager());
    }

    @Transactional
    public Result deletePrescription(Long prescriptionID) {
        Prescription prescription = (Prescription) jpaApi.em().createQuery("select pre from Prescription pre where pre.prescriptionID = :Id").setParameter("Id", prescriptionID).getSingleResult();
        jpaApi.em().remove(prescription);
        return redirect(routes.PrescriptionManagerController.prescriptionManager());
    }

    @Transactional(readOnly = true)
    public Result editPrescription(Long prescriptionID)
    {
        PrescriptionManager currentPrescription = (PrescriptionManager) jpaApi.em().createNativeQuery("select pre.PRESCRIPTION_ID, m.MEDICATION_ID, p.PATIENT_ID, p.FIRST_NAME, m.medication_name, pre.dosage, f.frequency, pre.frequency_id, d.doc_name, d.doctor_id, pharm.pharmacy_id, pharm.pharm_name, pre.date from patient p\n" +
                "join prescription pre on p.PATIENT_ID = pre.PATIENT_ID\n" +
                "join medication m on pre.MEDICATION_ID = m.MEDICATION_ID\n" +
                "join pharmacy pharm on pre.PHARMACY_ID = pharm.PHARMACY_ID\n" +
                "join doctor d on pre.DOCTOR_ID = d.DOCTOR_ID\n" +
                "join frequency f on pre.FREQUENCY_ID = f.FREQUENCY_ID where pre.PRESCRIPTION_ID = :Id", PrescriptionManager.class).setParameter("Id", prescriptionID).getSingleResult();

        List<Patient> patientList = (List<Patient>) jpaApi.em().createNativeQuery("select p.PATIENT_ID, p.FIRST_NAME from Patient p", Patient.class).getResultList();

        List<Frequency> frequencyList = (List<Frequency>) jpaApi.em().createNativeQuery("select f.frequency_id, f.frequency from frequency f", Frequency.class).getResultList();

        List<MedicationName> medicationList = (List<MedicationName>) jpaApi.em().createNativeQuery("select m.medication_id, m.medication_name from medication m", MedicationName.class).getResultList();

        List<Doctor> doctorList = (List<Doctor>) jpaApi.em().createNativeQuery("select d.doctor_id, d.doc_name from doctor d", Doctor.class).getResultList();

        List<Pharmacy> pharmacyList = (List<Pharmacy>) jpaApi.em().createNativeQuery("select p.pharmacy_id, p.pharm_name from pharmacy p", Pharmacy.class).getResultList();

        return ok(views.html.editPrescription.render(currentPrescription, patientList, frequencyList, medicationList, doctorList, pharmacyList));
    }

    @Transactional
    public Result updatePrescription()
    {
        DynamicForm postedForm = formFactory.form().bindFromRequest();
        Long prescriptionID = new Long(postedForm.get("prescriptionID"));
        Long patientID = new Long(postedForm.get("patientID"));
        String dosage = postedForm.get("dosage");
        Long medicationID = new Long(postedForm.get("medicationID"));
        Long pharmacyID = new Long(postedForm.get("pharmacyID"));
        Long doctorID = new Long(postedForm.get("doctorID"));
        Long frequencyID = new Long(postedForm.get("frequencyID"));
        Prescription prescription = (Prescription) jpaApi.em().createQuery("select pre from Prescription pre where pre.prescriptionID = :Id").setParameter("Id", prescriptionID).getSingleResult();

        prescription.prescriptionID = prescriptionID;
        prescription.patientID = patientID;
        prescription.dosage = dosage;
        prescription.medicationID = medicationID;
        prescription.pharmacyID = pharmacyID;
        prescription.doctorID = doctorID;
        prescription.frequencyID = frequencyID;

        jpaApi.em().persist(prescription);
        return redirect(routes.PrescriptionManagerController.prescriptionManager());
    }




}
