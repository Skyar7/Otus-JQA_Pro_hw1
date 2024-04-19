package pageobject.pages;

import annotations.UrlPrefix;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pageobject.utils.AbsBaseUtils;

public abstract class AbsBasePage<T extends AbsBasePage<T>> extends AbsBaseUtils {

  protected final static String BASE_URL = System.getProperty("base.url");
  protected String jivoChatIconLocator = "//jdiv[@class='iconWrap_f24a']";

  public AbsBasePage(WebDriver driver) {
    super(driver);
  }

  @Step("Открытие страницы")
  public T openPage() {
    driver.get(BASE_URL + getUrlPrefix());
    return (T) this;
  }

  protected void closeCookiesMessage() {
    WebElement cookieMessageButton = fe(By.cssSelector(".sc-9a4spb-0.ckCZjI"));
    moveAndClick(cookieMessageButton);
  }

  private String getUrlPrefix() {
    UrlPrefix urlAnnotation = getClass().getAnnotation(UrlPrefix.class);
    return urlAnnotation.value();
  }
}