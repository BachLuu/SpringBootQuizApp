# SpringBootQuizApp — Enterprise Quiz Management Backend

Backend Spring Boot cho hệ thống quản lý Quiz theo hướng “enterprise-ready”: REST APIs, phân quyền, JWT stateless, PostgreSQL, và tài liệu API tự động.

## Highlights

- Kiến trúc rõ ràng theo mô hình nhiều lớp: `Controller → Service (interface/impl) → Repository → DB`.
- Bảo mật Spring Security (Stateless) + JWT (Nimbus JOSE JWT) với cookie `HttpOnly` cho access/refresh token.
- DTO thiết kế hiện đại bằng Java `record` + mapping bằng MapStruct để tách biệt domain/entity và API contract.
- PostgreSQL schema `dbo` + quản lý migration bằng Liquibase (changelog theo ngày).
- Swagger/OpenAPI UI sẵn để demo và test API.

## Tech Stack

- **Language/Runtime**: Java 25
- **Framework**: Spring Boot 4.0.1 (WebMVC)
- **Security**: Spring Security + JWT (Nimbus JOSE JWT), BCrypt password hashing
- **Data**: Spring Data JPA + PostgreSQL
- **DB Migration**: Liquibase
- **API Docs**: SpringDoc OpenAPI (Swagger UI)
- **Mapping/Boilerplate**: MapStruct, Lombok
- **Observability**: Spring Boot Actuator, logback

## Chức năng chính

- **Auth**: đăng ký, đăng nhập, refresh token, logout
- **RBAC**: phân quyền theo role (ví dụ `@PreAuthorize(...)` cho admin endpoints)
- **Quiz Domain**: quản lý quiz / câu hỏi / đáp án / user / role (CRUD theo controller/service)
- **CORS config**: hỗ trợ frontend chạy local (ví dụ Vite `localhost:5173`)

## Kiến trúc & cấu trúc thư mục

Project đi theo layered architecture và tách lớp rõ ràng để dễ bảo trì/mở rộng:

- `src/main/java/com/example/springbootweb/controllers`: REST Controllers
- `src/main/java/com/example/springbootweb/services/interfaces`: Service contracts
- `src/main/java/com/example/springbootweb/services/impl`: Business logic
- `src/main/java/com/example/springbootweb/repositories`: Spring Data JPA repositories
- `src/main/java/com/example/springbootweb/entities/models`: JPA entities
- `src/main/java/com/example/springbootweb/entities/dtos`: DTOs (đa số là Java `record`)
- `src/main/java/com/example/springbootweb/services/jwt`: JWT utilities + filter
- `src/main/resources/db/changelog`: Liquibase changelog files

## Authentication flow (tóm tắt)

- Login thành công sẽ trả về `accessToken` + `refreshToken` và đồng thời set cookie `HttpOnly`.
- Mọi request bảo vệ sẽ đi qua `JwtAuthenticationFilter` để đọc access token từ cookie và set `SecurityContext`.
- Khi access token hết hạn, gọi refresh endpoint để cấp access token mới dựa trên refresh token.

## Run local

### 1) Prerequisites

- Java 25
- PostgreSQL (tạo DB `QuizSpringDB`, schema `dbo`)

### 2) Cấu hình môi trường

Ứng dụng đọc cấu hình từ `src/main/resources/application.yaml` và có thể override bằng env vars:

- `DB_USERNAME` (mặc định: `postgres`)
- `DB_PASSWORD`
- `JWT_SECRET` (khuyến nghị >= 32 ký tự)

Ví dụ chạy nhanh:

```bash
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
export JWT_SECRET='CHANGE_ME__MIN_32_CHARS'
./mvnw spring-boot:run
```

Mặc định chạy ở `http://localhost:8080`.

### 3) Swagger UI

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Database & migrations

- PostgreSQL được cấu hình với `currentSchema=dbo`.
- Liquibase changelog nằm trong `src/main/resources/db/changelog`.

## License

MIT
