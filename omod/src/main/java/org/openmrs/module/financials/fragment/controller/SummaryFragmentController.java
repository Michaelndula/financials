package org.openmrs.module.financials.fragment.controller;

import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.financials.PatientBillSummary;
import org.openmrs.module.financials.utils.FinancialsUtils;
import org.openmrs.module.hospitalcore.HospitalCoreService;
import org.openmrs.module.hospitalcore.model.PatientServiceBillItem;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SummaryFragmentController {
	
	public void controller(FragmentModel model) {
		HospitalCoreService hospitalCoreService = Context.getService(HospitalCoreService.class);
		Date startOfDay = FinancialsUtils.getStartOfDay(new Date());
		Date endOfDay = FinancialsUtils.getEndOfDay(new Date());
		
		List<PatientServiceBillItem> getBilledItemsPerDepartment = hospitalCoreService.getPatientServiceBillByDepartment(
		    hospitalCoreService.getDepartmentByName("Registration"), startOfDay, endOfDay);
		
		List<PatientBillSummary> allRegistrationBills = new ArrayList<PatientBillSummary>();
		ConceptService conceptService = Context.getConceptService();
		double registrationFees = 0.0;
		double revisitFees = 0.0;
		double specialClinicFees = 0.0;
		for (PatientServiceBillItem patientServiceBillItem : getBilledItemsPerDepartment) {
			
			PatientBillSummary patientBillSummary = new PatientBillSummary();
			patientBillSummary.setBillId(patientServiceBillItem.getPatientServiceBillItemId());
			patientBillSummary.setPatient(patientServiceBillItem.getPatientServiceBill().getPatient().getPersonName()
			        .getFullName());
			patientBillSummary.setCategory(patientServiceBillItem.getPatientServiceBill().getPatientCategory());
			patientBillSummary.setSubCategory(patientServiceBillItem.getPatientServiceBill().getPatientSubCategory());
			patientBillSummary.setWaiver(String.valueOf(patientServiceBillItem.getPatientServiceBill().getWaiverAmount()));
			patientBillSummary.setActualAmount(String.valueOf(patientServiceBillItem.getActualAmount()));
			patientBillSummary.setPaidAmount(String.valueOf(patientServiceBillItem.getAmount()));
			patientBillSummary.setRebate(String.valueOf(patientServiceBillItem.getPatientServiceBill().getRebateAmount()));
			patientBillSummary.setTransactionDate(String.valueOf(patientServiceBillItem.getCreatedDate()));
			patientBillSummary.setServiceOffered(patientServiceBillItem.getService().getName());
			patientBillSummary.setIdentifier(patientServiceBillItem.getPatientServiceBill().getPatient()
			        .getPatientIdentifier().getIdentifier());
			//add this build object to the list
			allRegistrationBills.add(patientBillSummary);
			if (FinancialsUtils.registrationFeeConcepts().contains(
			    conceptService.getConcept(patientServiceBillItem.getService().getConceptId()))) {
				registrationFees += patientServiceBillItem.getAmount().doubleValue();
			}
			if (FinancialsUtils.revisitFeeConcepts().contains(
			    conceptService.getConcept(patientServiceBillItem.getService().getConceptId()))) {
				revisitFees += patientServiceBillItem.getAmount().doubleValue();
			}
			if (FinancialsUtils.specialClinicFeeConcepts().contains(
			    conceptService.getConcept(patientServiceBillItem.getService().getConceptId()))) {
				specialClinicFees += patientServiceBillItem.getAmount().doubleValue();
			}
		}
		
		model.addAttribute("bills", allRegistrationBills);
		model.addAttribute("registrationFees", registrationFees);
		model.addAttribute("revisitFees", revisitFees);
		model.addAttribute("specialClinicFees", specialClinicFees);
		
	}
}
