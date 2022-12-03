package org.ta4j.core.indicators.stdfilterednpolegaussian;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.binance.api.client.domain.market.Candlestick;

import com.cristian.chart.analyzer.enums.CyprtoCurrency;
import com.cristian.chart.analyzer.enums.OperationType;
import com.cristian.chart.analyzer.exceptions.AppException;
import com.cristian.chart.analyzer.models.CurrencyPair;
import com.cristian.chart.analyzer.models.Operation;

import org.junit.Assert;
import org.junit.Test;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;

public class STDFilteredNPoleGaussianFilterTest {

  @Test
  public void buySignal() throws ParseException, AppException {
    final CurrencyPair currencyPair = new CurrencyPair(CyprtoCurrency.BTC, CyprtoCurrency.USDT);
    final List<Operation> operations = STDFilteredNPoleGaussianFilter.of(buildBarSerie(btcUsdQuotes()), currencyPair)
        .execute();

    operations.forEach(System.out::println);
    Assert.assertEquals(39, operations.size());
    Assert.assertEquals(36, operations.stream().map(Operation::getType).filter(OperationType.NONE::equals).count());
    final List<Operation> expOperations = Arrays.asList(
        Operation.of(currencyPair)
            .type(OperationType.BUY)
            .time(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S").parse("2022-10-27 00:15:00.0").getTime()),
        Operation.of(currencyPair)
            .type(OperationType.BUY)
            .time(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S").parse("2022-10-27 07:15:00.0").getTime()),
        Operation.of(currencyPair)
            .type(OperationType.SELL)
            .time(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S").parse("2022-10-27 03:30:00.0").getTime())
    );
    Assert.assertTrue(operations.containsAll(expOperations));
  }

  private BarSeries buildBarSerie(final List<Candlestick> candlesticks) {
    return new BaseBarSeriesBuilder()
        .withBars(candlesticks.stream()
            .map(this::buildBar)
            .collect(Collectors.toList()))
        .build();
  }

  private Bar buildBar(final Candlestick candlestick) {
    ZonedDateTime time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(candlestick.getOpenTime()), ZoneOffset.UTC);
    return new BaseBar(Duration.ofMinutes(15), time, 0, 0, 0, Double.parseDouble(candlestick.getClose()), 0);
  }

  /**
   * BTC-USD quotes.
   */
  private List<Candlestick> btcUsdQuotes() throws ParseException {
    final List<Candlestick> candlesticks = Arrays.asList(
        candlestick(20146.96, "2022-10-26 07:00:00"),
        candlestick(20161.60, "2022-10-26 07:15:00"),
        candlestick(20192.01, "2022-10-26 07:30:00"),
        candlestick(20188.06, "2022-10-26 07:45:00"),

        candlestick(20229.53, "2022-10-26 08:00:00"),
        candlestick(20177.49, "2022-10-26 08:15:00"),
        candlestick(20231.65, "2022-10-26 08:30:00"),
        candlestick(20217.15, "2022-10-26 08:45:00"),

        candlestick(20218.71, "2022-10-26 09:00:00"),
        candlestick(20336.30, "2022-10-26 09:15:00"),
        candlestick(20315.15, "2022-10-26 09:30:00"),
        candlestick(20348.53, "2022-10-26 09:45:00"),

        candlestick(20304.27, "2022-10-26 10:00:00"),
        candlestick(20319.18, "2022-10-26 10:15:00"),
        candlestick(20668.50, "2022-10-26 10:30:00"),
        candlestick(20668.78, "2022-10-26 10:45:00"),

        candlestick(20593.88, "2022-10-26 11:00:00"),
        candlestick(20607.81, "2022-10-26 11:15:00"),
        candlestick(20650.08, "2022-10-26 11:30:00"),
        candlestick(20594.57, "2022-10-26 11:45:00"),

        candlestick(20562.43, "2022-10-26 12:00:00"),
        candlestick(20635.60, "2022-10-26 12:15:00"),
        candlestick(20670.93, "2022-10-26 12:30:00"),
        candlestick(20665.99, "2022-10-26 12:45:00"),

        candlestick(20664.32, "2022-10-26 13:00:00"),
        candlestick(20657.40, "2022-10-26 13:15:00"),
        candlestick(20626.12, "2022-10-26 13:30:00"),
        candlestick(20606.28, "2022-10-26 13:45:00"),

        candlestick(20597.99, "2022-10-26 14:00:00"),
        candlestick(20534.22, "2022-10-26 14:15:00"),
        candlestick(20552.12, "2022-10-26 14:30:00"),
        candlestick(20548.93, "2022-10-26 14:45:00"),

        candlestick(20555.28, "2022-10-26 15:00:00"),
        candlestick(20458.21, "2022-10-26 15:15:00"),
        candlestick(20473.35, "2022-10-26 15:30:00"),
        candlestick(20554.18, "2022-10-26 15:45:00"),

        candlestick(20753.05, "2022-10-26 16:00:00"),
        candlestick(20822.06, "2022-10-26 16:15:00"),
        candlestick(20948.61, "2022-10-26 16:30:00"),
        candlestick(20831.76, "2022-10-26 16:45:00"),

        candlestick(20883.76, "2022-10-26 17:00:00"),
        candlestick(20872.47, "2022-10-26 17:15:00"),
        candlestick(20927.97, "2022-10-26 17:30:00"),
        candlestick(20845.93, "2022-10-26 17:45:00"),

        candlestick(20914.84, "2022-10-26 18:00:00"),
        candlestick(20830.38, "2022-10-26 18:15:00"),
        candlestick(20784.96, "2022-10-26 18:30:00"),
        candlestick(20762.81, "2022-10-26 18:45:00"),

        candlestick(20787.11, "2022-10-26 19:00:00"),
        candlestick(20723.37, "2022-10-26 19:15:00"),
        candlestick(20745.74, "2022-10-26 19:30:00"),
        candlestick(20822.61, "2022-10-26 19:45:00"),

        candlestick(20816.23, "2022-10-26 20:00:00"),
        candlestick(20759.79, "2022-10-26 20:15:00"),
        candlestick(20715.83, "2022-10-26 20:30:00"),
        candlestick(20695.73, "2022-10-26 20:45:00"),

        candlestick(20697.36, "2022-10-26 21:00:00"),
        candlestick(20736.06, "2022-10-26 21:15:00"),
        candlestick(20771.38, "2022-10-26 21:30:00"),
        candlestick(20761.61, "2022-10-26 21:45:00"),

        candlestick(20771.15, "2022-10-26 22:00:00"),
        candlestick(20710.18, "2022-10-26 22:15:00"),
        candlestick(20746.92, "2022-10-26 22:30:00"),
        candlestick(20746.06, "2022-10-26 22:45:00"),

        candlestick(20725.53, "2022-10-26 23:00:00"),
        candlestick(20743.32, "2022-10-26 23:15:00"),
        candlestick(20751.96, "2022-10-26 23:30:00"),
        candlestick(20740.68, "2022-10-26 23:45:00"),

        candlestick(20786.28, "2022-10-27 00:00:00"),
        candlestick(20862.30, "2022-10-27 00:15:00"),
        candlestick(20786.98, "2022-10-27 00:30:00"),
        candlestick(20818.12, "2022-10-27 00:45:00"),

        candlestick(20849.19, "2022-10-27 01:00:00"),
        candlestick(20827.58, "2022-10-27 01:15:00"),
        candlestick(20789.94, "2022-10-27 01:30:00"),
        candlestick(20769.26, "2022-10-27 01:45:00"),

        candlestick(20804.46, "2022-10-27 02:00:00"),
        candlestick(20762.12, "2022-10-27 02:15:00"),
        candlestick(20742.78, "2022-10-27 02:30:00"),
        candlestick(20755.27, "2022-10-27 02:45:00"),

        candlestick(20771.28, "2022-10-27 03:00:00"),
        candlestick(20722.67, "2022-10-27 03:15:00"),
        candlestick(20735.79, "2022-10-27 03:30:00"),
        candlestick(20724.76, "2022-10-27 03:45:00"),

        candlestick(20770.00, "2022-10-27 04:00:00"),
        candlestick(20769.57, "2022-10-27 04:15:00"),
        candlestick(20777.36, "2022-10-27 04:30:00"),
        candlestick(20683.62, "2022-10-27 04:45:00"),

        candlestick(20712.48, "2022-10-27 05:00:00"),
        candlestick(20678.89, "2022-10-27 05:15:00"),
        candlestick(20722.68, "2022-10-27 05:30:00"),
        candlestick(20754.35, "2022-10-27 05:45:00"),

        candlestick(20747.84, "2022-10-27 06:00:00"),
        candlestick(20791.80, "2022-10-27 06:15:00"),
        candlestick(20781.86, "2022-10-27 06:30:00"),
        candlestick(20803.98, "2022-10-27 06:45:00"),

        candlestick(20827.83, "2022-10-27 07:00:00"),
        candlestick(20823.07, "2022-10-27 07:15:00")
    );
    Collections.reverse(candlesticks);
    return candlesticks;
  }

  private Candlestick candlestick(final double closeValue, final String openTime) throws ParseException {
    final Candlestick candlestick = new Candlestick();
    candlestick.setClose(String.valueOf(closeValue));
    candlestick.setOpenTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(openTime).getTime());
    return candlestick;
  }
}