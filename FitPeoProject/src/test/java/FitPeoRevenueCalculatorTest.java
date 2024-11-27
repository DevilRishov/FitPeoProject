import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.interactions.Actions;
import java.time.Duration;

public class FitPeoRevenueCalculatorTest {

    WebDriver driver;

    @BeforeMethod
    public void setUp() {
       
        WebDriverManager.firefoxdriver().browserVersion("0.30.0").setup();  
        driver = new FirefoxDriver();

       
        driver.manage().window().maximize();
    }

    @Test
    public void testRevenueCalculator() {
        driver.get("https://www.fitpeo.com");

        
        WebDriverWait waitForPageLoad = new WebDriverWait(driver, Duration.ofSeconds(120)); // Increased timeout for page load
        waitForPageLoad.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Revenue Calculator")));

        
        navigateToRevenueCalculatorPage();
        scrollToSliderAndAdjustValue(820);
        enterValueInTextFieldAndVerifySlider(560);
        selectCPTCodes();
        validateTotalRecurringReimbursement();
    }

    private void navigateToRevenueCalculatorPage() {
        WebElement revenueCalculatorLink = driver.findElement(By.linkText("Revenue Calculator"));
        revenueCalculatorLink.click();
        
        
        System.out.println("Current URL: " + driver.getCurrentUrl());
        System.out.println("Page Source: " + driver.getPageSource());

        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(180));  // Increased wait time
        
        try {
            
            WebElement sliderSection = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("revenue-slider-section")));
            
            if (sliderSection == null) {
                System.out.println("Slider section not visible even after waiting.");
                Assert.fail("Slider section not found!");
            }
            
            
            wait.until(ExpectedConditions.elementToBeClickable(sliderSection));

        } catch (TimeoutException e) {
            System.out.println("Timed out waiting for the slider section to be visible: " + e.getMessage());
            Assert.fail("Timed out waiting for the slider section.");
        }
    }

    private void scrollToSliderAndAdjustValue(int value) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60)); // Increased timeout to 60 seconds
        
        try {
            WebElement sliderSection = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("revenue-slider-section")));
            
            
            Actions actions = new Actions(driver);
            actions.moveToElement(sliderSection).perform();

           
            WebElement slider = driver.findElement(By.id("revenue-slider"));
            actions.dragAndDropBy(slider, value, 0).perform();

           
            WebElement textField = driver.findElement(By.id("revenue-value"));
            String sliderValue = textField.getAttribute("value");
            Assert.assertEquals(sliderValue, String.valueOf(value), "Slider value mismatch!");
        } catch (TimeoutException e) {
            System.out.println("Timed out waiting for slider section: " + e.getMessage());
            Assert.fail("Slider section not found or visible.");
        }
    }

    private void enterValueInTextFieldAndVerifySlider(int value) {
        try {
            
            WebElement textField = driver.findElement(By.id("revenue-value"));
            textField.clear();
            textField.sendKeys(String.valueOf(value));

            
            WebElement slider = driver.findElement(By.id("revenue-slider"));
            String updatedSliderValue = slider.getAttribute("value");
            Assert.assertEquals(updatedSliderValue, String.valueOf(value), "Slider position mismatch after text entry!");
        } catch (NoSuchElementException e) {
            System.out.println("Error interacting with slider or text field: " + e.getMessage());
            Assert.fail("Unable to interact with slider or text field.");
        }
    }

    private void selectCPTCodes() {
        try {
            
            driver.findElement(By.id("cpt-99091")).click();
            driver.findElement(By.id("cpt-99453")).click();
            driver.findElement(By.id("cpt-99454")).click();
            driver.findElement(By.id("cpt-99474")).click();
        } catch (NoSuchElementException e) {
            System.out.println("Error selecting CPT codes: " + e.getMessage());
            Assert.fail("Unable to select CPT codes.");
        }
    }

    private void validateTotalRecurringReimbursement() {
        try {
            
            WebElement reimbursementHeader = driver.findElement(By.id("total-recurring-reimbursement"));
            String totalReimbursement = reimbursementHeader.getText();
            Assert.assertEquals(totalReimbursement, "$110700", "Total Recurring Reimbursement value mismatch!");
        } catch (NoSuchElementException e) {
            System.out.println("Error validating total recurring reimbursement: " + e.getMessage());
            Assert.fail("Unable to validate total recurring reimbursement.");
        }
    }

    @AfterMethod
    public void tearDown() {
        
        if (driver != null) {
            driver.quit();  
            System.out.println("Test Completed Successfully");
        }
    }
}