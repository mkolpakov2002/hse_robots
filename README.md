# Система дистанционного управления роботизированными устройствами
## Описание
ПО может быть востребовано техническими университетами и лабораториями для **облегчения процесса управления роботизированными устройствами** по *протоколу беспроводной передачи данных Bluetooth / WiFi* с открытой **структурой пакета** передаваемых данных. С другой стороны, оно поможет решить проблему **демонстрации работы устройства**.

Приложение разработано **для использования** в домашних и учебных целях **инженерами, студентами, преподавателями и лаборантами**. 

## Функционал приложения
1.	**Подключение по Bluetooth или WiFi** к определенным роботизированным устройствам;
2.	Дистанционное **управление функциями передвижения** устройства;
3.	**Управление несколькими устройствами** одновременно;
4.	Передача, получение и обработка данных данных с **использованием собственной структуры пакета**;
5.	**Добавление** в базу приложения собственного и сторонних **структур пакетов данных**;
6.	**Добавление** в базу приложения **поддерживаемых устройств** и информации о них.

## Технические требования
Приложение может быть использовано на **любом устройстве** (смартфоне или планшете, ноутбуке) с операционной системой Android версии не ниже **9.0 Pie (API 28)** или Windows 11 с установленной **Windows subsystem for android**, обязательно наличие встроенного **Bluetooth или WiFi модуля**.

## Структура файла пакета передачи данных
Для упрощения процесса составления XML кода протокола можно воспользоваться файлом шаблона по [ссылке](https://drive.google.com/file/d/1DgwXtwNb38TRlwDL3d81XfH5eFk_xhqG/view?usp=sharing)

Файл протокола передачи данных имеет **разрешение XML** и состоит из **тегов** участков сообщения, **каждый из которых имеет вложенные теги** с именами и возможными **значениями данного участка в байтах** (шестнадцатеричная система). 

Если какой-либо из участков сообщения **не используется в протоколе**, его **тег не указывается в файле**. **Обязательные теги не опускаются**. 

**Длина участка сообщения** для кодов команд рассчитывается **автоматически** как разность общей длины и введенных компонентов.

В **дефолтном протоколе** длина передаваемого пакета составляет **32 байта**. 
* Первые **4 байта** задают **класс и тип устройства отправления и устройства назначения соответственно**, которыми могут быть Android устройство, компьютер, а также устройства на базе Arduino. 
* Следующий **1 байт** занимает метка, отвечающая за указание какая отправляется команда: **новая или повтор предыдущей**. 
* Далее **1 байт** занимает один из 3 **типов команды**: перемещение, телеметрия и калибровка (калибровка либо установка скорости сервомоторов). 
* **Тип** задается в **старших 4 битах**, а **4-х младших** указывается количество байт **последующих далее данных**. Ими могут быть закодированы команды перемещения, данные телеметрии и калибровки.

## Руководство пользователя

Более подробно можно прочитать в [руководстве пользователя](https://drive.google.com/file/d/1zkhzHQyIvyE6i714tYS-aKuhP0ao2Z2Y/view?usp=sharing)

### Добавление устройства

Для добавления нового роботизированного устройства выберите один из вариантов в нижнем меню. 

По нажатию на кнопку «**ДОБАВИТЬ УСТРОЙСТВА ИЗ СПИСКА СОПРЯЖЕННЫХ**» откроется окно выбора робота для сохранения, список роботов берётся из списка ранее сопряжённых по Bluetooth устройств из настроек Android. 

Если **Bluetooth модуль** вашего робота **доступен для обнаружения** вашим устройством, но его нет в списке ранее сопряжённых, можно перейти в настройки с помощью кнопки "Новое сопряжение", где будет возможность выполнить новое сопряжение.

Если же **робот не доступен для обнаружения** возможен **ручной ввод данных** по нажатию на кнопку «**ДОБАВИТЬ УСТРОЙСТВО**» -> «**ВРУЧНУЮ**». При этом **откроется окно ввода** данных (имени устройства, MAC адреса, Ip) с автоматически примененной маской. Для введения в поле MAC доступны только **заглавные буквы латинского алфавита и цифры**. В поле IP - цифры и точки. В случае неправильного ввода высветится ошибка.

Для ввода обязательно поле **имени устройства**.

### Добавление протокола

Для добавления нового протокола передачи данных необходимо перейти в подпункт **настройки** и нажать на элемент «**Добавить новый протокол**». 

Откроется окно добавления нового протокола. **Ограничения на имя** пустая строка и уникальность, так как в дальнейшем доступ к протоколам происходит только по имени. **Длина сообщения** должна быть не меньше самого длинного возможного сообщения, исходя из введенного кода протокола.

В **поле код** необходимо **ввести протокол передачи данных** в XML формате. Протокол можно **ввести вручную**, а также **прочитать из файла** (доступные расширения .txt и .xml). Для последнего необходимо разрешить приложению **доступ к внутренней памяти**.

### Подключение к устройствам

Для подключения по Bluetooth обязательно наличие MAC адреса у сохранённого в списке устройства. Для подключения по WiFi - Ip и порт.

Подключиться к **одному устройству** возможно по нажатию на кнопку **ПОДКЛЮЧЕНИЕ в меню устройства**, которое открывается по нажатию на плитку с устройством из главного меню. После выбора типа подключения в меню **Выберите тип подключения** начнется процесс подключения. Если подключение **не состоится**, то приложение вернется к главному меню, а по центру экрана появится диалог с сообщением, «**Подключение не успешно**».

Подключаться **одновременно** можно только **к устройствам с одинаковым протоколом передачи данных, а также классом и типом**, **в противном случае** выбор этих устройств одновременно будет **невозможен** и выведется соответствующее сообщение. Далее **для выбора нескольких устройств** для подключения, необходимо **зажать одно** из них на главном экране **до появления галочки** на карточке устройства. **Следующие** устройства можно выбрать **по обычному клику**. **Для старта** подключения необходимо нажать на кнопку внизу экрана **ПОДКЛЮЧЕНИЕ**. 

## Разработчики

*[Максим Колпаков](https://vk.com/mkolpakov2002)* - разработчик на языке высокого уровня

*[Рыкова Татьяна](https://vk.com/id326207212)* - стажёр

*[Лазизбек Камаров](https://vk.com/kamarov11)* - стажёр

*[Анастасия Овчинникова](https://vk.com/n.ovechka)* - работала в 2020-2021, разработчик GUI

На базе УЛ САПР МИЭМ НИУ ВШЭ

