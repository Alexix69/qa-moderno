package demo;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class SeleniumLocatorDemo {

  // Visual pause between demo sections (ms).
  private static final int STEP_PAUSE = 3500;

  public static void main(String[] args) {
    ChromeOptions options = new ChromeOptions();

    WebDriver driver = new ChromeDriver(options);
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));

    try {

      // ── DEMO 1 ─────────────────────────────────────────────────────────────
      // WebElement: findElement returns a single node handle (throws if absent).
      // ───────────────────────────────────────────────────────────────────────
      banner("[DEMO 1] WebElement — locating a single element with By.id()");
      System.out.println("  driver.findElement(By.id(\"username\"))");
      System.out.println("  A WebElement is Selenium's live handle to a DOM node.");
      System.out.println("  findElement returns ONE element, or throws NoSuchElementException.");

      driver.get("https://the-internet.herokuapp.com/login");
      WebElement username = driver.findElement(By.id("username"));
      username.sendKeys("tomsmith");
      highlightElement(driver, username);
      pause(STEP_PAUSE);

      // ── DEMO 2 ─────────────────────────────────────────────────────────────
      // Locator hierarchy — same element found four different ways.
      // ───────────────────────────────────────────────────────────────────────
      banner("[DEMO 2] Locator hierarchy — same element, four strategies");
      System.out.println("  Ordered from most robust to most fragile:");

      driver.get("https://the-internet.herokuapp.com/login");
      showLocator(driver, By.id("username"),                   "By.id(\"username\")            ← most robust");
      showLocator(driver, By.name("username"),                 "By.name(\"username\")");
      showLocator(driver, By.cssSelector("#username"),         "By.cssSelector(\"#username\")");
      showLocator(driver, By.xpath("//input[@id='username']"), "By.xpath(\"//input[@id='username']\")");
      pause(STEP_PAUSE);

      // ── DEMO 3 ─────────────────────────────────────────────────────────────
      // Fragile vs robust selector.
      // ───────────────────────────────────────────────────────────────────────
      banner("[DEMO 3] Fragile vs robust selector");

      driver.get("https://the-internet.herokuapp.com/login");
      try {
        WebElement fragile = driver.findElement(
            By.xpath("/html/body/div[2]/div/form/input[1]"));
        System.out.println("  [FRAGILE]  By.xpath(\"/html/body/div[2]/div/form/input[1]\")");
        System.out.println("             Absolute path — breaks on any structural DOM change.");
        highlightElement(driver, fragile);
        pause(2000);
      } catch (Exception ex) {
        System.out.println("  [FRAGILE]  Absolute XPath — element NOT found. This demonstrates fragility.");
      }

      WebElement robust = driver.findElement(By.id("username"));
      System.out.println("  [ROBUST]   By.id(\"username\") — semantic, stable, readable.");
      highlightElement(driver, robust);
      pause(STEP_PAUSE);

      // ── DEMO 4 ─────────────────────────────────────────────────────────────
      // findElements: returns a List — empty if nothing matches, never throws.
      // SelectorsHub practice page has many inputs: ideal for this concept.
      // ───────────────────────────────────────────────────────────────────────
      banner("[DEMO 4] findElements — locating multiple nodes at once");
      System.out.println("  driver.findElements(By.tagName(\"input\"))");
      System.out.println("  Returns List<WebElement> — empty list if none found (never throws).");

      driver.get("https://selectorshub.com/xpath-practice-page/");
      List<WebElement> inputs = driver.findElements(By.tagName("input"));
      System.out.println("  Inputs found on this page: " + inputs.size());

      if (!inputs.isEmpty()) {
        System.out.println("  Highlighting first input element...");
        highlightElement(driver, inputs.get(0));
      }
      pause(STEP_PAUSE);

      banner("Demo complete — browser closing.");

    } finally {
      driver.quit();
    }
  }

  // ── Helpers ──────────────────────────────────────────────────────────────

  /**
   * Prints a locator label, finds the element, and animates it.
   */
  private static void showLocator(WebDriver driver, By by, String label) {
    System.out.println("    → " + label);
    WebElement element = driver.findElement(by);
    highlightElement(driver, element);
    pause(800);
  }

  /**
   * Animates element with a blinking red outline — 5 cycles × 400 ms ≈ 2 s.
   * Leaves the outline visible after the animation ends.
   */
  private static void highlightElement(WebDriver driver, WebElement element) {
    JavascriptExecutor js = (JavascriptExecutor) driver;
    String on  = "arguments[0].style.outline='3px solid #ff0000';" +
                 "arguments[0].style.boxShadow='0 0 10px 2px #ff0000';";
    String off = "arguments[0].style.outline='';" +
                 "arguments[0].style.boxShadow='';";
    for (int i = 0; i < 5; i++) {
      js.executeScript(i % 2 == 0 ? on : off, element);
      pause(400);
    }
    js.executeScript(on, element); // leave border on
  }

  /** Prints a clearly visible section banner to the console. */
  private static void banner(String title) {
    System.out.println("\n══════════════════════════════════════════════════════");
    System.out.println("  " + title);
    System.out.println("══════════════════════════════════════════════════════");
  }

  private static void pause(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
