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

  // Overlay panel element ID injected into each page.
  private static final String OVERLAY_ID = "__selenium_demo_overlay__";

  public static void main(String[] args) {
    ChromeOptions options = new ChromeOptions();

    WebDriver driver = new ChromeDriver(options);
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));

    try {

      // ── DEMO 1 ─────────────────────────────────────────────────────────────
      // WebElement: findElement returns a single node handle (throws if absent).
      // ───────────────────────────────────────────────────────────────────────
      driver.get("https://the-internet.herokuapp.com/login");
      createOverlay(driver);

      String d1Title = "[DEMO 1] WebElement — By.id()";
      String d1Body  = "driver.findElement(By.id(\"username\"))\n\n"
                     + "WebElement = Selenium's live handle to a DOM node.\n"
                     + "findElement → returns ONE element\n"
                     + "             or throws NoSuchElementException.";
      banner(d1Title);
      System.out.println("  driver.findElement(By.id(\"username\"))");
      System.out.println("  A WebElement is Selenium's live handle to a DOM node.");
      System.out.println("  findElement returns ONE element, or throws NoSuchElementException.");
      updateOverlay(driver, d1Title, d1Body);

      WebElement username = driver.findElement(By.id("username"));
      username.sendKeys("tomsmith");
      highlightElement(driver, username);
      pause(STEP_PAUSE);

      // ── DEMO 2 ─────────────────────────────────────────────────────────────
      // Locator hierarchy — same element found four different ways.
      // ───────────────────────────────────────────────────────────────────────
      driver.get("https://the-internet.herokuapp.com/login");
      createOverlay(driver);

      String d2Title = "[DEMO 2] Locator hierarchy";
      banner(d2Title);
      System.out.println("  Same element — four different locator strategies:");

      String[] locatorLabels = {
        "By.id(\"username\")            ← most robust",
        "By.name(\"username\")",
        "By.cssSelector(\"#username\")",
        "By.xpath(\"//input[@id='username']\")"
      };
      By[] locators = {
        By.id("username"),
        By.name("username"),
        By.cssSelector("#username"),
        By.xpath("//input[@id='username']")
      };

      for (int i = 0; i < locators.length; i++) {
        System.out.println("    → " + locatorLabels[i]);
        updateOverlay(driver, d2Title,
            "Locating the same element four ways:\n\n  → " + locatorLabels[i]);
        WebElement el = driver.findElement(locators[i]);
        highlightElement(driver, el);
        pause(800);
      }
      pause(STEP_PAUSE);

      // ── DEMO 3 ─────────────────────────────────────────────────────────────
      // Fragile vs robust selector.
      // ───────────────────────────────────────────────────────────────────────
      driver.get("https://the-internet.herokuapp.com/login");
      createOverlay(driver);

      String d3Title = "[DEMO 3] Fragile vs robust selector";
      banner(d3Title);

      try {
        WebElement fragile = driver.findElement(
            By.xpath("/html/body/div[2]/div/form/input[1]"));
        String fragileMsg = "FRAGILE XPath:\n/html/body/div[2]/div/form/input[1]\n\n"
                          + "Absolute path — breaks on any\nstructural DOM change.";
        System.out.println("  [FRAGILE]  By.xpath(\"/html/body/div[2]/div/form/input[1]\")");
        System.out.println("             Absolute path — breaks on any structural DOM change.");
        updateOverlay(driver, d3Title, fragileMsg);
        highlightElement(driver, fragile);
        pause(2000);
      } catch (Exception ex) {
        System.out.println("  [FRAGILE]  Absolute XPath — element NOT found. This demonstrates fragility.");
        updateOverlay(driver, d3Title,
            "FRAGILE XPath: element NOT found!\n\nAbsolute paths break on DOM changes.\nThis IS the point.");
        pause(2000);
      }

      WebElement robust = driver.findElement(By.id("username"));
      String robustMsg = "ROBUST locator:\nBy.id(\"username\")\n\nSemantic, stable,\nindependent of DOM structure.";
      System.out.println("  [ROBUST]   By.id(\"username\") — semantic, stable, readable.");
      updateOverlay(driver, d3Title, robustMsg);
      highlightElement(driver, robust);
      pause(STEP_PAUSE);

      // ── DEMO 4 ─────────────────────────────────────────────────────────────
      // findElements: returns a List — empty if nothing matches, never throws.
      // SelectorsHub practice page has many inputs: ideal for this concept.
      // ───────────────────────────────────────────────────────────────────────
      driver.get("https://selectorshub.com/xpath-practice-page/");
      createOverlay(driver);

      String d4Title = "[DEMO 4] findElements — multiple nodes";
      banner(d4Title);
      System.out.println("  driver.findElements(By.tagName(\"input\"))");
      System.out.println("  Returns List<WebElement> — empty list if none found (never throws).");
      updateOverlay(driver, d4Title,
          "driver.findElements(By.tagName(\"input\"))\n\n"
        + "Returns List<WebElement>.\n"
        + "Empty list if none found (never throws).\n\n"
        + "Counting and highlighting ALL inputs...");

      List<WebElement> inputs = driver.findElements(By.tagName("input"));
      System.out.println("  Inputs found on this page: " + inputs.size());
      updateOverlay(driver, d4Title,
          "findElements(By.tagName(\"input\"))\n\n"
        + "Found: " + inputs.size() + " input elements\n\n"
        + "Highlighting ALL matches simultaneously...");

      // Highlight every matched element at once via a single JS call.
      highlightAll(driver, inputs);
      pause(STEP_PAUSE);

      banner("Demo complete — browser closing.");
      updateOverlay(driver, "Demo complete", "Thank you!\n\nBrowser closing...");
      pause(1500);

    } finally {
      driver.quit();
    }
  }

  // ── Overlay helpers ───────────────────────────────────────────────────────

  /**
   * Injects the floating overlay panel into the current page.
   * Must be called after each driver.get() because navigation clears injected DOM.
   */
  private static void createOverlay(WebDriver driver) {
    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript(
        "var existing = document.getElementById('" + OVERLAY_ID + "');" +
        "if (existing) existing.remove();" +
        "var panel = document.createElement('div');" +
        "panel.id = '" + OVERLAY_ID + "';" +
        "panel.style.cssText = '" +
            "position:fixed;" +
            "top:20px;" +
            "right:20px;" +
            "max-width:360px;" +
            "background:rgba(0,0,0,0.87);" +
            "color:#fff;" +
            "padding:16px 20px;" +
            "font-family:monospace;" +
            "font-size:13px;" +
            "line-height:1.6;" +
            "border-radius:8px;" +
            "z-index:999999;" +
            "box-shadow:0 4px 24px rgba(0,0,0,0.5);" +
            "white-space:pre-wrap;" +
            "pointer-events:none;" +
        "';" +
        "document.body.appendChild(panel);"
    );
  }

  /**
   * Updates the overlay panel text with a title and body message.
   */
  private static void updateOverlay(WebDriver driver, String title, String body) {
    JavascriptExecutor js = (JavascriptExecutor) driver;
    // Escape for embedding inside a JS single-quoted string literal.
    String safeTitle = title.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n");
    String safeBody  = body .replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n");
    js.executeScript(
        "var panel = document.getElementById('" + OVERLAY_ID + "');" +
        "if (panel) {" +
        "  panel.innerHTML = '<span style=\"color:#ff6b6b;font-weight:bold\">' + '" + safeTitle + "' + '</span>\\n\\n' + '" + safeBody + "';" +
        "}"
    );
  }

  // ── Highlight helpers ─────────────────────────────────────────────────────

  /**
   * Blinks a single element — 5 cycles × 400 ms ≈ 2 s.
   * Leaves the red outline visible after animation.
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

  /**
   * Highlights ALL elements in the list simultaneously using a single JS call,
   * then blinks them in sync to demonstrate that findElements returned many nodes.
   */
  private static void highlightAll(WebDriver driver, List<WebElement> elements) {
    if (elements.isEmpty()) return;
    JavascriptExecutor js = (JavascriptExecutor) driver;
    Object[] args = elements.toArray();

    String applyStyle = buildBulkStyleScript("3px solid #ff0000", "0 0 10px 2px #ff0000");
    String clearStyle = buildBulkStyleScript("", "");

    for (int i = 0; i < 5; i++) {
      js.executeScript(i % 2 == 0 ? applyStyle : clearStyle, args);
      pause(400);
    }
    js.executeScript(applyStyle, args); // leave borders on
  }

  /**
   * Builds a JS snippet that applies outline + boxShadow to all arguments[0..n].
   */
  private static String buildBulkStyleScript(String outline, String shadow) {
    StringBuilder sb = new StringBuilder();
    sb.append("for(var i=0;i<arguments.length;i++){");
    sb.append("  arguments[i].style.outline='").append(outline).append("';");
    sb.append("  arguments[i].style.boxShadow='").append(shadow).append("';");
    sb.append("}");
    return sb.toString();
  }

  // ── Console helpers ───────────────────────────────────────────────────────

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
