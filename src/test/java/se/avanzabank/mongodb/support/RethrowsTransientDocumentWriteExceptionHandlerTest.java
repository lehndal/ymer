/*
 * Copyright 2015 Avanza Bank AB
 *
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
 */
package se.avanzabank.mongodb.support;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import se.avanzabank.mongodb.support.mirror.DocumentWriteTransientException;

import com.mongodb.MongoException;
import com.mongodb.MongoException.Network;

/**
 * @author Kristoffer Erlandsson (krierl), kristoffer.erlandsson@avanzabank.se
 */
public class RethrowsTransientDocumentWriteExceptionHandlerTest {

	private DocumentWriteExceptionHandler handler = new RethrowsTransientDocumentWriteExceptionHandler();

	@Test(expected = DocumentWriteTransientException.class)
	public void throwsOnTransientException() throws Exception {
		handler.handleException(newMongoNetworkException(), "");
	}

	@Test
	public void doesNotThrowOnNonTransientException() throws Exception {
		try {
			handler.handleException(new RuntimeException(), "");
		} catch (RuntimeException e) {
			fail("Exception should not be thrown");
		}
	}

	@Test	
	public void throwsOnTransientExceptionByErrorMessage() throws Exception {		
		List<String> transientErrorMessages = Arrays.asList("No replica set members available for query with", "not master");
		for (String message : transientErrorMessages) {
			try {
				handler.handleException(new RuntimeException(message), "");
				fail("Exception should not be thrown for exception with message " + message);
			} catch (DocumentWriteTransientException e) {
			}
		}
	}

	private Network newMongoNetworkException() {
		return new MongoException.Network(new IOException());
	}

}
