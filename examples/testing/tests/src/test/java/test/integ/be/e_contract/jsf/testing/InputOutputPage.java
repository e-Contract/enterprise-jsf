package test.integ.be.e_contract.jsf.testing;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class InputOutputPage {

    private final WebDriver driver;

    public InputOutputPage(WebDriver driver) {
        this.driver = driver;
        this.driver.get("http://localhost:8080/testing/input-output.xhtml");
    }

    public void setInput(String value) {
        WebElement input = this.driver.findElement(By.id("mainForm:input"));
        input.sendKeys(value);
    }

    public String getOutput() {
        WebElement output = this.driver.findElement(By.id("mainForm:output"));
        return output.getText();
    }

    public void submit() {
        WebElement button = this.driver.findElement(By.id("mainForm:button"));
        button.click();
    }
}
