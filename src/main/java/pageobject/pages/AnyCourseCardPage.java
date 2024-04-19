package pageobject.pages;

import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import java.time.LocalDate;

public class AnyCourseCardPage extends AbsBasePage<AnyCourseCardPage> {

  public AnyCourseCardPage(WebDriver driver) {
    super(driver);
  }

  @Step("Проверка названия и описания курса")
  public void checkCourseNameAndDescriptionData() {
    String name = "";

    waiters.presenceOfElementLocated(By.xpath(jivoChatIconLocator));
    try {
      name = fe(By.cssSelector(".sc-1og4wiw-0.sc-s2pydo-1.ifZfhS.diGrSa")).getText();
    } catch (NoSuchElementException e) {
      String nameFailMessage = "Имя карточки курса не найдено!";
      log.info(nameFailMessage);
      Assertions.fail(nameFailMessage);
    }

    try {
      fe(By.cssSelector(".sc-1og4wiw-0.sc-s2pydo-3.gaEufI.dZDxRw")).getText();
    } catch (NoSuchElementException e) {
      String descriptionFailMessage = "Описание курса для карточки '%s' не найдено!";
      log.info(String.format(descriptionFailMessage, name));
      Assertions.fail(String.format(descriptionFailMessage, name));
    }
  }

  public LocalDate getCourseDate() {
    waiters.presenceOfElementLocated(By.xpath(jivoChatIconLocator));
    return dateParser(fe(By.xpath("//div[@class='sc-3cb1l3-4 kGoYMV']//p[substring(text(), string-length(text()) - 0) = 'я' or contains(text(),'та')]")));
  }
}