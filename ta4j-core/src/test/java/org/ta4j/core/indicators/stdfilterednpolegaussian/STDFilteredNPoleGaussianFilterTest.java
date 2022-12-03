package org.ta4j.core.indicators.stdfilterednpolegaussian;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.binance.api.client.domain.market.Candlestick;

import com.cristian.chart.analyzer.CandlestickLoader;
import com.cristian.chart.analyzer.enums.CyprtoCurrency;
import com.cristian.chart.analyzer.enums.OperationType;
import com.cristian.chart.analyzer.models.CurrencyPair;
import com.cristian.chart.analyzer.models.Operation;

import org.junit.Assert;
import org.junit.Test;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;

public class STDFilteredNPoleGaussianFilterTest {

  private static final CurrencyPair CURRENCY_PAIR = new CurrencyPair(CyprtoCurrency.BTC, CyprtoCurrency.USDT);

  @Test
  public void testIndicator() throws ParseException {
    final List<Candlestick> candlesticks = CandlestickLoader.btcUsdt();

    final List<Operation> operations = STDFilteredNPoleGaussianFilter.of(buildBarSerie(candlesticks), CURRENCY_PAIR).execute();

    Assert.assertEquals(902, operations.size());
    Assert.assertEquals(849, operations.stream().map(Operation::getType).filter(OperationType.NONE::equals).count());
    Assert.assertEquals(expBuys(), findOperationByType(operations, OperationType.BUY));
    Assert.assertEquals(expSells(), findOperationByType(operations, OperationType.SELL));
  }

  private List<Operation> expBuys() throws ParseException {
    return Arrays.asList(
        buildBuyOperation("2022-11-01 20:30:00.0"),
        buildBuyOperation("2022-11-02 00:45:00.0"),
        buildBuyOperation("2022-11-02 10:45:00.0"),
        buildBuyOperation("2022-11-02 16:30:00.0"),
        buildBuyOperation("2022-11-03 02:00:00.0"),
        buildBuyOperation("2022-11-03 16:00:00.0"),
        buildBuyOperation("2022-11-03 22:45:00.0"),
        buildBuyOperation("2022-11-04 02:00:00.0"),
        buildBuyOperation("2022-11-04 14:00:00.0"),
        buildBuyOperation("2022-11-04 20:30:00.0"),
        buildBuyOperation("2022-11-05 12:45:00.0"),
        buildBuyOperation("2022-11-05 18:45:00.0"),
        buildBuyOperation("2022-11-05 21:00:00.0"),
        buildBuyOperation("2022-11-06 02:30:00.0"),
        buildBuyOperation("2022-11-06 10:00:00.0"),
        buildBuyOperation("2022-11-06 16:45:00.0"),
        buildBuyOperation("2022-11-07 04:15:00.0"),
        buildBuyOperation("2022-11-07 11:45:00.0"),
        buildBuyOperation("2022-11-07 17:30:00.0"),
        buildBuyOperation("2022-11-07 20:30:00.0"),
        buildBuyOperation("2022-11-08 02:45:00.0"),
        buildBuyOperation("2022-11-08 17:30:00.0"),
        buildBuyOperation("2022-11-08 23:45:00.0"),
        buildBuyOperation("2022-11-09 06:45:00.0"),
        buildBuyOperation("2022-11-10 02:00:00.0"),
        buildBuyOperation("2022-11-10 14:45:00.0"),
        buildBuyOperation("2022-11-10 22:00:00.0")
    );
  }

  private List<Operation> expSells() throws ParseException {
    return Arrays.asList(
        buildSellOperation("2022-11-02 00:00:00.0"),
        buildSellOperation("2022-11-02 07:15:00.0"),
        buildSellOperation("2022-11-02 11:45:00.0"),
        buildSellOperation("2022-11-02 21:00:00.0"),
        buildSellOperation("2022-11-03 08:45:00.0"),
        buildSellOperation("2022-11-03 19:15:00.0"),
        buildSellOperation("2022-11-03 23:45:00.0"),
        buildSellOperation("2022-11-04 13:15:00.0"),
        buildSellOperation("2022-11-04 19:00:00.0"),
        buildSellOperation("2022-11-05 07:45:00.0"),
        buildSellOperation("2022-11-05 14:45:00.0"),
        buildSellOperation("2022-11-05 20:15:00.0"),
        buildSellOperation("2022-11-06 00:15:00.0"),
        buildSellOperation("2022-11-06 03:15:00.0"),
        buildSellOperation("2022-11-06 13:45:00.0"),
        buildSellOperation("2022-11-06 19:30:00.0"),
        buildSellOperation("2022-11-07 04:45:00.0"),
        buildSellOperation("2022-11-07 15:00:00.0"),
        buildSellOperation("2022-11-07 18:30:00.0"),
        buildSellOperation("2022-11-07 23:30:00.0"),
        buildSellOperation("2022-11-08 04:45:00.0"),
        buildSellOperation("2022-11-08 19:30:00.0"),
        buildSellOperation("2022-11-09 02:30:00.0"),
        buildSellOperation("2022-11-09 09:30:00.0"),
        buildSellOperation("2022-11-10 11:30:00.0"),
        buildSellOperation("2022-11-10 19:45:00.0")
    );
  }

  private List<Operation> findOperationByType(final List<Operation> operations, final OperationType buy) {
    return operations.stream().filter(operation -> buy.equals(operation.getType())).collect(Collectors.toList());
  }

  private Operation buildOperation(final OperationType buy, final String time) throws ParseException {
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
    return Operation.of(CURRENCY_PAIR)
        .type(buy)
        .time(simpleDateFormat.parse(time).getTime());
  }

  private Operation buildBuyOperation(final String time) throws ParseException {
    return buildOperation(OperationType.BUY, time);
  }

  private Operation buildSellOperation(final String time) throws ParseException {
    return buildOperation(OperationType.SELL, time);
  }

  private BarSeries buildBarSerie(final List<Candlestick> candlesticks) {
    return new BaseBarSeriesBuilder()
        .withBars(candlesticksToBars(candlesticks))
        .build();
  }

  private List<Bar> candlesticksToBars(final List<Candlestick> candlesticks) {
    return candlesticks.stream()
        .map(this::buildBar)
        .collect(Collectors.toList());
  }

  private Bar buildBar(final Candlestick candlestick) {
    final ZonedDateTime time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(candlestick.getOpenTime()), ZoneId.systemDefault());
    return new BaseBar(Duration.ofMinutes(15), time, 0, 0, 0, Double.parseDouble(candlestick.getClose()), 0);
  }
}