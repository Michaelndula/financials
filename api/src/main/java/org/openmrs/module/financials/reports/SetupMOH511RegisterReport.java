package org.openmrs.module.financials.reports;

import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.financials.reporting.library.dataset.CommonDatasetDefinition;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractHybridReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.kenyaemr.reporting.calculation.converter.GenderConverter;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.financials.reports.SetupMalariaReport.DATE_FORMAT;

@Component
@Builds({ "financials.common.report.511" })
public class SetupMOH511RegisterReport extends AbstractHybridReportBuilder {
	
	private CommonDatasetDefinition commonDatasetDefinition;
	
	@Autowired
	public SetupMOH511RegisterReport(CommonDatasetDefinition commonDatasetDefinition) {
		this.commonDatasetDefinition = commonDatasetDefinition;
	}
	
	@Override
	protected Mapped<CohortDefinition> buildCohort(HybridReportDescriptor hybridReportDescriptor,
	        PatientDataSetDefinition patientDataSetDefinition) {
		return null;
	}
	
	@Override
	protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {
		PatientDataSetDefinition dsd = getCwcRegister();
		dsd.setName("cwcr");
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		report.setBaseCohortDefinition(all511PatientsCohort());
		
		return Arrays.asList(ReportUtils.map((DataSetDefinition) dsd, "startDate=${startDate},endDate=${endDate}"));
	}
	
	@Override
	protected List<Parameter> getParameters(ReportDescriptor reportDescriptor) {
		return Arrays.asList(new Parameter("startDate", "Start Date", Date.class), new Parameter("endDate", "End Date",
		        Date.class), new Parameter("dateBasedReporting", "", String.class));
	}
	
	private PatientDataSetDefinition getCwcRegister() {
		PatientDataSetDefinition dsd = new PatientDataSetDefinition();
		dsd.setName("cwcr");
		DataConverter nameFormatter = new ObjectFormatter("{familyName}, {givenName}, {middleName}");
		DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
		dsd.addColumn("id", new PersonIdDataDefinition(), "");
		dsd.addColumn("Name", nameDef, "");
		dsd.addColumn("Sex", new GenderDataDefinition(), "", new GenderConverter());
		dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), "", new BirthdateConverter(DATE_FORMAT));
		return dsd;
		
	}
	
	private Mapped<CohortDefinition> all511PatientsCohort() {
		Program program1 = Context.getProgramWorkflowService().getProgramByUuid("645d7e4c-fbdb-11ea-911a-5fe00fc87a47");
		Program program2 = Context.getProgramWorkflowService().getProgramByUuid("c2ecdf11-97cd-432a-a971-cfd9bd296b83");
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setName("Admitted patients");
		cd.setQuery("SELECT p.patient_id FROM patient p INNER JOIN person pe ON p.patient_id=pe.person_id  "
		        + " INNER JOIN patient_program pp ON p.patient_id=pp.patient_id  INNER JOIN program pr ON pr.program_id=pp.program_id "
		        + " WHERE TIMESTAMPDIFF(YEAR, pe.birthdate, :endDate) < 5 AND pr.program_id IN(" + program1.getProgramId()
		        + "," + program2.getProgramId() + ")");
		return ReportUtils.map((CohortDefinition) cd, "startDate=${startDate},endDate=${endDate}");
	}
}
