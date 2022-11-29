package org.ta4j.core.indicators;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.mocks.MockBarSeries;
import org.ta4j.core.num.Num;

public class StandardDeviationFilterTest extends AbstractIndicatorTest<Indicator<Num>, Num> {

  public StandardDeviationFilterTest(final Function<Number, Num> numFunction) {
    super(numFunction);
  }

  @Test
  public void filter_candles_by_standard_deviation() {
    final MockBarSeries data = new MockBarSeries(this.numFunction, values());

    final StandardDeviationFilter sdv = new StandardDeviationFilter(new ClosePriceIndicator(data), 10, 1);

    final List<Double> expCloseValues =
        Arrays.asList(20710.18, 20746.92, 20746.92, 20746.92, 20746.92, 20746.92, 20746.92, 20786.28, 20862.3, 20786.98, 20786.98, 20849.19,
            20849.19, 20789.94, 20789.94, 20789.94, 20789.94, 20742.78, 20742.78, 20742.78, 20742.78, 20742.78, 20742.78, 20770.0, 20770.0,
            20770.0, 20683.62, 20683.62, 20683.62, 20722.68, 20722.68, 20722.68, 20791.8, 20791.8, 20791.8, 20791.8);
    final List<Num> expNum = expCloseValues.stream().map(this::numOf).collect(Collectors.toList());
    Assert.assertEquals(expNum, sdv.getValue());
  }

  @Test
  public void return_same_candles_values_when_filter_is_zero() {
    final MockBarSeries data = new MockBarSeries(this.numFunction, values());

    final StandardDeviationFilter sdv = new StandardDeviationFilter(new ClosePriceIndicator(data), 10, 0);

    final List<Double> expCloseValues =
        Arrays.asList(20710.18, 20746.92, 20746.06, 20725.53, 20743.32, 20751.96, 20740.68, 20786.28, 20862.3, 20786.98, 20818.12, 20849.19,
            20827.58, 20789.94, 20769.26, 20804.46, 20762.12, 20742.78, 20755.27, 20771.28, 20722.67, 20735.79, 20724.76, 20770.0, 20769.57,
            20777.36, 20683.62, 20712.48, 20678.89, 20722.68, 20754.35, 20747.84, 20791.8, 20781.86, 20803.98, 20827.83);
    final List<Num> expNum = expCloseValues.stream().map(this::numOf).collect(Collectors.toList());
    Assert.assertEquals(expNum, sdv.getValue());
  }

  /**
   * Precios de BTC desde 2022-10-27 07:00:00 hasta 2022-10-26 20:00:00 en tramos de 15 minutos.
   */
  private List<Double> values() {
    return Arrays.asList(20816.23, 20759.79, 20715.83, 20695.73, 20697.36, 20736.06, 20771.38, 20761.61, 20771.15, 20710.18, 20746.92,
        20746.06, 20725.53, 20743.32, 20751.96, 20740.68, 20786.28, 20862.3, 20786.98, 20818.12, 20849.19, 20827.58, 20789.94, 20769.26,
        20804.46, 20762.12, 20742.78, 20755.27, 20771.28, 20722.67, 20735.79, 20724.76, 20770.0, 20769.57, 20777.36, 20683.62, 20712.48,
        20678.89, 20722.68, 20754.35, 20747.84, 20791.8, 20781.86, 20803.98, 20827.83);
  }
}