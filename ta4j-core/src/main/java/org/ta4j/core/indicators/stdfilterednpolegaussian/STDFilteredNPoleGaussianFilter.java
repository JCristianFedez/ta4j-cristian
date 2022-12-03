package org.ta4j.core.indicators.stdfilterednpolegaussian;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.cristian.chart.analyzer.enums.OperationType;
import com.cristian.chart.analyzer.exceptions.AppException;
import com.cristian.chart.analyzer.manager.CandleManager;
import com.cristian.chart.analyzer.models.CurrencyPair;
import com.cristian.chart.analyzer.models.Operation;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.indicators.GaussianFilter;
import org.ta4j.core.indicators.RecursiveCachedIndicator;
import org.ta4j.core.indicators.StandardDeviationFilter;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

// TODO: 2/12/22 Hacer que funcione bien sin reverts, para ello el unitario cargara datos de binance y vere que las ordenes son iguales
// que las de tradingview
public class STDFilteredNPoleGaussianFilter extends RecursiveCachedIndicator<Num> {

  private static final CandleManager CANDELS_MANAGER = new CandleManager();

  private static final String SMTH_TYPE = "Kaufman";

  private static final String SRCOPTION = "Close";

  private static final int PERIOD = 25; // TODO: 30/10/22 Ver que indica esto

  private static final int ORDER = 5;

  private static final String FILTER_OPEN = "Both";

  private static final int FILTER = 1;

  private static final int FILTER_PERIOD = 10;

  private static final boolean COLOR_BARS = true;

  private static final boolean SHOW_SIGS = true;

  private static final double KFL = 0.666;

  private static final double KSL = 0.0645;

  private static final int AMAFL = 2;

  private static final int AMASL = 30;

  private final CurrencyPair currencyPair;

  private double preContSw = 0;

  private boolean goLong;

  private boolean goShort;

  private STDFilteredNPoleGaussianFilter(final BarSeries barSeries, final CurrencyPair currencyPair) {
    super(barSeries);
    this.currencyPair = currencyPair;
  }

  public static STDFilteredNPoleGaussianFilter of(final BarSeries indicator, final CurrencyPair currencyPair) {
    return new STDFilteredNPoleGaussianFilter(indicator, currencyPair);
  }

  public List<Operation> execute() throws AppException {
    final List<Operation> operations = new ArrayList<>();
    final int rangeToFilter = 60;
    for (int i = getBarSeries().getBarCount(); i >= rangeToFilter; i--) {
      final int fromIndex = i - rangeToFilter;
      final BarSeries subSeries = getBarSeries().getSubSeries(fromIndex, i);
      operations.add(innerExecute(subSeries));
    }
    return operations;
  }

  private Operation innerExecute(final BarSeries src) throws AppException {
    Collections.reverse(src.getBarData());
    final BarSeries srcFiltered = FILTER > 0 ? buildBarSerie(StandardDeviationFilter.of(src, FILTER_PERIOD, FILTER).stream()) : src;
    // Hasta aqui igual que el antiguo
    final BarSeries srcFiltered2 = srcFiltered.getSubSeries(0, 51);
    final BarSeries outWithGaussian = buildBarSerie(GaussianFilter.of(new ClosePriceIndicator(srcFiltered2)).stream());
    Collections.reverse(outWithGaussian.getBarData());
    // Hasta aqui igual mas o menos

    final BarSeries outWithGaussian2 = outWithGaussian.getSubSeries(0, 40);
    Collections.reverse(outWithGaussian2.getBarData());
    final BarSeries outFiltered = FILTER > 0
        ? buildBarSerie(StandardDeviationFilter.of(outWithGaussian2, FILTER_PERIOD, FILTER).stream())
        : outWithGaussian2;
    final BarSeries outFiltered2 = outFiltered.getSubSeries(0, 31);
    Collections.reverse(outFiltered2.getBarData());

    final BarSeries sig = outFiltered2.getSubSeries(1, outFiltered2.getEndIndex() + 1);
    // Hasta aqui igual mas o menos

    calculateOperation(outFiltered2, sig);

    return Operation.of(this.currencyPair)
        .type(operationType())
        .time(Timestamp.valueOf(src.getLastBar().getEndTime().withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()).getTime());
  }

  private BarSeries buildBarSerie(final Stream<Num> values) {
    final ZonedDateTime time = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault());
    return new BaseBarSeriesBuilder()
        .withBars(values
            .filter(Objects::nonNull)
            .map(close -> new BaseBar(Duration.ofMinutes(15), time, 0, 0, 0, close.doubleValue(), 0))
            .collect(Collectors.toList()))
        .build();
  }

  private void calculateOperation(final BarSeries outFiltered, final BarSeries sig) {
    Collections.reverse(outFiltered.getBarData());
    Collections.reverse(sig.getBarData());
    final boolean pregoLong = isPregoLong(outFiltered, sig);
    final boolean pregoShort = isPregoShort(outFiltered, sig);

    this.goLong = pregoLong && this.preContSw == -1;
    this.goShort = pregoShort && this.preContSw == 1;

    if (pregoLong) {
      this.preContSw = 1;
    } else if (pregoShort) {
      this.preContSw = -1;
    }
  }

  private boolean isPregoShort(final BarSeries outFiltered, final BarSeries sig) {
    final Num outClosePrice = outFiltered.getBar(sig.getEndIndex() - 1).getClosePrice();
    final Num sigClosPrice = sig.getBar(sig.getEndIndex() - 1).getClosePrice();
    return outFiltered.getLastBar().getClosePrice().isLessThan(sig.getLastBar().getClosePrice())
        && outClosePrice.isGreaterThanOrEqual(sigClosPrice);
  }

  private boolean isPregoLong(final BarSeries outFiltered, final BarSeries sig) {
    final Num outClosePrice = outFiltered.getBar(sig.getEndIndex() - 1).getClosePrice();
    final Num sigClosPrice = sig.getBar(sig.getEndIndex() - 1).getClosePrice();
    return outFiltered.getLastBar().getClosePrice().isGreaterThan(sig.getLastBar().getClosePrice())
        && outClosePrice.isLessThanOrEqual(sigClosPrice);
  }

  private OperationType operationType() {
    if (this.goLong) {
      return OperationType.BUY;
    }
    if (this.goShort) {
      return OperationType.SELL;
    }
    return OperationType.NONE;
  }

  @Override
  protected Num calculate(final int index) {
    return null;
  }
}