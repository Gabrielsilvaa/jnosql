/*
 *
 *  Copyright (c) 2022 Contributors to the Eclipse Foundation
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 *
 */

package org.eclipse.jnosql.communication.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.YearMonth;

import org.eclipse.jnosql.communication.ValueReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class YearMonthTest {

	private ValueReader valueReader;

	@BeforeEach
	public void init() {
		valueReader = new YearMonthReader();
	}

	@Test
	public void shouldValidateCompatibility() {
		assertTrue(valueReader.test(YearMonth.class));
		assertFalse(valueReader.test(String.class));
		assertFalse(valueReader.test(Long.class));
	}

	@Test
	public void shouldConvert() {
		YearMonth yearMonth = YearMonth.parse("2016-08");

		assertEquals(yearMonth, valueReader.read(YearMonth.class, YearMonth.parse("2016-08")));
		assertEquals(yearMonth, valueReader.read(String.class,"2016-08"));
		assertEquals(yearMonth, valueReader.read(Integer.class,YearMonth.of(2016,8)));
		assertEquals(yearMonth, valueReader.read(Long.class,YearMonth.of(2016,8)));
	}

}
