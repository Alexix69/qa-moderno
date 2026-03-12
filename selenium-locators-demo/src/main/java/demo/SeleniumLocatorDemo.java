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
  private static final int STEP_PAUSE = 9000;

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
                     + "WebElement = referencia de Selenium a un nodo del DOM.\n"
                     + "findElement → retorna UN solo elemento\n"
                     + "             o lanza NoSuchElementException.";
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
      transition(driver, "[DEMO 2] Locator hierarchy");
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
            "Localizando el mismo elemento de cuatro formas:\n\n  → " + locatorLabels[i]);
        WebElement el = driver.findElement(locators[i]);
        highlightElement(driver, el);
        pause(800);
      }
      pause(STEP_PAUSE);

      // ── DEMO 3 ─────────────────────────────────────────────────────────────
      // Fragile vs robust selector.
      // ───────────────────────────────────────────────────────────────────────
      transition(driver, "[DEMO 3] Fragile vs robust selector");
      driver.get("https://the-internet.herokuapp.com/login");
      createOverlay(driver);

      String d3Title = "[DEMO 3] Fragile vs robust selector";
      banner(d3Title);

      // Show the FRAGILE locator — an absolute XPath that currently works
      // but breaks if any surrounding element is added, removed, or reordered.
      String fragileXPath = "/html/body/div[2]/div/div/form/div/input";
      String fragileMsg = "DEMO 3 — SELECTOR FRÁGIL\n\n"
                        + "XPath: /html/body/div[2]/div/div/form/div/input\n\n"
                        + "El XPath absoluto depende de la estructura del DOM.\n"
                        + "Cambios pequeños en el layout pueden romper este localizador.";
      System.out.println("  [FRAGILE]  By.xpath(\"" + fragileXPath + "\")");
      System.out.println("             Absolute path — depends entirely on DOM structure.");
      System.out.println("             Any layout change will silently break this locator.");
      updateOverlay(driver, d3Title, fragileMsg);
      try {
        WebElement fragile = driver.findElement(By.xpath(fragileXPath));
        highlightElement(driver, fragile);
      } catch (Exception ex) {
        System.out.println("  [FRAGILE]  XPath did not match — page structure may have changed.");
      }
      pause(STEP_PAUSE);

      // Now locate the same element with a robust, semantic locator.
      String robustMsg = "SELECTOR ROBUSTO\n\n"
                       + "By.id(\"username\")\n\n"
                       + "Usar un atributo estable (id) hace al localizador\n"
                       + "legible, confiable y fácil de mantener.";
      System.out.println("  [ROBUST]   By.id(\"username\") — semantic, stable, independent of DOM structure.");
      updateOverlay(driver, d3Title, robustMsg);
      WebElement robust = driver.findElement(By.id("username"));
      highlightElement(driver, robust);
      pause(STEP_PAUSE);

      // ── DEMO 4 ─────────────────────────────────────────────────────────────
      // findElements: returns a List — empty if nothing matches, never throws.
      // SelectorsHub practice page has many inputs: ideal for this concept.
      // ───────────────────────────────────────────────────────────────────────
      transition(driver, "[DEMO 4] findElements — multiple nodes");
      driver.get("https://selectorshub.com/xpath-practice-page/");
      createOverlay(driver);

      String d4Title = "[DEMO 4] findElements — multiple nodes";
      banner(d4Title);
      System.out.println("  driver.findElements(By.tagName(\"input\"))");
      System.out.println("  Returns List<WebElement> — empty list if none found (never throws).");
      updateOverlay(driver, d4Title,
          "driver.findElements(By.tagName(\"input\"))\n\n"
        + "Retorna List<WebElement>.\n"
        + "Lista vacía si no hay coincidencias (nunca lanza excepción).\n\n"
        + "Contando y resaltando TODOS los inputs...");

      List<WebElement> inputs = driver.findElements(By.tagName("input"));
      System.out.println("  Inputs found on this page: " + inputs.size());
      updateOverlay(driver, d4Title,
          "findElements(By.tagName(\"input\"))\n\n"
        + "Encontrados: " + inputs.size() + " elementos input\n\n"
        + "Resaltando TODAS las coincidencias simultáneamente...");

      // Highlight every matched element at once via a single JS call.
      highlightAll(driver, inputs);
      pause(STEP_PAUSE);

      banner("Demo complete — browser closing.");
      updateOverlay(driver, "Demo completado", "¡Gracias!\n\nCerrando el navegador...");
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
        // Inject pulse keyframe animation (once per page load).
        "if (!document.getElementById('__selenium_demo_style__')) {" +
        "  var st = document.createElement('style');" +
        "  st.id = '__selenium_demo_style__';" +
        "  st.textContent = '@keyframes seleniumPulse {" +
        "    0%   { box-shadow: 0 0 5px 2px #ff0000; }" +
        "    50%  { box-shadow: 0 0 22px 8px #ff0000; }" +
        "    100% { box-shadow: 0 0 5px 2px #ff0000; }" +
        "  }';" +
        "  document.head.appendChild(st);" +
        "}" +
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
   * Applies a persistent CSS pulse animation to an element.
   * The red glow remains active for the entire duration of the demo step.
   */
  private static void highlightElement(WebDriver driver, WebElement element) {
    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript(
        "arguments[0].style.outline = '3px solid #ff0000';" +
        "arguments[0].style.animation = 'seleniumPulse 1s ease-in-out infinite';",
        element);
  }

  /**
   * Applies a persistent CSS pulse animation to ALL matched elements simultaneously.
   * Every element keeps its red glow for the entire duration of the demo step.
   */
  private static void highlightAll(WebDriver driver, List<WebElement> elements) {
    if (elements.isEmpty()) return;
    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript(
        "for (var i = 0; i < arguments.length; i++) {" +
        "  arguments[i].style.outline = '3px solid #ff0000';" +
        "  arguments[i].style.animation = 'seleniumPulse 1s ease-in-out infinite';" +
        "}",
        elements.toArray());
  }

  // ── Console helpers ───────────────────────────────────────────────────────

  /**
   * Displays a "NEXT DEMONSTRATION" transition message in the overlay
   * and pauses briefly before the next demo begins.
   * Safe to call before driver.get() — uses the previous page's overlay.
   */
  private static void transition(WebDriver driver, String nextTitle) {
    try {
      updateOverlay(driver,
          "SIGUIENTE DEMOSTRACIÓN",
          "A continuación:\n" + nextTitle);
    } catch (Exception ignored) {
      // Overlay may not exist on the very first call — that is fine.
    }
    pause(2000);
  }

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
