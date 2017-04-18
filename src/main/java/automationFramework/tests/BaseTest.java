package automationFramework.tests;

import automationFramework.utils.GetProperties;
import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.Eyes;
import com.applitools.eyes.MatchLevel;
import org.openqa.selenium.WebDriver;
import automationFramework.utils.datatypes.BrowserType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

public class BaseTest {

    protected WebDriver driver;
    protected Eyes eyes;
    private static GetProperties properties = new GetProperties();
    private static String BROWSER = properties.getString("BROWSER").toUpperCase();;
    private static String APP_NAME = properties.getString("APP_NAME");
    private static String API_KEY = properties.getString("API_KEY");
    private BatchInfo batch;

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) throws Exception {
        BrowserType browserType = BrowserType.valueOf(BROWSER.toUpperCase());
        DesiredCapabilities capabilities;
        String baseline = method.getName();
        String batch_name = (!System.getenv("JOB_NAME").isEmpty())? System.getenv("JOB_NAME") : "Local";
        batch = new BatchInfo(batch_name);
        batch.setId(System.getenv("APPLITOOLS_BATCH_ID"));
        configureApplitoolsEyes();
        switch (browserType){
            case FIREFOX:
                capabilities = DesiredCapabilities.firefox();
                driver = new FirefoxDriver(capabilities);
                baseline += " Firefox";
                break;
            case CHROME:
                capabilities = DesiredCapabilities.chrome();
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--disable-extensions");
                capabilities.setCapability(ChromeOptions.CAPABILITY, options);
                driver = new ChromeDriver(capabilities);
                baseline += " Chrome";
                break;
            case IE:
                capabilities = DesiredCapabilities.internetExplorer();
                capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
                capabilities.setJavascriptEnabled(true);
                capabilities.setCapability("requireWindowFocus", false);
                capabilities.setCapability("enablePersistentHover", false);
                capabilities.setCapability("ignoreProtectedModeSettings", true);
                capabilities.setCapability(InternetExplorerDriver.ENABLE_ELEMENT_CACHE_CLEANUP, true);
                capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
                driver = new InternetExplorerDriver(capabilities);
                baseline += " Internet Explorer";
                break;
            default:
                capabilities = DesiredCapabilities.firefox();
                driver = new FirefoxDriver(capabilities);
        }
        try {
            driver.manage().window().maximize();
            driver = eyes.open(driver, APP_NAME, baseline);
            navigateToHome();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void navigateToHome(){
        String BASE_URL = properties.getString("BASE_URL");
        driver.get(BASE_URL);
    }

    private void configureApplitoolsEyes(){
        eyes = new Eyes();
        eyes.setApiKey(API_KEY);
        eyes.setMatchLevel(MatchLevel.LAYOUT2);
        eyes.setSaveNewTests(false);
        eyes.setBatch(batch);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() throws Exception {
        try {
            eyes.close();
        }
        finally {
            eyes.abortIfNotClosed();
            driver.quit();
        }
    }

}