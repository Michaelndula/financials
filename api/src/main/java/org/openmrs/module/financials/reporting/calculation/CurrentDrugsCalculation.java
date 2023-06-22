package org.openmrs.module.financials.reporting.calculation;

import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.hospitalcore.PatientDashboardService;
import org.openmrs.module.hospitalcore.model.OpdDrugOrder;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CurrentDrugsCalculation extends AbstractPatientCalculation {
	
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> map,
	        PatientCalculationContext context) {
		
		CalculationResultMap ret = new CalculationResultMap();
		CalculationResultMap lastEncounter = Calculations.lastEncounter(Context.getEncounterService()
		        .getEncounterTypeByUuid("ba45c278-f290-11ea-9666-1b3e6e848887"), cohort, context);
		for (Integer pId : cohort) {
			StringBuilder drugs = new StringBuilder();
			
			Encounter lastOpdEncounter = EmrCalculationUtils.encounterResultForPatient(lastEncounter, pId);
			
			List<OpdDrugOrder> opdDrugs = Context.getService(PatientDashboardService.class)
			        .getOpdDrugOrder(lastOpdEncounter);
			for (OpdDrugOrder opdDrugOrder : opdDrugs) {
				if (opdDrugOrder != null && opdDrugOrder.getInventoryDrug() != null) {
					drugs.append(opdDrugOrder.getInventoryDrug().getName()).append(" ")
					        .append(opdDrugOrder.getInventoryDrugFormulation().getName()).append(" ")
					        .append(opdDrugOrder.getDosage()).append(" ")
					        .append(opdDrugOrder.getFrequency().getName().getName()).append("\n");
				}
			}
			ret.put(pId, new SimpleResult(drugs, this));
		}
		
		return ret;
	}
}
