/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */
package be.e_contract.demo;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import jakarta.inject.Named;

@Named("outputEnumController")
public class OutputEnumController {

    public enum ExampleEnum {
        STATE1, STATE2, STATE3, STATE4
    }

    public ExampleEnum[] getEnums() {
        List<ExampleEnum> enums = new LinkedList<>(Arrays.asList(ExampleEnum.values()));
        enums.add(null);
        return enums.toArray(new ExampleEnum[0]);
    }
}
