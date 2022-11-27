/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2022 Ta4j Organization & respective authors (see AUTHORS)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ta4j.core.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.FixedDecimalIndicator;
import org.ta4j.core.num.Num;

public class InPipeRuleTest {

  private InPipeRule rule;

  @Before
  public void setUp() {
    BarSeries series = new BaseBarSeries("I am empty");
    Indicator<Num> indicator = new FixedDecimalIndicator(series, 50d, 70d, 80d, 90d, 99d, 60d, 30d, 20d, 10d, 0d);
    rule = new InPipeRule(indicator, series.numOf(80), series.numOf(20));
  }

  @Test
  public void isSatisfied() {
    assertTrue(rule.isSatisfied(0));
    assertTrue(rule.isSatisfied(1));
    assertTrue(rule.isSatisfied(2));
    assertFalse(rule.isSatisfied(3));
    assertFalse(rule.isSatisfied(4));
    assertTrue(rule.isSatisfied(5));
    assertTrue(rule.isSatisfied(6));
    assertTrue(rule.isSatisfied(7));
    assertFalse(rule.isSatisfied(8));
    assertFalse(rule.isSatisfied(9));
  }
}
