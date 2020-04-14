package com.twitter.challenge;

import com.twitter.challenge.util.TemperatureConverter;

import org.assertj.core.data.Offset;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.within;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TemperatureConverterTests {
    @Test
    public void testCelsiusToFahrenheitConversion() {
        final Offset<Float> precision = within(0.01f);

        assertThat(TemperatureConverter.celsiusToFahrenheit(-50)).isEqualTo(-58, precision);
        assertThat(TemperatureConverter.celsiusToFahrenheit(0)).isEqualTo(32, precision);
        assertThat(TemperatureConverter.celsiusToFahrenheit(10)).isEqualTo(50, precision);
        assertThat(TemperatureConverter.celsiusToFahrenheit(21.11f)).isEqualTo(70, precision);
        assertThat(TemperatureConverter.celsiusToFahrenheit(37.78f)).isEqualTo(100, precision);
        assertThat(TemperatureConverter.celsiusToFahrenheit(100)).isEqualTo(212, precision);
        assertThat(TemperatureConverter.celsiusToFahrenheit(1000)).isEqualTo(1832, precision);
    }
}
