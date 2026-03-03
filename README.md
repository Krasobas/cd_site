## cd_site

### Описание проекта

`cd_site` — это **веб-интерфейс** и **gateway** всей экосистемы **CheckDev**.
Он предоставляет пользователям графический доступ к функционалу платформы:

* регистрация и авторизация,
* просмотр интервью, профилей, откликов и уведомлений,
* аналитика вакансий и экзаменов,
* взаимодействие с Telegram и внутренней системой уведомлений.

Сервис построен на **Spring Boot (MVC + WebClient)** и использует **Thymeleaf** (или аналогичный шаблонизатор) для серверной генерации HTML-страниц.
Он является **единым фронтендом** для всех backend-микросервисов CheckDev.


### Основные возможности

* **Единый UI** для всей платформы.
* **Интеграция со всеми микросервисами** через WebClient и Eureka.
* **Регистрация и вход** пользователей.
* **Просмотр и фильтрация интервью, категорий, тем, откликов.**
* **Работа с уведомлениями** и внутренними сообщениями.
* **Просмотр статистики вакансий.**
* **Загрузка и отображение изображений.**
* **Интеграция с Telegram через Notification-сервис.**


### Архитектура проекта

```
ru.checkdev.site/
├── SiteSrv.java                            # Главный класс (Spring Boot entry point)
├── configuration/
│   ├── InterceptorSite.java                # Перехватчики запросов (логирование, аутентификация)
│   ├── WebClientAuthConfig.java            # Конфигурация WebClient для Auth-сервиса
│   └── WebConfig.java                      # MVC и шаблоны (Thymeleaf, статика)
├── component/
│   └── FilterRequestParamsManager.java     # Менеджер параметров фильтрации
├── controller/                             # MVC и REST-контроллеры
│   ├── IndexController.java
│   ├── LoginController.java
│   ├── RegistrationController.java
│   ├── InterviewsController.java
│   ├── FeedbackController.java
│   ├── MessagesController.java
│   ├── ProfilesController.java
│   ├── CategoriesController.java
│   ├── TopicsController.java
│   ├── WisherController.java
│   ├── VacancyStatisticController.java
│   ├── NotificationController.java
│   └── rest/
│       ├── FilterRestController.java
│       ├── InterviewsRestController.java
│       ├── MessagesRestController.java
│       ├── PhotoRestController.java
│       └── TopicsRestController.java
├── domain/                                 # Доменные объекты UI-уровня
│   ├── Category.java
│   ├── Interview.java
│   ├── VacancyStatistic.java
│   ├── FilterProfile.java
│   └── VacancyURL.java
├── dto/                                    # DTO для микросервисов
│   ├── CategoryDTO.java
│   ├── InterviewDTO.java
│   ├── ProfileDTO.java
│   ├── WisherDTO.java
│   ├── FeedbackDTO.java
│   ├── NotificationDTO.java
│   ├── VacancyStatisticWithDates.java
│   └── ...
├── enums/
│   ├── InterviewMode.java
│   └── StatusInterview.java
├── service/                                # Бизнес-логика UI-клиента
│   ├── AuthService.java
│   ├── EurekaUriProvider.java
│   ├── FeedbackServiceWebClient.java
│   ├── WisherServiceWebClient.java
│   ├── NotificationService.java
│   ├── ProfilesService.java
│   ├── VacancyStatisticService.java
│   ├── WebClientAuthCall.java
│   ├── WebClientTopicCall.java
│   └── UriStoreService.java
└── util/                                   # Утилиты
    ├── MultipartFileImpl.java
    ├── RequestResponseTools.java
    ├── RestAuthCall.java
    └── RestPageImpl.java
```

### Технологический стек

| Компонент                  | Назначение                           |
| -------------------------- | ------------------------------------ |
| **Java 17+**               | Язык реализации                      |
| **Spring Boot**            | Основной фреймворк                   |
| **Spring MVC + Thymeleaf** | Отрисовка страниц                    |
| **Spring WebClient**       | REST-взаимодействие с микросервисами |
| **Spring Security**        | Аутентификация и авторизация         |
| **Eureka Client**          | Сервис-дискавери                     |
| **Liquibase**              | Миграции базы данных                 |
| **PostgreSQL**             | Основная БД                          |
| **Maven**                  | Система сборки                       |
| **Jenkins**                | CI/CD                                |
| **HTML / CSS / JS**        | Вёрстка фронтенда                    |

---

### Конфигурация приложения

Пример `src/main/resources/application.properties`:

```properties
spring.application.name=site
server.port=9015

spring.datasource.url=jdbc:postgresql://localhost:5432/cd_site
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=validate
spring.liquibase.change-log=classpath:db/db.changelog-master.xml

# Eureka discovery
eureka.client.service-url.defaultZone=http://localhost:9009/eureka

# Сервисы CheckDev
checkdev.auth.url=http://localhost:9010
checkdev.desc.url=http://localhost:9011
checkdev.generator.url=http://localhost:9012
checkdev.notification.url=http://localhost:9014
checkdev.mock.url=http://localhost:9013
```

---

### HTML-шаблоны

В каталоге `src/main/resources/templates` находятся страницы:

| Файл                | Назначение               |
| ------------------- | ------------------------ |
| `index.html`        | Главная страница         |
| `login.html`        | Страница входа           |
| `registration.html` | Регистрация пользователя |

Изображения (например, `person_mock.jpeg`) хранятся в `src/main/resources/img`.


### REST и MVC API

#### MVC-контроллеры

* `/login` — вход пользователя
* `/registration` — регистрация
* `/interviews` — просмотр интервью
* `/categories` — список категорий
* `/notifications` — уведомления
* `/feedback` — отзывы
* `/profile` — просмотр профиля

#### REST API

* `/api/interviews` — получение интервью (AJAX)
* `/api/topics` — темы и категории
* `/api/messages` — внутренние сообщения
* `/api/photos` — изображения профилей
* `/api/filters` — фильтры и параметры поиска

---

### Интеграция с микросервисами CheckDev

| Сервис                    | Порт | Назначение                |
| ------------------------- | ---- | ------------------------- |
| **checkdev_auth**         | 9010 | Авторизация, регистрация  |
| **checkdev_desc**         | 9011 | Категории и темы          |
| **checkdev_generator**    | 9012 | Вакансии и статистика     |
| **checkdev_mock**         | 9013 | Заглушка для тестирования |
| **checkdev_notification** | 9014 | Уведомления и Telegram    |
| **cd_eureka**             | 9009 | Сервис-дискавери          |

Все взаимодействия выполняются через `EurekaUriProvider` и `WebClient`.


### Как запустить проект локально

#### 1. Сборка

```bash
mvn clean package
```

#### 2. Запуск

```bash
java -jar target/cd_site-0.0.1-SNAPSHOT.jar
```

или:

```bash
mvn spring-boot:run
```

#### 3. Доступ

После запуска интерфейс доступен по адресу:
[http://localhost:9015](http://localhost:9015)


### Dockerfile (пример)

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/cd_site-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9015
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Сборка:

```bash
docker build -t checkdev-site .
```

Запуск:

```bash
docker run -p 9015:9015 checkdev-site
```


### Jenkins (CI/CD)

`Jenkinsfile` реализует типовой pipeline:

1. **Build** — сборка через Maven
2. **Test** — прогон юнит-тестов
3. **Package** — создание артефакта
4. **Deploy** — публикация (в Docker или Kubernetes)
