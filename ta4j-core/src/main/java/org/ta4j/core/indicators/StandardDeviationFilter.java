package org.ta4j.core.indicators;

import java.util.ArrayList;
import java.util.List;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.Num;

/**
 * Standard deviation filter.
 *
 * @see <a href= "https://www.spcforexcel.com/knowledge/control-chart-basics/estimated-standard-deviation-and-control-charts">
 *     https://www.spcforexcel.com/knowledge/control-chart-basics/estimated-standard-deviation-and-control-charts</a>
 */
public class StandardDeviationFilter {

  private final CollectionCachedIndicator collectionCachedIndicator;

  private StandardDeviationFilter(final Indicator<Num> indicator, final int barCount, final int filter) {
    this.collectionCachedIndicator = new CollectionCachedIndicator(indicator, barCount, filter);
  }

  public static StandardDeviationFilter of(final Indicator<Num> indicator, final int barCount, final int filter) {
    return new StandardDeviationFilter(indicator, barCount, filter);
  }

  public static StandardDeviationFilter of(final Indicator<Num> indicator, final int barCount) {
    return new StandardDeviationFilter(indicator, barCount, 1);
  }

  public List<Num> getValue() {
    return collectionCachedIndicator.getValue(0);
  }

  private static final class CollectionCachedIndicator extends CachedIndicator<List<Num>> {

    private final int barCount;

    private final Indicator<Num> indicator;

    private final int filter;

    private Num prevValue;

    private CollectionCachedIndicator(final Indicator<Num> indicator, final int barCount, final int filter) {
      super(indicator);
      this.indicator = indicator;
      this.barCount = barCount;
      this.prevValue = numOf(Double.MIN_VALUE);
      this.filter = filter;
    }

    @Override
    protected List<Num> calculate(final int index) {
      final List<Num> values = new ArrayList<>();
      for (int i = 0; i <= getBarSeries().getBarCount() - this.barCount; i++) {
        final int toIndex = i + this.barCount;
        final BarSeries subSeries = this.indicator.getBarSeries().getSubSeries(i, toIndex);
        final Num value = standardDeviationFilterPerCandle(subSeries);
        values.add(value);
      }
      return values;
    }

    private Num standardDeviationFilterPerCandle(final BarSeries values) {
      final Num standardDeviation =
          new StandardDeviationIndicator(new ClosePriceIndicator(values), values.getBarCount()).getValue(values.getBarCount() - 1);
      final Num filterDeviation = standardDeviation.multipliedBy(numOf(this.filter));
      final Num subtract = values.getLastBar().getClosePrice().minus(this.prevValue);
      if (this.prevValue.doubleValue() == Double.MIN_VALUE) {
        this.prevValue = values.getLastBar().getClosePrice();
      }
      if (subtract.abs().isGreaterThanOrEqual(filterDeviation)) {
        this.prevValue = values.getLastBar().getClosePrice();
      }
      return this.prevValue;
    }
  }
}
