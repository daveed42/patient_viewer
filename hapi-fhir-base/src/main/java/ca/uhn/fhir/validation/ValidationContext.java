package ca.uhn.fhir.validation;

/*
 * #%L
 * HAPI FHIR - Core Library
 * %%
 * Copyright (C) 2014 - 2015 University Health Network
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.instance.model.api.IBaseResource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;

class ValidationContext<T> {

	private final IEncoder myEncoder;
	private final FhirContext myFhirContext;
	private IBaseOperationOutcome myOperationOutcome;
	private final T myResource;
	private String myXmlEncodedResource;
private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(ValidationContext.class);
	private ValidationContext(FhirContext theContext, T theResource, IEncoder theEncoder) {
		myFhirContext = theContext;
		myResource = theResource;
		myEncoder = theEncoder;
	}

	public FhirContext getFhirContext() {
		return myFhirContext;
	}

	public IBaseOperationOutcome getOperationOutcome() {
		if (myOperationOutcome == null) {
			try {
				myOperationOutcome = (IBaseOperationOutcome) myFhirContext.getResourceDefinition("OperationOutcome").getImplementingClass().newInstance();
			} catch (Exception e1) {
				ourLog.error("Failed to instantiate OperationOutcome resource instance", e1);
				throw new InternalErrorException("Failed to instantiate OperationOutcome resource instance", e1);
			}
		}
		return myOperationOutcome;
	}

	public T getResource() {
		return myResource;
	}

	public String getXmlEncodedResource() {
		if (myXmlEncodedResource == null) {
			myXmlEncodedResource = myEncoder.encode();
		}
		return myXmlEncodedResource;
	}

	public static ValidationContext<Bundle> forBundle(final FhirContext theContext, final Bundle theBundle) {
		return new ValidationContext<Bundle>(theContext, theBundle, new IEncoder() {
			@Override
			public String encode() {
				return theContext.newXmlParser().encodeBundleToString(theBundle);
			}
		});
	}

	public static <T extends IBaseResource> ValidationContext<T> forResource(final FhirContext theContext, final T theResource) {
		return new ValidationContext<T>(theContext, theResource, new IEncoder() {
			@Override
			public String encode() {
				return theContext.newXmlParser().encodeResourceToString(theResource);
			}
		});
	}

	public static ValidationContext<IBaseResource> newChild(ValidationContext<Bundle> theContext, IBaseResource theResource) {
		ValidationContext<IBaseResource> retVal = forResource(theContext.getFhirContext(), theResource);
		retVal.myOperationOutcome = theContext.getOperationOutcome();
		return retVal;
	}

	private interface IEncoder {
		String encode();
	}

}
