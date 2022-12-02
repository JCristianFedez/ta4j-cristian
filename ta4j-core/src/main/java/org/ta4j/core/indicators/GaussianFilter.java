package org.ta4j.core.indicators;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

public class GaussianFilter extends RecursiveCachedIndicator<List<Num>> {

  private final int cyclePeriod;

  private final int poles;

  private final Indicator<Num> indicator;

  private GaussianFilter(final Indicator<Num> indicator, final int cyclePeriod, final int poles) {
    super(indicator);
    this.cyclePeriod = cyclePeriod;
    this.poles = poles;
    this.indicator = indicator;
  }

  public static List<Num> of(final Indicator<Num> indicator) {
    return new GaussianFilter(indicator, 25, 5).calculate(0);
  }

  private static List<Bar> buildBar(final Stream<Double> values) {
    final ZonedDateTime time = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault());

    return values.map(close -> new BaseBar(Duration.ofMinutes(15), time, 0, 0, 0, close, 0))
        .collect(Collectors.toList());
  }

  @Override
  protected List<Num> calculate(final int index) {
    final Stream<Double> ema1 = new EMAIndicator(this.indicator, 3).stream().map(Num::doubleValue);
    final Stream<Double> ema2 = new EMAIndicator(new ClosePriceIndicator(buildBarSerie(ema1)), 3).stream().map(Num::doubleValue);
    final Stream<Double> ema3 = new EMAIndicator(new ClosePriceIndicator(buildBarSerie(ema2)), 3).stream().map(Num::doubleValue);
    final Stream<Double> ema4 = new EMAIndicator(new ClosePriceIndicator(buildBarSerie(ema3)), 3).stream().map(Num::doubleValue);
    return new EMAIndicator(new ClosePriceIndicator(buildBarSerie(ema4)), 4).stream().collect(Collectors.toList());
  }

  private BarSeries buildBarSerie(final Stream<Double> values) {

    return new BaseBarSeriesBuilder()
        .withBars(buildBar(values))
        .build();
  }
}
