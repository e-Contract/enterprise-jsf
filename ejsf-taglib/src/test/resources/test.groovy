/*
 * Enterprise JSF project.
 *
 * Copyright 2023 e-Contract.be BV. All rights reserved.
 * e-Contract.be BV proprietary/confidential. Use is subject to license terms.
 */

counter = 1;

def increaseCounter() {
    println("increaseCounter invoked");
    counter = counter + 1;
}

println("Hello world from groovy");
println("Counter: " + counter);
increaseCounter();
println("Counter: " + counter);
