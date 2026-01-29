# ☕ Java & Spring Framework Skills

## 📋 Table of Contents

- [1. Layered Architecture](#1-layered-architecture)
- [2. Dependency Injection](#2-dependency-injection)
- [3. Service Layer Pattern](#3-service-layer-pattern)
- [4. Transaction Management](#4-transaction-management)
- [5. Exception Handling](#5-exception-handling)
- [6. Configuration Management](#6-configuration-management)
- [7. Data Mapping với MapStruct](#7-data-mapping-với-mapstruct)
- [8. Repository Pattern](#8-repository-pattern)
- [9. Controller Best Practices](#9-controller-best-practices)
- [10. Security Integration](#10-security-integration)
- [11. Interface-Driven Controller Pattern](#11-interface-driven-controller-pattern)
- [12. Interface-Based Projections for Repositories](#12-interface-based-projections-for-repositories)
- [13. Enum with Business Logic](#13-enum-with-business-logic)
- [14. JPA Specifications for Dynamic Filtering](#14-jpa-specifications-for-dynamic-filtering)

---

## 1. Layered Architecture

### Overview

Project sử dụng **Layered Architecture** với flow:

```
Controller → Service Interface → Service Implementation → Repository → Database
```

### Layer Responsibilities

| Layer                 | Responsibility                     | Example                   |
| --------------------- | ---------------------------------- | ------------------------- |
| **Controller**        | HTTP handling, validation, routing | `QuizController.java`     |
| **Service Interface** | Contract definition                | `IQuizService.java`       |
| **Service Impl**      | Business logic                     | `QuizService.java`        |
| **Repository**        | Data access                        | `QuizRepository.java`     |
| **Entity**            | Data model                         | `Quiz.java`               |
| **DTO**               | Data transfer                      | `QuizDetailResponse.java` |

### Code Example

```java
// 1. Controller Layer
@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {
    private final IQuizService quizService;  // Interface, không phải Implementation

    @GetMapping("/{id}")
    public ResponseEntity<QuizDetailResponse> getQuizById(@PathVariable UUID id) {
        QuizDetailResponse quiz = quizService.getQuizById(id);
        return ResponseEntity.ok(quiz);
    }
}

// 2. Service Interface
public interface IQuizService {
    QuizDetailResponse getQuizById(UUID id);
    QuizDetailResponse createQuiz(CreateQuizRequest request);
}

// 3. Service Implementation
@Service
@RequiredArgsConstructor
public class QuizService implements IQuizService {
    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;

    @Override
    @Transactional(readOnly = true)
    public QuizDetailResponse getQuizById(UUID id) {
        Quiz quiz = quizRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Quiz not found: " + id));
        return quizMapper.toResponse(quiz);
    }
}
```

### 📌 Best Practice

- Controller **KHÔNG chứa** business logic
- Service **KHÔNG return** Entity, phải map sang DTO
- Repository **CHỈ làm** data access

---

## 2. Dependency Injection

### Constructor Injection (Recommended ✅)

```java
@Service
@RequiredArgsConstructor  // Lombok tự generate constructor
public class QuizService implements IQuizService {
    // Tất cả fields là 'final' → required dependency
    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;
    private final QuestionService questionService;
}

// Lombok generate:
// public QuizService(QuizRepository repo, QuizMapper mapper, QuestionService service) {
//     this.quizRepository = repo;
//     this.quizMapper = mapper;
//     this.questionService = service;
// }
```

### ❌ Avoid: Field Injection

```java
// KHÔNG NÊN dùng @Autowired trên field
@Service
public class QuizService {
    @Autowired  // ❌ Khó test, hidden dependency
    private QuizRepository quizRepository;
}
```

### Tại sao Constructor Injection tốt hơn?

1. **Immutability** - dependencies là `final`, không thể thay đổi
2. **Required Dependencies** - biết ngay dependency nào cần thiết
3. **Testability** - dễ mock khi viết unit test
4. **No Reflection** - không cần Spring context để instantiate

---

## 3. Service Layer Pattern

### Interface + Implementation

```java
// 📁 services/interfaces/IUserService.java
public interface IUserService {
    UserDetailResponse getUserById(UUID id);
    List<UserSummaryResponse> getAllUsers();
    UserDetailResponse createUser(CreateUserRequest request);
    UserDetailResponse updateUser(UUID id, UpdateUserRequest request);
    void deleteUser(UUID id);
}

// 📁 services/impl/UserService.java
@Service
@RequiredArgsConstructor
@Slf4j  // Lombok logger
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetailResponse getUserById(UUID id) {
        log.info("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        return userMapper.toDetailResponse(user);
    }

    @Override
    @Transactional
    public UserDetailResponse createUser(CreateUserRequest request) {
        log.info("Creating new user: {}", request.getEmail());

        // Business validation
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        // Entity creation
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Save and return
        User savedUser = userRepository.save(user);
        log.info("User created with id: {}", savedUser.getId());

        return userMapper.toDetailResponse(savedUser);
    }
}
```

### 📌 Service Layer Rules

1. **Always return DTOs** - không leak Entity ra ngoài
2. **Handle business exceptions** - throw custom exceptions
3. **Log important operations** - dùng slf4j
4. **Validate business rules** - sau validation layer

---

## 4. Transaction Management

### Basic Annotations

```java
@Service
@RequiredArgsConstructor
public class AnswerService implements IAnswerService {

    // READ operation - readOnly = true (performance optimization)
    @Transactional(readOnly = true)
    public List<AnswerSummaryResponse> getAllAnswers() {
        return answerRepository.findAll()
            .stream()
            .map(answerMapper::toSummary)
            .toList();
    }

    // WRITE operation - default propagation
    @Transactional
    public AnswerResponse createAnswer(CreateAnswerRequest request) {
        Answer answer = answerMapper.toEntity(request);
        Answer saved = answerRepository.save(answer);
        return answerMapper.toResponse(saved);
    }

    // Complex operation with rollback
    @Transactional(rollbackFor = Exception.class)
    public void batchCreateAnswers(List<CreateAnswerRequest> requests) {
        for (CreateAnswerRequest request : requests) {
            Answer answer = answerMapper.toEntity(request);
            answerRepository.save(answer);
        }
    }
}
```

### Transaction Propagation

| Propagation          | Behavior                                    |
| -------------------- | ------------------------------------------- |
| `REQUIRED` (default) | Join existing or create new                 |
| `REQUIRES_NEW`       | Always create new, suspend existing         |
| `SUPPORTS`           | Join if exists, non-transactional otherwise |
| `NOT_SUPPORTED`      | Execute non-transactionally                 |

### Example: Nested Transactions

```java
@Service
public class QuizService {

    @Transactional
    public void createQuizWithQuestions(CreateQuizWithQuestionsRequest request) {
        // Outer transaction
        Quiz quiz = quizRepository.save(mapToQuiz(request));

        // Call another transactional method
        questionService.createQuestions(quiz.getId(), request.getQuestions());
        // If this fails, entire transaction rolls back
    }
}

@Service
public class QuestionService {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createQuestions(UUID quizId, List<CreateQuestionRequest> questions) {
        // New transaction - independent of outer
        for (CreateQuestionRequest q : questions) {
            questionRepository.save(mapToQuestion(quizId, q));
        }
    }
}
```

---

## 5. Exception Handling

### Custom Exceptions

```java
// 📁 exceptions/ResourceNotFoundException.java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

// 📁 exceptions/BadRequestException.java
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}

// 📁 exceptions/ErrorResponse.java
@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
```

### Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        log.error("Resource not found: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Not Found")
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            BadRequestException ex, WebRequest request) {
        log.error("Bad request: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Bad Request")
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", 400);
        response.put("errors", errors);

        return ResponseEntity.badRequest().body(response);
    }

    // Fallback for all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, WebRequest request) {
        log.error("Unexpected error", ex);

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Internal Server Error")
            .message("An unexpected error occurred")
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

---

## 6. Configuration Management

### application.yaml Structure

```yaml
spring:
  application:
    name: SpringBootWeb

  # Import local overrides
  config:
    import: optional:classpath:application-local.yaml

  # Database config
  datasource:
    url: jdbc:postgresql://localhost:5432/QuizDB?currentSchema=dbo
    username: ${DB_USERNAME:postgres} # Environment variable with default
    password: ${DB_PASSWORD:}
    driver-class-name: org.postgresql.Driver

  # JPA config
  jpa:
    hibernate:
      ddl-auto: none # Let Liquibase handle schema
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true

# Custom JWT properties
jwt:
  secret: ${JWT_SECRET:CHANGE_ME__DEV_ONLY}
  expiration: 36000000 # 10 hours
  refresh-expiration: 604800000 # 7 days
```

### Configuration Properties Class

```java
// 📁 entities/jwt/JwtProperties.java
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
    String secret,
    long expiration,
    long refreshExpiration
) {}

// Enable in main class
@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class SpringBootWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebApplication.class, args);
    }
}

// Usage
@Service
@RequiredArgsConstructor
public class JwtTokenUtil {
    private final JwtProperties props;  // Inject type-safe config

    public String generateToken(UserDetails user) {
        Date expiration = new Date(System.currentTimeMillis() + props.expiration());
        // ...
    }
}
```

### CORS Configuration

```java
@Configuration
public class SecurityConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.web.cors")
    public CorsConfiguration corsConfiguration() {
        return new CorsConfiguration();  // Auto-populated from YAML
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(CorsConfiguration cors) {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }
}
```

---

## 7. Data Mapping với MapStruct

### Basic Mapper

```java
// 📁 mappers/CommonMapperConfig.java
@MapperConfig(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CommonMapperConfig {}

// 📁 mappers/QuizMapper.java
@Mapper(config = CommonMapperConfig.class)
public interface QuizMapper {

    // Entity → Detail Response (với computed fields)
    @Mapping(target = "totalQuestions",
             expression = "java(quiz.getQuizQuestions() == null ? 0 : quiz.getQuizQuestions().size())")
    @Mapping(target = "totalAttempts",
             expression = "java(quiz.getUserQuizzes() == null ? 0 : quiz.getUserQuizzes().size())")
    QuizDetailResponse toResponse(Quiz quiz);

    // Entity → Summary Response
    QuizSummaryResponse toSummary(Quiz quiz);

    // Request → Entity (create)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userQuizzes", ignore = true)
    @Mapping(target = "quizQuestions", ignore = true)
    @Mapping(target = "isActive",
             expression = "java(request.isActive() == null ? Boolean.TRUE : request.isActive())")
    Quiz toEntity(CreateQuizRequest request);

    // Request → Entity (update, partial update)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userQuizzes", ignore = true)
    @Mapping(target = "quizQuestions", ignore = true)
    void updateEntity(UpdateQuizRequest request, @MappingTarget Quiz quiz);
}
```

### 📌 MapStruct Best Practices

1. **Use `@MapperConfig`** - share config across mappers
2. **Ignore unmapped** - set `ReportingPolicy.IGNORE`
3. **Null handling** - use `NullValuePropertyMappingStrategy` for updates
4. **Custom expressions** - computed fields with `expression`

---

## 8. Repository Pattern

### Spring Data JPA Repository

```java
@Repository
public interface QuizRepository extends JpaRepository<Quiz, UUID> {

    // Method name query
    List<Quiz> findByIsActiveTrue();

    // Case-insensitive search
    List<Quiz> findByTitleContainingIgnoreCase(String title);

    // Custom JPQL query
    @Query("SELECT q FROM Quiz q WHERE q.duration BETWEEN :min AND :max")
    List<Quiz> findByDurationRange(@Param("min") int min, @Param("max") int max);

    // Native query
    @Query(value = "SELECT * FROM quizzes WHERE created_at > :date",
           nativeQuery = true)
    List<Quiz> findRecentQuizzes(@Param("date") LocalDateTime date);

    // Exists check
    boolean existsByTitle(String title);

    // Count
    long countByIsActiveTrue();
}
```

### Pagination Support

```java
// Repository
public interface QuizRepository extends JpaRepository<Quiz, UUID> {
    Page<Quiz> findByIsActiveTrue(Pageable pageable);
}

// Service
@Transactional(readOnly = true)
public Page<QuizSummaryResponse> getPagedQuizzes(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    return quizRepository.findAll(pageable)
        .map(quizMapper::toSummary);
}

// Controller
@GetMapping("/paged")
public ResponseEntity<Page<QuizSummaryResponse>> getPagedQuizzes(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(quizService.getPagedQuizzes(page, size));
}
```

---

## 9. Controller Best Practices

### Standard Controller Structure

```java
@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
@Tag(name = "Quiz", description = "Quiz management APIs")  // Swagger
public class QuizController {

    private static final Logger log = LoggerFactory.getLogger(QuizController.class);
    private final IQuizService quizService;

    // GET all
    @GetMapping
    @Operation(summary = "Get all quizzes")
    public ResponseEntity<List<QuizSummaryResponse>> getAllQuizzes() {
        log.info("GET /api/quizzes - Fetching all quizzes");
        List<QuizSummaryResponse> quizzes = quizService.getAllQuizzes();

        if (quizzes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(quizzes);
    }

    // GET by ID
    @GetMapping("/{id}")
    @Operation(summary = "Get quiz by ID")
    public ResponseEntity<QuizDetailResponse> getQuizById(
            @PathVariable("id") UUID id) {
        log.info("GET /api/quizzes/{} - Fetching quiz", id);
        return ResponseEntity.ok(quizService.getQuizById(id));
    }

    // POST create
    @PostMapping
    @Operation(summary = "Create new quiz")
    public ResponseEntity<QuizDetailResponse> createQuiz(
            @Valid @RequestBody CreateQuizRequest request) {
        log.info("POST /api/quizzes - Creating quiz: {}", request.getTitle());
        QuizDetailResponse created = quizService.createQuiz(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT update
    @PutMapping("/{id}")
    @Operation(summary = "Update quiz")
    public ResponseEntity<QuizDetailResponse> updateQuiz(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateQuizRequest request) {
        log.info("PUT /api/quizzes/{} - Updating quiz", id);
        return ResponseEntity.ok(quizService.updateQuiz(id, request));
    }

    // DELETE
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete quiz")
    public ResponseEntity<Void> deleteQuiz(@PathVariable("id") UUID id) {
        log.info("DELETE /api/quizzes/{} - Deleting quiz", id);
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }
}
```

### HTTP Status Code Guide

| Status               | When to use                     |
| -------------------- | ------------------------------- |
| `200 OK`             | Successful GET, PUT             |
| `201 Created`        | Successful POST                 |
| `204 No Content`     | Successful DELETE, empty result |
| `400 Bad Request`    | Validation error                |
| `401 Unauthorized`   | Not authenticated               |
| `403 Forbidden`      | Not authorized                  |
| `404 Not Found`      | Resource not found              |
| `500 Internal Error` | Server error                    |

---

## 10. Security Integration

### Security Configuration

```java
@Configuration
@EnableMethodSecurity  // Enable @PreAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)  // Stateless = no CSRF
            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // All others require authentication
                .anyRequest().authenticated()
            )
            // Add JWT filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

### Method-level Security

```java
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")  // Only ADMIN
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
```

### JWT Authentication Filter

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtService;
    private final UserDetailsService userDetailsService;

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
        "/api/auth/login",
        "/api/auth/register",
        "/swagger-ui"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Get token from cookie
        String accessToken = jwtService.getAccessTokenFromCookie(request);

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract user and validate
        String userEmail = jwtService.extractUserSubject(accessToken);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(accessToken, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

---

## 📚 Quick Reference

### Annotation Cheat Sheet

| Annotation                 | Layer      | Purpose                |
| -------------------------- | ---------- | ---------------------- |
| `@RestController`          | Controller | REST endpoints         |
| `@RequestMapping`          | Controller | Base path              |
| `@GetMapping`              | Controller | GET endpoint           |
| `@PostMapping`             | Controller | POST endpoint          |
| `@Valid`                   | Controller | Trigger validation     |
| `@PathVariable`            | Controller | URL path param         |
| `@RequestParam`            | Controller | Query param            |
| `@RequestBody`             | Controller | JSON body              |
| `@Service`                 | Service    | Business service       |
| `@Transactional`           | Service    | Transaction boundary   |
| `@Repository`              | Repository | Data access            |
| `@Entity`                  | Entity     | JPA entity             |
| `@Data`                    | Any        | Lombok getters/setters |
| `@Builder`                 | Any        | Lombok builder         |
| `@RequiredArgsConstructor` | Any        | Constructor DI         |
| `@Slf4j`                   | Any        | Lombok logger          |

### Common Patterns

```java
// 1. Optional handling
User user = userRepository.findById(id)
    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

// 2. Stream mapping
List<UserResponse> users = userRepository.findAll()
    .stream()
    .map(userMapper::toResponse)
    .toList();

// 3. Conditional response
if (results.isEmpty()) {
    return ResponseEntity.noContent().build();
}
return ResponseEntity.ok(results);

// 4. Created response
return ResponseEntity.status(HttpStatus.CREATED).body(created);

// 5. Delete response
return ResponseEntity.noContent().build();
```

---

## 11. Interface-Driven Controller Pattern

### Overview

Sử dụng **Interface-Driven Controller Pattern** để tách biệt **Swagger/OpenAPI documentation** khỏi **implementation code**. Pattern này giúp:

1. **Clean Code**: Controller chỉ chứa logic, không bị "noise" từ Swagger annotations
2. **Separation of Concerns**: Documentation tách riêng khỏi business logic
3. **Maintainability**: Dễ update API docs mà không sửa controller
4. **Consistency**: Đảm bảo tất cả endpoints đều có documentation

### Folder Structure

```
controllers/
├── quiz/
│   ├── api/
│   │   └── QuizApi.java          # Interface với Swagger annotations
│   └── QuizController.java        # Implementation
├── user/
│   ├── api/
│   │   └── UserApi.java
│   └── UserController.java
├── quizsession/
│   ├── api/
│   │   ├── QuizSessionApi.java
│   │   └── QuizHistoryApi.java
│   ├── QuizSessionController.java
│   └── QuizHistoryController.java
└── ...
```

### API Interface Example

```java
// 📁 controllers/quiz/api/QuizApi.java
package com.example.springbootweb.controllers.quiz.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * API Interface for Quiz operations.
 * Contains all Swagger/OpenAPI documentation annotations.
 * Controller implements this interface to keep code clean.
 */
@Tag(name = "Quiz", description = "Quiz management APIs")
public interface QuizApi {

    @Operation(summary = "Get all quizzes",
               description = "Retrieve a list of all quizzes in the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved quiz list"),
        @ApiResponse(responseCode = "204", description = "No quizzes found")
    })
    ResponseEntity<List<QuizSummaryResponse>> getAllQuizzes();

    @Operation(summary = "Get quiz by ID",
               description = "Retrieve detailed information about a specific quiz")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved quiz details",
            content = @Content(schema = @Schema(implementation = QuizDetailResponse.class))),
        @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    ResponseEntity<QuizDetailResponse> getQuizById(
            @Parameter(description = "Quiz ID", required = true) UUID id);

    @Operation(summary = "Create a new quiz",
               description = "Create a new quiz with the provided details")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Quiz created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid quiz data provided")
    })
    ResponseEntity<QuizDetailResponse> createQuiz(CreateQuizRequest request);

    // ... other methods
}
```

### Controller Implementation Example

```java
// 📁 controllers/quiz/QuizController.java
package com.example.springbootweb.controllers.quiz;

/**
 * REST Controller for Quiz management operations.
 * Implements QuizApi interface for clean separation of Swagger documentation.
 */
@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController implements QuizApi {

    private static final Logger log = LoggerFactory.getLogger(QuizController.class);
    private final IQuizService quizService;

    @Override
    @GetMapping
    public ResponseEntity<List<QuizSummaryResponse>> getAllQuizzes() {
        log.info("GET /api/quizzes");
        List<QuizSummaryResponse> quizzes = quizService.getAllQuizzes();
        if (quizzes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(quizzes);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<QuizDetailResponse> getQuizById(@PathVariable("id") UUID id) {
        log.info("GET /api/quizzes/{}", id);
        return ResponseEntity.ok(quizService.getQuizById(id));
    }

    @Override
    @PostMapping
    public ResponseEntity<QuizDetailResponse> createQuiz(
            @Valid @RequestBody CreateQuizRequest request) {
        log.info("POST /api/quizzes - Creating: {}", request.title());
        QuizDetailResponse created = quizService.createQuiz(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ... other methods
}
```

### 📌 Best Practices

1. **API Interface chỉ chứa**:
   - `@Tag` cho controller group
   - `@Operation` cho summary/description
   - `@ApiResponses` cho response codes
   - `@Parameter` cho path/query params
   - Method signatures (không có annotations như `@GetMapping`)

2. **Controller chỉ chứa**:
   - `@RestController`, `@RequestMapping`
   - HTTP mapping annotations (`@GetMapping`, `@PostMapping`, etc.)
   - Validation annotations (`@Valid`, `@PathVariable`, etc.)
   - Implementation logic

3. **Khi nào split Controller**:
   - Controller > 200 lines → consider splitting
   - Mixed concerns (CRUD + Reporting) → split by concern
   - Example: `QuizSessionController` (lifecycle) + `QuizHistoryController` (reporting)

### ❌ Avoid: Mixed Annotations

```java
// KHÔNG NÊN: Swagger annotations trực tiếp trong controller
@RestController
@RequestMapping("/api/quizzes")
@Tag(name = "Quiz")  // ❌ Nên ở interface
public class QuizController {

    @GetMapping("/{id}")
    @Operation(summary = "Get quiz")  // ❌ Nên ở interface
    @ApiResponses({...})              // ❌ Nên ở interface
    public ResponseEntity<QuizDetailResponse> getQuizById(@PathVariable UUID id) {
        // ...
    }
}
```

### ✅ Recommended: Interface-Driven

```java
// ✅ Interface chứa documentation
@Tag(name = "Quiz")
public interface QuizApi {
    @Operation(summary = "Get quiz")
    @ApiResponses({...})
    ResponseEntity<QuizDetailResponse> getQuizById(UUID id);
}

// ✅ Controller clean, chỉ chứa implementation
@RestController
@RequestMapping("/api/quizzes")
public class QuizController implements QuizApi {
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<QuizDetailResponse> getQuizById(@PathVariable UUID id) {
        return ResponseEntity.ok(quizService.getQuizById(id));
    }
}
```

---

## 12. Interface-Based Projections for Repositories

### Overview

Khi làm việc với **aggregate queries** (SUM, COUNT, AVG, GROUP BY), **KHÔNG** nên trả về `List<Object[]>` hoặc `Object[]`. Thay vào đó, sử dụng **Interface-Based Projections** để đảm bảo type safety.

### ❌ Avoid: Object Array Returns (Bad Practice)

```java
// ❌ KHÔNG NÊN: Trả về Object[] - Type unsafe, khó maintain
@Repository
public interface AnalyticsRepository extends JpaRepository<QuizSession, UUID> {

    @Query("""
        SELECT
            COUNT(qs) as totalAttempts,
            AVG(qs.score) as avgScore,
            MAX(qs.score) as maxScore
        FROM QuizSession qs
        WHERE qs.quizId = :quizId
    """)
    List<Object[]> getQuizStatistics(@Param("quizId") UUID quizId);  // ❌ Bad
}

// Service phải cast thủ công - dễ lỗi ClassCastException
public void processStats(UUID quizId) {
    List<Object[]> results = repository.getQuizStatistics(quizId);
    Object[] row = results.get(0);

    Long total = (Long) row[0];        // ❌ Index-based, khó đọc
    BigDecimal avg = (BigDecimal) row[1];  // ❌ Dễ sai type
    BigDecimal max = (BigDecimal) row[2];  // ❌ Nếu đổi query, phải sửa index
}
```

### ✅ Recommended: Interface-Based Projections

```java
// 📁 entities/projections/analytics/QuizStatsProjection.java
package com.example.springbootweb.entities.projections.analytics;

import java.math.BigDecimal;

/**
 * Projection interface for quiz statistics.
 * Spring Data JPA automatically maps query aliases to getter methods.
 */
public interface QuizStatsProjection {
    Long getTotalAttempts();
    BigDecimal getAvgScore();
    BigDecimal getMaxScore();
}
```

```java
// 📁 repositories/AnalyticsRepository.java
@Repository
public interface AnalyticsRepository extends JpaRepository<QuizSession, UUID> {

    @Query("""
        SELECT
            COUNT(qs) as totalAttempts,
            AVG(qs.score) as avgScore,
            MAX(qs.score) as maxScore
        FROM QuizSession qs
        WHERE qs.quizId = :quizId
    """)
    QuizStatsProjection getQuizStatistics(@Param("quizId") UUID quizId);  // ✅ Type-safe
}
```

```java
// Service sử dụng - Clean và type-safe
public QuizStatisticsResponse getStats(UUID quizId) {
    QuizStatsProjection stats = repository.getQuizStatistics(quizId);

    // ✅ Type-safe getter methods
    Long total = stats.getTotalAttempts();
    BigDecimal avg = stats.getAvgScore();
    BigDecimal max = stats.getMaxScore();

    return new QuizStatisticsResponse(total, avg, max);
}
```

### Projection Interface Naming Convention

```
📁 entities/
└── 📁 projections/
    └── 📁 analytics/           # Group by domain
        ├── QuizStatsProjection.java
        ├── UserPerformanceProjection.java
        └── ScoreDistributionProjection.java
    └── 📁 reports/
        └── SalesReportProjection.java
```

### Query Alias Rules

**Quan trọng:** Tên alias trong query phải match với getter method (case-insensitive).

```java
// Query alias: totalAttempts → Getter: getTotalAttempts()
// Query alias: avgScore → Getter: getAvgScore()
// Query alias: max_score → Getter: getMaxScore() hoặc getMax_score()

@Query("""
    SELECT
        COUNT(qs) as totalAttempts,    -- matches getTotalAttempts()
        AVG(qs.score) as avgScore,      -- matches getAvgScore()
        MAX(qs.score) as maxScore       -- matches getMaxScore()
    FROM QuizSession qs
""")
QuizStatsProjection getStatistics();
```

### Complex Projection Example

```java
// 📁 entities/projections/analytics/QuestionPerformanceProjection.java
public interface QuestionPerformanceProjection {
    UUID getQuestionId();
    String getContent();
    QuestionType getType();        // Enum type supported
    Long getTotalAnswers();
    Long getCorrectAnswers();
    Long getIncorrectAnswers();
    Double getAvgTime();           // Can be Double for AVG()
}

// Repository
@Query("""
    SELECT
        sa.questionId as questionId,
        q.content as content,
        q.questionType as type,
        COUNT(sa) as totalAnswers,
        COUNT(CASE WHEN sa.isCorrect = true THEN 1 END) as correctAnswers,
        COUNT(CASE WHEN sa.isCorrect = false THEN 1 END) as incorrectAnswers,
        AVG(sa.timeSpentSeconds) as avgTime
    FROM SessionAnswer sa
    JOIN sa.question q
    GROUP BY sa.questionId, q.content, q.questionType
""")
List<QuestionPerformanceProjection> getQuestionPerformance();
```

### Single vs List Return Types

```java
// Single result (aggregate without GROUP BY)
QuizStatsProjection getQuizStatistics(UUID quizId);

// Multiple results (with GROUP BY)
List<ScoreDistributionProjection> getScoreDistribution(UUID quizId);
```

### Null Handling in Service

```java
@Service
public class AnalyticsService {

    // Utility method for null-safe access
    private Long nullSafe(Long value) {
        return value != null ? value : 0L;
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    public QuizStatisticsResponse getStats(UUID quizId) {
        QuizStatsProjection stats = repository.getQuizStatistics(quizId);

        // Projection getters may return null if no data
        return new QuizStatisticsResponse(
            nullSafe(stats.getTotalAttempts()),
            nullSafe(stats.getAvgScore()),
            nullSafe(stats.getMaxScore())
        );
    }
}
```

### 📌 Best Practices

1. **One Projection per Query Type** - Mỗi query phức tạp nên có projection riêng
2. **Group by Domain** - Tổ chức projections theo nghiệp vụ (analytics, reports, etc.)
3. **Use Descriptive Names** - `QuizBasicStatsProjection`, không phải `StatsProjection`
4. **Match Aliases Exactly** - Query alias phải khớp getter method name
5. **Handle Nulls** - Projection getters có thể trả về null khi không có data
6. **Prefer Single Return** - Nếu query chỉ trả về 1 row, return single object thay vì List

### Why Interface Projections?

| Aspect              | `Object[]`                  | Interface Projection         |
| ------------------- | --------------------------- | ---------------------------- |
| **Type Safety**     | ❌ Runtime errors           | ✅ Compile-time checking     |
| **Readability**     | ❌ `row[0]`, `row[1]`       | ✅ `proj.getTotalAttempts()` |
| **Refactoring**     | ❌ Index changes break code | ✅ IDE support               |
| **Maintainability** | ❌ Hard to understand       | ✅ Self-documenting          |
| **Testing**         | ❌ Difficult to mock        | ✅ Easy to mock interface    |

---

## 13. Enum with Business Logic

### Overview

Thay vì dùng constants và logic tính toán riêng biệt, sử dụng **Enum with Business Logic** để đóng gói cả giá trị và behavior.

### ❌ Anti-Pattern: Constants + External Logic

```java
// KHÔNG NÊN - logic phân tán, khó maintain
@Service
public class AnalyticsService {
    // Constants rải rác trong service
    private static final BigDecimal VERY_EASY_THRESHOLD = BigDecimal.valueOf(80);
    private static final BigDecimal EASY_THRESHOLD = BigDecimal.valueOf(60);
    private static final BigDecimal MEDIUM_THRESHOLD = BigDecimal.valueOf(40);
    private static final BigDecimal HARD_THRESHOLD = BigDecimal.valueOf(20);

    // Logic xử lý tách biệt
    private String calculateDifficultyLevel(BigDecimal correctRate) {
        if (correctRate == null) return "UNKNOWN";
        if (correctRate.compareTo(VERY_EASY_THRESHOLD) >= 0) return "VERY_EASY";
        else if (correctRate.compareTo(EASY_THRESHOLD) >= 0) return "EASY";
        // ... nhiều if-else
    }
}

// DTO dùng String - không type-safe
public record QuestionPerformanceDto(
    String difficultyLevel  // ❌ Có thể là bất kỳ string nào
) {}
```

### ✅ Best Practice: Enum with Methods

```java
// 📁 entities/enums/DifficultyLevel.java
public enum DifficultyLevel {
    VERY_EASY(80, "Very Easy - Most users answer correctly"),
    EASY(60, "Easy - Majority of users answer correctly"),
    MEDIUM(40, "Medium - Moderate difficulty"),
    HARD(20, "Hard - Challenging for most users"),
    VERY_HARD(0, "Very Hard - Most users struggle"),
    UNKNOWN(-1, "Unknown - Not enough data");

    private final int minCorrectRate;
    private final String description;

    DifficultyLevel(int minCorrectRate, String description) {
        this.minCorrectRate = minCorrectRate;
        this.description = description;
    }

    public int getMinCorrectRate() {
        return minCorrectRate;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Factory method - encapsulates classification logic
     */
    public static DifficultyLevel fromCorrectRate(BigDecimal correctRate) {
        if (correctRate == null) {
            return UNKNOWN;
        }
        int rate = correctRate.intValue();

        if (rate >= VERY_EASY.minCorrectRate) return VERY_EASY;
        else if (rate >= EASY.minCorrectRate) return EASY;
        else if (rate >= MEDIUM.minCorrectRate) return MEDIUM;
        else if (rate >= HARD.minCorrectRate) return HARD;
        else return VERY_HARD;
    }

    /**
     * Business methods - comparison logic
     */
    public boolean isHarderThan(DifficultyLevel other) {
        if (this == UNKNOWN || other == UNKNOWN) return false;
        return this.minCorrectRate < other.minCorrectRate;
    }

    public boolean isEasierThan(DifficultyLevel other) {
        if (this == UNKNOWN || other == UNKNOWN) return false;
        return this.minCorrectRate > other.minCorrectRate;
    }
}
```

### Usage in Service

```java
@Service
public class AnalyticsService {
    // Không cần constants - đã có trong Enum!

    private List<QuestionPerformanceDto> getQuestionPerformance(UUID quizId) {
        return results.stream()
            .map(proj -> {
                BigDecimal correctRate = calculateRate(correct, total);
                // ✅ Clean - factory method on Enum
                DifficultyLevel difficulty = DifficultyLevel.fromCorrectRate(correctRate);

                return new QuestionPerformanceDto(
                    proj.getQuestionId(),
                    correctRate,
                    difficulty  // ✅ Type-safe enum
                );
            })
            .toList();
    }
}
```

### Usage in Switch Statement

```java
// ✅ Type-safe switch với enum values
switch (metrics.difficultyLevel()) {
    case VERY_EASY:
        recommendations.add("Consider making this question more challenging");
        break;
    case EASY:
        recommendations.add("This question has good balance");
        break;
    case MEDIUM:
        recommendations.add("This question has optimal difficulty level");
        break;
    case HARD:
        recommendations.add("Review if the question wording is clear");
        break;
    case VERY_HARD:
        recommendations.add("Consider revising - may be too difficult");
        break;
    default:
        recommendations.add("Unable to determine difficulty");
        break;
}
```

### DTO with Enum Type

```java
// ✅ Type-safe DTO
public record QuestionPerformanceDto(
    UUID questionId,
    BigDecimal correctRate,
    DifficultyLevel difficultyLevel  // ✅ Enum type, not String
) {}
```

### JSON Serialization

Enum được serialize tự động thành string trong JSON response:

```json
{
  "questionId": "abc-123",
  "correctRate": 75.5,
  "difficultyLevel": "EASY"
}
```

### 📌 Benefits of Enum with Business Logic

| Aspect              | Constants + Methods       | Enum with Logic           |
| ------------------- | ------------------------- | ------------------------- |
| **Type Safety**     | ❌ String/int anywhere    | ✅ Compile-time checking  |
| **Encapsulation**   | ❌ Logic scattered        | ✅ Logic in one place     |
| **Maintainability** | ❌ Change multiple files  | ✅ Single source of truth |
| **Documentation**   | ❌ Separate comments      | ✅ Description in enum    |
| **Switch Safety**   | ❌ String comparison      | ✅ Exhaustive checking    |
| **Refactoring**     | ❌ Find all string usages | ✅ IDE support            |

### When to Use Enum with Logic

1. **Classification/Categorization** - Difficulty levels, status states
2. **Threshold-based decisions** - Score ranges, performance tiers
3. **Domain constants with behavior** - Payment status, order states
4. **Values that need comparison** - Priority levels, severity grades

---

## 14. JPA Specifications for Dynamic Filtering

### Overview

Sử dụng **Spring Data JPA Specifications** để tạo dynamic queries với filter parameters. Pattern này cho phép:

- **Dynamic filtering** - Tùy chọn filter fields (null = ignored)
- **Type-safe queries** - Criteria API compile-time checking
- **Reusable predicates** - Composable specifications
- **Clean separation** - Filter logic trong Service, không phải Controller

### Architecture Flow

```
Controller (@ModelAttribute Filter)
    → Service (build Specification from Filter)
    → Repository.findAll(spec, pageable)
```

### 📁 File Structure

```
entities/dtos/{entity}/
    └── {Entity}Filter.java          # Filter DTO with @ModelAttribute

repositories/
    └── {Entity}Repository.java      # extends JpaSpecificationExecutor<Entity>

repositories/specifications/
    └── {Entity}Specifications.java  # Static Specification methods

services/interfaces/
    └── I{Entity}Service.java        # Method nhận Filter object

services/impl/
    └── {Entity}Service.java         # Build Specification từ Filter

controllers/{entity}/api/
    └── {Entity}Api.java             # @ModelAttribute Filter parameter

controllers/{entity}/
    └── {Entity}Controller.java      # Truyền Filter xuống Service
```

### Step 1: Create Filter DTO

```java
// 📁 entities/dtos/quizzes/QuizFilter.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Filter criteria for quiz search")
public class QuizFilter {

    @Schema(description = "Search keyword for quiz title (partial match)")
    private String titleKeyword;

    @Schema(description = "Filter by active status")
    private Boolean isActive;

    @Schema(description = "Filter by minimum duration (minutes)")
    private Integer minDuration;

    @Schema(description = "Filter by maximum duration (minutes)")
    private Integer maxDuration;
}
```

### Step 2: Repository extends JpaSpecificationExecutor

```java
// 📁 repositories/QuizRepository.java
@Repository
public interface QuizRepository extends
        JpaRepository<Quiz, UUID>,
        JpaSpecificationExecutor<Quiz> {  // ✅ Enable Specifications
    // existing methods...
}
```

### Step 3: Create Specifications Class

```java
// 📁 repositories/specifications/QuizSpecifications.java
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuizSpecifications {

    // ==================== Basic Field Specifications ====================

    public static Specification<Quiz> titleContains(String keyword) {
        return (root, query, cb) -> keyword == null || keyword.isBlank()
            ? null
            : cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<Quiz> isActive(Boolean isActive) {
        return (root, query, cb) -> isActive == null
            ? null
            : cb.equal(root.get("isActive"), isActive);
    }

    public static Specification<Quiz> durationBetween(Integer min, Integer max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min != null && max != null) {
                return cb.between(root.get("duration"), min, max);
            }
            if (min != null) {
                return cb.greaterThanOrEqualTo(root.get("duration"), min);
            }
            return cb.lessThanOrEqualTo(root.get("duration"), max);
        };
    }

    // ==================== Combined Filter Specification ====================

    /**
     * Build Specification from Filter object.
     * Service calls this method - NOT Controller.
     */
    public static Specification<Quiz> fromFilter(QuizFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getTitleKeyword() != null && !filter.getTitleKeyword().isBlank()) {
                predicates.add(cb.like(
                    cb.lower(root.get("title")),
                    "%" + filter.getTitleKeyword().toLowerCase() + "%"
                ));
            }

            if (filter.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), filter.getIsActive()));
            }

            if (filter.getMinDuration() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("duration"), filter.getMinDuration()));
            }

            if (filter.getMaxDuration() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("duration"), filter.getMaxDuration()));
            }

            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
```

### Step 4: Service Interface nhận Filter

```java
// 📁 services/interfaces/IQuizService.java
public interface IQuizService {

    // ✅ Methods nhận Filter object (không phải từng param riêng lẻ)
    List<QuizSummaryResponse> getAllQuizzes(QuizFilter filter);

    Page<QuizSummaryResponse> getPagedQuizzes(Integer page, Integer size, QuizFilter filter);

    // ... other methods
}
```

### Step 5: Service Implementation build Specification

```java
// 📁 services/impl/QuizService.java
@Service
@RequiredArgsConstructor
public class QuizService implements IQuizService {

    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;

    @Override
    @Transactional(readOnly = true)
    public List<QuizSummaryResponse> getAllQuizzes(QuizFilter filter) {
        // ✅ Service builds Specification from Filter
        Specification<Quiz> spec = QuizSpecifications.fromFilter(filter);

        return quizRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "title"))
            .stream()
            .map(quizMapper::toSummaryResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuizSummaryResponse> getPagedQuizzes(Integer page, Integer size, QuizFilter filter) {
        // ✅ Service builds Specification from Filter
        Specification<Quiz> spec = QuizSpecifications.fromFilter(filter);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "title"));

        return quizRepository.findAll(spec, pageable)
            .map(quizMapper::toSummaryResponse);
    }
}
```

### Step 6: API Interface với @ModelAttribute

```java
// 📁 controllers/quiz/api/QuizApi.java
@Tag(name = "Quiz", description = "Quiz management APIs")
public interface QuizApi {

    @Operation(summary = "Get all quizzes with filters")
    ResponseEntity<List<QuizSummaryResponse>> getAllQuizzes(
        @ModelAttribute QuizFilter filter);  // ✅ @ModelAttribute binds query params

    @Operation(summary = "Get paged quizzes with filters")
    ResponseEntity<Page<QuizSummaryResponse>> getPagedQuizzes(
        @Parameter(description = "Page number") Integer page,
        @Parameter(description = "Page size") Integer size,
        @ModelAttribute QuizFilter filter);  // ✅ Filter as single object
}
```

### Step 7: Controller truyền Filter xuống Service

```java
// 📁 controllers/quiz/QuizController.java
@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController implements QuizApi {

    private final IQuizService quizService;

    @Override
    @GetMapping
    public ResponseEntity<List<QuizSummaryResponse>> getAllQuizzes(
            @ModelAttribute QuizFilter filter) {  // ✅ Spring binds query params to Filter
        List<QuizSummaryResponse> quizzes = quizService.getAllQuizzes(filter);
        return quizzes.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(quizzes);
    }

    @Override
    @GetMapping("/paged")
    public ResponseEntity<Page<QuizSummaryResponse>> getPagedQuizzes(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @ModelAttribute QuizFilter filter) {  // ✅ Filter separate from pagination
        return ResponseEntity.ok(quizService.getPagedQuizzes(page, size, filter));
    }
}
```

### Usage Examples

```bash
# Get all quizzes (no filter)
GET /api/quizzes

# Filter by title keyword
GET /api/quizzes?titleKeyword=java

# Filter by active status
GET /api/quizzes?isActive=true

# Filter by duration range
GET /api/quizzes?minDuration=10&maxDuration=30

# Combined filters with pagination
GET /api/quizzes/paged?page=0&size=10&titleKeyword=spring&isActive=true&minDuration=15
```

### 📌 Best Practices

| Rule               | Description                                                         |
| ------------------ | ------------------------------------------------------------------- |
| **Controller**     | CHỈ nhận Filter qua `@ModelAttribute`, KHÔNG build Specification    |
| **Service**        | Build Specification từ Filter bằng `XxxSpecifications.fromFilter()` |
| **Specifications** | Static utility class với `AccessLevel.PRIVATE` constructor          |
| **Filter DTO**     | Dùng `@Data @Builder @NoArgsConstructor @AllArgsConstructor`        |
| **Null handling**  | Return `null` trong Specification nếu filter field là null          |
| **Sorting**        | Apply sort trong Service, không phải Specifications                 |

### ❌ Anti-patterns

```java
// ❌ KHÔNG build Specification trong Controller
@GetMapping
public ResponseEntity<List<Quiz>> getQuizzes(@ModelAttribute QuizFilter filter) {
    Specification<Quiz> spec = QuizSpecifications.fromFilter(filter);  // ❌ Wrong place!
    return ResponseEntity.ok(quizRepository.findAll(spec));
}

// ❌ KHÔNG truyền từng filter param riêng lẻ (quá nhiều parameters)
List<QuizSummaryResponse> getAllQuizzes(
    String titleKeyword,    // ❌ Too many params
    Boolean isActive,
    Integer minDuration,
    Integer maxDuration
);

// ✅ ĐÚNG: Truyền Filter object duy nhất
List<QuizSummaryResponse> getAllQuizzes(QuizFilter filter);  // ✅ Single Filter object
```

---

## 🔗 Related Documents

- [Skills Overview](../SKILL.md)
- [Copilot Instructions](../../copilot-instructions.md)
- [Project README](../../../README.md)

---

_Last updated: January 2026_
