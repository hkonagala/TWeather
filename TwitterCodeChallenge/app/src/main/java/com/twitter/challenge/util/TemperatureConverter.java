package com.twitter.challenge.util;

public class TemperatureConverter {
    /**
     * Converts temperature in Celsius to temperature in Fahrenheit.
     *
     * @param temperatureInCelsius Temperature in Celsius to convert.
     * @return Temperature in Fahrenheit.
     */
    public static float celsiusToFahrenheit(float temperatureInCelsius) {
        return temperatureInCelsius * 1.8f + 32;
    }

    /**
     *
     * @param temperatureInCelsius Temperature in Celsius
     * @return formatted string ex: 14.2 °C / 57.559 °F
     */
    public static String processTemperature(float temperatureInCelsius) {
        return temperatureInCelsius + " °C / " + celsiusToFahrenheit(temperatureInCelsius) + " °F";
    }

    /**
     * Calculates standard deviation of temperatures.
     *
     * @param numArray of temperatures for next N days
     * @return SD
     */
    public static double calculateSD(float[] numArray)
    {
        double sum = 0.0, standardDeviation = 0.0;
        int length = numArray.length;

        for(double num : numArray) {
            sum += num;
        }

        double mean = sum/length;

        for(double num: numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.round(Math.sqrt(standardDeviation/length) * 100.0) / 100.0; // round off to two decimal places
    }
}
