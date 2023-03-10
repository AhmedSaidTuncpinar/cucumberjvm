package io.cucumber.java.annotation;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParameterTypeSteps {

    private final LocalDate expected = LocalDate.of(1907, 11, 14);

    @ParameterType("([0-9]{4})-([0-9]{2})-([0-9]{2})")
    public LocalDate parameterTypeIso8601Date(String year, String month, String day) {
        return LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
    }

    @Given("today is {parameterTypeIso8601Date}")
    public void today_is(LocalDate date) {
        assertEquals(expected, date);
    }

}
