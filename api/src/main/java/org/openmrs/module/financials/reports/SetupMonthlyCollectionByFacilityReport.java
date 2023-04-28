package org.openmrs.module.financials.reports;

import org.openmrs.module.financials.reporting.library.queries.MonthlyCollectionQueries;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Builds({ "financials.common.report.facility.monthly.collection" })
public class SetupMonthlyCollectionByFacilityReport extends AbstractHybridReportBuilder {
	
	@Override
	protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor hybridReportDescriptor,
	        PatientDataSetDefinition patientDataSetDefinition) {
		return null;
	}
	
	@Override
	protected List<Parameter> getParameters(ReportDescriptor reportDescriptor) {
		return Arrays.asList(new Parameter("startDate", "Start Date", Date.class), new Parameter("endDate", "End Date",
		        Date.class), new Parameter("dateBasedReporting", "", String.class));
	}
	
	@Override
	protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {
		SqlDataSetDefinition dsdM1 = new SqlDataSetDefinition();
		dsdM1.setName("M1");
		dsdM1.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsdM1.addParameter(new Parameter("endDate", "End Date", Date.class));
		dsdM1.setSqlQuery(MonthlyCollectionQueries.getMonthlySummaryQueryM1());
		
		SqlDataSetDefinition dsdM2 = new SqlDataSetDefinition();
		dsdM2.setName("M2");
		dsdM2.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsdM2.addParameter(new Parameter("endDate", "End Date", Date.class));
		dsdM2.setSqlQuery(MonthlyCollectionQueries.getMonthlySummaryQueryM2());
		
		return Arrays.asList(ReportUtils.map((DataSetDefinition) dsdM1, "startDate=${startDate},endDate=${endDate+23h}"),
		    ReportUtils.map((DataSetDefinition) dsdM2, "startDate=${startDate},endDate=${endDate+23h}"));
	}
	
}
