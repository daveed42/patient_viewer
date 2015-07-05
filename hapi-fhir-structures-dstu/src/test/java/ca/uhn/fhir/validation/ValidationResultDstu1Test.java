package ca.uhn.fhir.validation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.base.resource.BaseOperationOutcome.BaseIssue;
import ca.uhn.fhir.model.dstu.resource.OperationOutcome;

import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ValidationResultDstu1Test {

	private static final FhirContext ourCtx = FhirContext.forDstu1();

	@Test
	public void isSuccessful_FalseForIssues() {
		OperationOutcome operationOutcome = new OperationOutcome();
		OperationOutcome.Issue issue = operationOutcome.addIssue();
		String errorMessage = "There was a validation problem";
		issue.setDetails(errorMessage);
		ValidationResult result = ValidationResult.valueOf(ourCtx, operationOutcome);
		assertFalse(result.isSuccessful());
		List<? extends BaseIssue> issues = ((OperationOutcome) result.getOperationOutcome()).getIssue();
		assertEquals(1, issues.size());
		assertEquals(errorMessage, issues.get(0).getDetailsElement().getValue());

		assertThat("ValidationResult#toString should contain the issue description", result.toString(), containsString(errorMessage));
	}

	@Test
	public void isSuccessful_IsTrueForNoIssues() {
		OperationOutcome operationOutcome = new OperationOutcome();
		// make sure a non-null ID doesn't cause the validation result to be a fail
		operationOutcome.setId(UUID.randomUUID().toString());
		ValidationResult result = ValidationResult.valueOf(ourCtx, operationOutcome);
		assertTrue(result.isSuccessful());
	}

	@Test
	public void isSuccessful_IsTrueForNullOperationOutcome() {
		ValidationResult result = ValidationResult.valueOf(ourCtx, null);
		assertTrue(result.isSuccessful());
	}

	/*
	 * Test for https://github.com/jamesagnew/hapi-fhir/issues/51
	 */
	@Test
	public void toString_ShouldNotCauseResultToBecomeFailure() {
		OperationOutcome operationOutcome = new OperationOutcome();
		ValidationResult result = ValidationResult.valueOf(ourCtx, operationOutcome);
		assertEquals(true, result.isSuccessful());
		// need to call toString to make sure any unwanted side effects are generated
		result.toString();
		assertEquals(true, result.isSuccessful());
	}
}
