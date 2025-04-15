# Articles

**Articles** — это веб-приложение, которое генерирует фейковые данные в базе данных и предоставляет веб-интерфейс для управления ими (редактирование, удаление, добавление). Сайт поддерживает несколько ролей пользователей: неавторизованные, авторизованные и администраторы. Каждая роль обладает своими правами. Например:

- **Неавторизованные пользователи** могут только просматривать статьи.  
- **Авторизованные пользователи** могут создавать, редактировать и удалять собственные статьи, а также добавлять новые теги.  
- **Администратор** имеет расширенные полномочия, включая удаление других пользователей вместе со всеми их статьями и комментариями.

---

## Структура проекта

### Config

- **InsertData**  
  Модуль для генерации и заполнения базы данных фейковыми данными при запуске. Фейковые данные создаются с помощью [JavaFaker](https://github.com/DiUS/java-faker) и сохраняются в нужном формате. Параллельно происходит шифрование паролей для обеспечения безопасности.

- **SecurityConfig**  
  Модуль, управляющий процессом входа и регистрации пользователей. Он ограничивает доступ неавторизованных пользователей, разрешая им переходить лишь на страницы со статьями, а также на страницы авторизации и регистрации. Здесь определяется и используется механизм шифрования паролей.

---

### Controllers

- **ArticleController**  
  Обеспечивает связь между `ArticleService` и пользователем, позволяя создавать, редактировать, удалять и просматривать статьи.

- **LoginController**  
  Отвечает за отображение формы входа (login.html) и обработку процесса авторизации пользователя.

- **RegistrationController**  
  Отвечает за отображение формы регистрации, создание нового пользователя и последующее перенаправление на страницу входа (login.html).

- **TagController**  
  Предоставляет функционал для создания, просмотра, редактирования и удаления тегов.

- **UserController**  
  Позволяет администрировать пользователей: создавать, отображать, редактировать и удалять их. Администратор имеет право удалять пользователей вместе со всеми зависимостями (статьи, комментарии и т.д.).

---

### Details

- **CustomUserDetails**  
  Класс, реализующий интерфейс `UserDetails` (Spring Security). Он используется системой безопасности для получения информации об учетной записи пользователя (логин, пароль, роли и т.д.) и проверки этих данных при авторизации.

---

### Entities

- **Article**  
  Представляет статью с полями: заголовок, описание, текст статьи, даты создания и обновления, а также ссылки на автора и теги.

- **ArticleComment**  
  Модель комментария к статье, содержащая текст, дату создания и связь с конкретной статьёй и пользователем.

- **ArticleFavorite**  
  Сущность, реализующая механизм «избранного» или лайков. Связывает пользователя со статьёй, которую он отметил как избранную.

- **Author**  
  Дополнительная сущность для представления информации об авторе (например, для внешних источников или детального описания). Содержит имя, биографию и другие связанные данные.

- **Tag**  
  Сущность, описывающая тег или категорию для статей. Позволяет группировать статьи по темам, упрощая поиск и фильтрацию.

- **User**  
  Содержит информацию о пользователе системы: логин, зашифрованный пароль, email, роль, биографию, аватар и связи с другими сущностями (например, статьи, комментарии, избранное).

---

### Repositories

- **ArticleRepository**  
  Интерфейс для работы со статьями, реализующий операции CRUD и дополнительные методы (поиск по ключевым словам, фильтрация по автору или тегу, получение списка статей, отсортированного по дате создания).

- **ArticleCommentRepository**  
  Предоставляет методы для работы с комментариями к статьям (поиск, сохранение, удаление).

- **ArticleFavoriteRepository**  
  Предоставляет методы для создания, поиска и удаления связей «избранного» между пользователями и статьями.

- **AuthorRepository**  
  Методы для управления данными об авторах (создание, редактирование, удаление, поиск).

- **TagRepository**  
  Интерфейс для операций с тегами: создание, поиск, обновление и удаление.

- **UserRepository**  
  Отвечает за операции с пользователями: регистрация, поиск по логину и другие действия, связанные с учетными записями.

---

### Services

- **ArticleService / ArticleServiceImpl**  
  Реализуют бизнес-логику для работы со статьями: создание, редактирование, удаление, а также поиск по тегам и авторам.

- **AuthorService / AuthorServiceImpl**  
  Содержат логику для управления информацией об авторах (например, имена и биографии).

- **CustomUserDetailsService**  
  Реализует интерфейс `UserDetailsService` Spring Security, загружая данные пользователя (логин, пароль, роли) для аутентификации.

- **TagService / TagServiceImpl**  
  Отвечают за операции с тегами (CRUD), их привязку к статьям и другие связанные задачи.

- **UserService / UserServiceImpl**  
  Предоставляют бизнес-логику для управления пользователями: создание, редактирование, удаление, поиск по логину и т.д.

---

### Main Class

- **ArticlesApplication**  
  Главный класс, запускающий приложение Spring Boot.

---

## Использование JavaFaker

В проекте для генерации фейковых данных используется библиотека [JavaFaker](https://github.com/DiUS/java-faker). Модуль **InsertData** создает реалистичные тестовые данные (пользователи, статьи, комментарии, теги), что позволяет протестировать функционал приложения без ручного ввода данных.

---

## Как запустить проект

1. **Установка XAMPP и настройка базы данных:**
   - Скачайте и установите [XAMPP](https://www.apachefriends.org/ru/index.html).
   - Запустите XAMPP, убедитесь, что запущены службы Apache и MySQL.
   - Откройте консоль MySQL (или phpMyAdmin) и создайте пользователя базы данных, выполнив следующие SQL-команды:
     ```sql
     CREATE USER 'nptv23soap'@'localhost' IDENTIFIED BY 'nptv23soap';
     GRANT ALL PRIVILEGES ON new_articles.* TO 'nptv23soap'@'localhost';
     FLUSH PRIVILEGES;
     ```
     Эта команда создаст нового пользователя и выдаст ему все привилегии на схему `new_articles`.

2. **Скачивание и запуск проекта:**
   - Скачайте код проекта с GitHub и разархивируйте архив в удобное для вас место.
   - Откройте проект в вашей любимой IDE (например, IntelliJ IDEA или Eclipse).
   - Убедитесь, что в настройках Tomcat (или встроенного сервера Spring Boot) указан правильный порт (например, `8080`).
   - Запустите приложение:
     - Если вы используете Maven, запустите команду:
       ```bash
       mvn spring-boot:run
       ```
     - Либо запустите главный класс `ArticlesApplication` через IDE.

3. **Доступ к веб-интерфейсу:**
   - После успешного запуска приложения откройте браузер и перейдите по адресу:
     ```
     http://localhost:8080/login
     ```
   - Если порт отличается, используйте соответствующий URL (например, `http://localhost:<PORT>/login`).

4. **Авторизация:**
   - Чтобы войти в систему, зарегистрируйте нового пользователя через форму регистрации.
   - Для входа под учетной записью администратора используйте следующие данные:
     - **Пользователь**: `admin`
     - **Пароль**: `123`
