/*
 * Enterprise JSF project.
 *
 * Copyright 2023-2024 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import jakarta.inject.Named;

@Named("outputDayOfWeekController")
public class OutputDayOfWeekController {

    public List<DayOfWeek> getDayOfWeekList() {
        List<DayOfWeek> result = new LinkedList<>();
        result.addAll(Arrays.asList(DayOfWeek.values()));
        result.add(null);
        return result;
    }
}
