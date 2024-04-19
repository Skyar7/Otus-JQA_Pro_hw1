package pageobject.pages;

import io.qameta.allure.Step;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import annotations.UrlPrefix;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@UrlPrefix("/")
public class MainPage extends AbsBasePage<MainPage> {
  private LocalDate earliestCourseTileDate;
  private LocalDate latestCourseTileDate;

  public MainPage(WebDriver driver) {
    super(driver);
  }

  @Step("Фильтрация и открытие курса по названию")
  public void filterAndOpenCourseByName(String requiredCourseName) {
    String templCourseNameLocator = "//h5[contains(text(),'%s')]";

    waiters.presenceOfElementLocated(By.xpath(jivoChatIconLocator));
    List<WebElement> filteredByNameCourses = fes(By.xpath(String.format(templCourseNameLocator, requiredCourseName)));
    log.info(String.format("Найдено курсов, по запросу '%s': %d.", requiredCourseName, filteredByNameCourses.size()));

    if (filteredByNameCourses.isEmpty()) {
      String noCoursesForCheckingFailMessage = "Нет курсов для проверки!";
      log.info(noCoursesForCheckingFailMessage);
      Assertions.fail(noCoursesForCheckingFailMessage);
    } else {
      Random random = new Random();
      WebElement chosenCourse = filteredByNameCourses.get(random.nextInt(filteredByNameCourses.size()));
      if (filteredByNameCourses.size() > 1) {
        log.info(String.format("Из найденных курсов, для проверки случайно был выбран '%s'.", chosenCourse.getText()));
      }

      try {
        moveAndClick(chosenCourse);
      } catch (ElementClickInterceptedException e) {
        closeCookiesMessage();
        moveAndClick(chosenCourse);
      }
    }
  }

  public MainPage choiceEarliestCourse() {
    Map<WebElement, LocalDate> tilesDateMap = this.getTilesElementsWithLocalDate();

    Optional<Map.Entry<WebElement, LocalDate>> earliestEntry = tilesDateMap.entrySet().stream()
            .reduce((entry1, entry2) -> entry1.getValue().isBefore(entry2.getValue()) ? entry1 : entry2);

    Map<WebElement, LocalDate> earlistCourseMap = earliestEntry.map(entry -> {
      Map<WebElement, LocalDate> mapWithLatestDate = new HashMap<>();
      mapWithLatestDate.put(entry.getKey(), entry.getValue());
      return mapWithLatestDate;
    }).orElseGet(HashMap::new);

    this.earliestCourseTileDate = earlistCourseMap.values().iterator().next();
    log.info(String.format("Cамый ранний курс начинается %s", this.earliestCourseTileDate));

    try {
      moveAndClick(earlistCourseMap.keySet().iterator().next());
    } catch (ElementClickInterceptedException e) {
      closeCookiesMessage();
      moveAndClick(earlistCourseMap.keySet().iterator().next());
    }

    return this;
  }

  public MainPage choiceLatestCourse() {
    Map<WebElement, LocalDate> tilesDateMap = this.getTilesElementsWithLocalDate();

    Optional<Map.Entry<WebElement, LocalDate>> latestEntry = tilesDateMap.entrySet().stream()
            .reduce((entry1, entry2) -> entry1.getValue().isAfter(entry2.getValue()) ? entry1 : entry2);

    Map<WebElement, LocalDate> latestCourseMap = latestEntry.map(entry -> {
      Map<WebElement, LocalDate> mapWithLatestDate = new HashMap<>();
      mapWithLatestDate.put(entry.getKey(), entry.getValue());
      return mapWithLatestDate;
    }).orElseGet(HashMap::new);

    this.latestCourseTileDate = latestCourseMap.values().iterator().next();
    log.info(String.format("Cамый поздний курс начинается %s", this.latestCourseTileDate));

    try {
      moveAndClick(latestCourseMap.keySet().iterator().next());
    } catch (ElementClickInterceptedException e) {
      closeCookiesMessage();
      moveAndClick(latestCourseMap.keySet().iterator().next());
    }

    return this;
  }

  public void checkEarliestCourseDateOnTileAndOnPage() {
    Assertions.assertEquals(this.earliestCourseTileDate, new AnyCourseCardPage(driver).getCourseDate());
  }

  public void checkLatestCourseDateOnTileAndOnPage() {
    Assertions.assertEquals(this.latestCourseTileDate, new AnyCourseCardPage(driver).getCourseDate());
  }

  public void findRequiredOrLaterDateCourse(String requiredCourseDateStr) {
    LocalDate requiredCourseDate = LocalDate.parse(requiredCourseDateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    Map<WebElement, LocalDate> originalMap = getTilesElementsWithLocalDate();

    Map<WebElement, LocalDate> filteredMap = originalMap.entrySet().stream()
            .filter(entry -> entry.getValue().isEqual(requiredCourseDate) || entry.getValue().isAfter(requiredCourseDate))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    if (!filteredMap.isEmpty()) {
      log.info(String.format("Курсы, которые начинаются не раньше %s: ", requiredCourseDateStr));
      filteredMap.forEach((key, value) -> {

        // Поиск вверх по дереву атрибута, содержащего ссылку на страницу курса.
        WebElement parentElement = key;
        String hrefValue = null;

        while (parentElement != null) {
          hrefValue = parentElement.getAttribute("href");
          if (hrefValue != null) {
            break;
          }
          parentElement = parentElement.findElement(By.xpath(".."));
        }

        // Использование найденной ссылки для jsoup парсера и вывода названия курса
        try {
          Document doc = Jsoup.connect(hrefValue).get();
          Elements nameCourse = doc.select(".sc-1og4wiw-0.sc-s2pydo-1.ifZfhS.diGrSa");
          log.info("Название: " + nameCourse.get(0).text());
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
        log.info("Дата старта: " + value.toString());
      });
    } else {
      log.info(String.format("Курсов, которые начинаются не раньше %s не найдено! ", requiredCourseDateStr));
    }
  }

  private Map<WebElement, LocalDate> getTilesElementsWithLocalDate() {

    // Плитки на главной странице двух типов, с разными локаторами.
    String firstTypeTilesLocator = "//span[@class='sc-1pljn7g-3 cdTYKW' and contains(text(), 'С ')]";
    String secondTypeTilesSelector = ".sc-12yergf-7.dPBnbE";

    waiters.presenceOfElementLocated(By.xpath(jivoChatIconLocator));
    List<WebElement> tilesList = fes(By.xpath(firstTypeTilesLocator));
    tilesList.addAll(fes(By.cssSelector(secondTypeTilesSelector)));

    Map<WebElement, LocalDate> tilesDateMap = new HashMap<>();
    for (WebElement element : tilesList) {
      LocalDate date = dateParser(element);
      tilesDateMap.put(element, date);
    }

    return tilesDateMap;
  }
}