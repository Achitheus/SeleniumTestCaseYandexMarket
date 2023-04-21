## Автоматизация тест-кейса ЯндексМаркета

### Тест-кейс
1. Открыть браузер и развернуть на весь экран.
2. Зайти на https://ya.ru/
3. Нажать слева внизу на значок "все сервисы" -> Кликнуть по "Маркет"
4. Перейти в Каталог -> Навести курсор на раздел "Ноутбуки и компьютеры"
5. Выбрать раздел "Ноутбуки"
6. Задать параметр «Цена, Р» от 10000 до 900000 рублей.
7. Выбрать производителя "Huawei" и "Lenovo"
8. Дождаться результатов поиска.
9. Проверить, что на странице отображалось более 12 элементов.
10. Проверить что на всех страницах предложения соответствуют фильтру.
11. Вернуться на первую страницу с результатами поиска ноутбуков и запомнить первое наименование ноутбука.
12. В поисковую строку ввести запомненное значение.
13. Нажать кнопку «Найти»
14. Проверить, что в результатах поиска есть искомый товар <br>
<br>Автотест необходимо написать, используя данный стек: Java, Junit Jupiter, Selenium, PageObject или PageFactory <br>
 ### Требования к тесту:
- Тест разбит на шаги. Коллега без знаний программирования по аллюр отчету должен понимать что делает тест, какие проверки происходят
- Тест должен быть параметризован
- Необходимо использовать константы через проперти файл
- Все ассерты должны быть переопределены
- Трай\кетчи не должны быть использованы для реализации логики
- Если в коде используются циклы, необходимо исключить возможность бесконечного цикла
- Обязательно использовать Джава док для всех методов и переменных. На русском языке
- Недопустимо использования Thread.sleep и Трай\кетчи. За исключением, создания собственных ожиданий (к примеру каждый 5 миллисекунд проверяем что что-то случилось, и так не более 10 секунд.). Лучше обойтись явными\неявными ожиданиями
- Помнить про универсальные методы. Писать код, полезный в других тестах, полезный коллегам.
- XPath не должен содержать индексов, динамических элементов
### Советы:
- Если яндекс маркет лочит - используйте прокси на всю винду
- Если с данными товарами тест пройти не получается, выберите других производителей, а перед сдачей верните фильтры согласно заданию
