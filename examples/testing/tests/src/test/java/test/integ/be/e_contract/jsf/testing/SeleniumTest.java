package test.integ.be.e_contract.jsf.testing;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class SeleniumTest {

    private WebDriver driver;

    @BeforeAll
    public static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void beforeEach() {
        this.driver = new ChromeDriver();
    }

    @AfterEach
    public void afterEach() {
        this.driver.quit();
    }

    @Test
    public void testInputOutputPage() throws Exception {
        InputOutputPage ioPage = new InputOutputPage(this.driver);
        ioPage.setInput("hello world");
        ioPage.submit();
        assertEquals("hello world", ioPage.getOutput());
    }
}
