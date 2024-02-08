##language: ru
#
#@main_page_courses_by_date
#Функционал: Поиск курсов, стартующих в указанную дату или позже
#
#  Структура сценария: Найти курсы, стартующие в указанную дату или позже и вывести информацию о них в консоль
#    Пусть Используется браузер <browser>
#    Если Открыта главная страница
#    Тогда Найти курс, стартующий не раньше даты <requiredCourseDate>
#    Примеры:
#      | browser  | requiredCourseDate |
#      | "Chrome" | "11.03.2024"       |