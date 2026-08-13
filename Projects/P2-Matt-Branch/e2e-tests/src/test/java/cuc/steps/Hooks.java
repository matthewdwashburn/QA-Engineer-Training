package cuc.steps;

import java.util.Map;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Label;

/**
 * Owns the WebDriver and database lifecycle for every scenario. Keeping this
 * in one place (instead of one @Before/@After per step class) means exactly
 * one browser is opened per scenario, shared across all step classes via
 * TestContext, against a database in a known state.
 */
public class Hooks {

    private static final boolean HEADLESS = Boolean.getBoolean("e2e.headless");

    private final TestContext context;

    public Hooks(TestContext context) {
        this.context = context;
    }

    @Before(order = 0)
    public void groupAllureResult(Scenario scenario) {
        String suite = scenario.getSourceTagNames().contains("@manager-e2e")
                ? "Manager - End-to-End Tests"
                : scenario.getSourceTagNames().contains("@cross-role-e2e")
                        ? "Cross Role Lifecycle"
                        : "Employee - End-to-End Tests";

        Allure.getLifecycle().updateTestCase(testResult -> {
            testResult.getLabels().removeIf(label -> "suite".equals(label.getName()));
            testResult.getLabels().add(new Label().setName("suite").setValue(suite));
        });
    }

    /**
     * Runs before the browser starts, so no page can be mid-request while the
     * expense tables are being rewritten. See {@link DatabaseState} for why the
     * reset is data-only and why neither application needs restarting.
     */
    @Before(order = 1)
    public void resetDatabase() {
        DatabaseState.resetToSeedBaseline();
    }

    @Before(order = 2)
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        // Chrome's native "Save password?" / leak-detection prompts are browser-chrome
        // UI, not page DOM — Selenium can't dismiss them, and they can steal focus
        // mid-scenario. Disable the password manager entirely for automated runs.
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false
        ));
        options.addArguments("--disable-features=PasswordLeakDetection,AutofillServerCommunication");

        if (HEADLESS) {
            // Chrome will not start as root with the sandbox on, and /dev/shm is
            // only 64MB inside a container.
            options.addArguments("--headless=new", "--no-sandbox",
                    "--disable-dev-shm-usage", "--window-size=1920,1080");
        }

        ChromeDriver driver = new ChromeDriver(options);
        if (!HEADLESS) {
            driver.manage().window().maximize();
        }
        context.setDriver(driver);
    }

    @After
    public void tearDown() {
        if (context.getDriver() != null) {
            context.getDriver().quit();
        }
    }
}
