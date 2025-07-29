# Geliştirme Rehberi

Bu dokümantasyon, Öğrenci Yönetim Sistemi'nde geliştirme yaparken takip edilmesi gereken kuralları ve best practice'leri detaylandırır.

## 📋 İçindekiler

- [Geliştirme Ortamı Kurulumu](#geliştirme-ortamı-kurulumu)
- [Kod Standartları](#kod-standartları)
- [Git Workflow](#git-workflow)
- [Testing](#testing)
- [Code Review](#code-review)
- [Performance](#performance)
- [Security](#security)
- [Troubleshooting](#troubleshooting)

## 🛠️ Geliştirme Ortamı Kurulumu

### Gerekli Araçlar

#### Backend Geliştirme

```bash
# Java Development Kit
java -version  # JDK 21 gerekli

# Maven
mvn -version   # 3.9+ gerekli

# IDE (IntelliJ IDEA önerilen)
# - Lombok plugin yükleyin
# - Spring Boot plugin yükleyin
# - Database plugin yükleyin

# PostgreSQL
psql --version  # 12+ gerekli
```

#### Frontend Geliştirme

```bash
# Node.js
node --version  # 18+ gerekli

# npm
npm --version   # 8+ gerekli

# IDE (VS Code önerilen)
# - ESLint extension
# - Prettier extension
# - React Developer Tools
```

### IDE Konfigürasyonu

#### IntelliJ IDEA Ayarları

```xml
<!-- .idea/codeStyles/Project.xml -->
<component name="ProjectCodeStyleConfiguration">
  <code_scheme name="Project" version="173">
    <JavaCodeStyleSettings>
      <option name="IMPORT_LAYOUT_TABLE">
        <value>
          <package name="java" withSubpackages="true" static="false" />
          <package name="javax" withSubpackages="true" static="false" />
          <emptyLine />
          <package name="org" withSubpackages="true" static="false" />
          <package name="com" withSubpackages="true" static="false" />
          <emptyLine />
          <package name="" withSubpackages="true" static="false" />
        </value>
      </option>
    </JavaCodeStyleSettings>
  </code_scheme>
</component>
```

#### VS Code Ayarları

```json
// .vscode/settings.json
{
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": true
  },
  "eslint.validate": ["javascript", "javascriptreact"],
  "prettier.singleQuote": true,
  "prettier.trailingComma": "es5"
}
```

### Environment Variables

#### Backend (.env.development)

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/student_management_dev
SPRING_DATASOURCE_USERNAME=student_user
SPRING_DATASOURCE_PASSWORD=dev_password

# JWT
JWT_SECRET=dev_jwt_secret_key_change_in_production
JWT_EXPIRATION=3600

# Application
SPRING_PROFILES_ACTIVE=development
SERVER_PORT=8080

# Logging
LOG_LEVEL=DEBUG
```

#### Frontend (.env.development)

```bash
REACT_APP_API_URL=http://localhost:8080
REACT_APP_ENVIRONMENT=development
```

## 📝 Kod Standartları

### Java Kod Standartları

#### Naming Conventions

```java
// Class names: PascalCase
public class StudentService {
    // Method names: camelCase
    public StudentResponse getStudentById(Long id) {
        // Variable names: camelCase
        String studentName = "John Doe";
        // Constants: UPPER_SNAKE_CASE
        private static final String DEFAULT_DEPARTMENT = "Computer Science";
    }
}

// Package names: lowercase
package com.example.backend.service;

// Interface names: PascalCase, often with 'able' suffix
public interface StudentRepository extends JpaRepository<Student, Long> {
}
```

#### Code Structure

```java
// 1. Package declaration
package com.example.backend.controller;

// 2. Imports (organized)
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;

// 3. Class declaration
@RestController
@RequestMapping("/api/v3/students")
@RequiredArgsConstructor
public class StudentController {

    // 4. Dependencies (final fields)
    private final StudentService studentService;

    // 5. Public methods
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudent(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    // 6. Private helper methods
    private void validateStudent(StudentRequest request) {
        // validation logic
    }
}
```

#### Exception Handling

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
            .error("RESOURCE_NOT_FOUND")
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
```

### JavaScript/React Kod Standartları

#### Component Structure

```jsx
// 1. Imports
import React, { useState, useEffect } from "react";
import PropTypes from "prop-types";

// 2. Component definition
const StudentList = ({ students, onDelete, onEdit }) => {
  // 3. State declarations
  const [filteredStudents, setFilteredStudents] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");

  // 4. Effects
  useEffect(() => {
    const filtered = students.filter((student) =>
      student.firstName.toLowerCase().includes(searchTerm.toLowerCase())
    );
    setFilteredStudents(filtered);
  }, [students, searchTerm]);

  // 5. Event handlers
  const handleSearch = (event) => {
    setSearchTerm(event.target.value);
  };

  // 6. Render method
  return (
    <div className="student-list">
      <input
        type="text"
        placeholder="Search students..."
        value={searchTerm}
        onChange={handleSearch}
      />
      {filteredStudents.map((student) => (
        <StudentCard
          key={student.id}
          student={student}
          onDelete={onDelete}
          onEdit={onEdit}
        />
      ))}
    </div>
  );
};

// 7. PropTypes
StudentList.propTypes = {
  students: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.number.isRequired,
      firstName: PropTypes.string.isRequired,
      lastName: PropTypes.string.isRequired,
    })
  ).isRequired,
  onDelete: PropTypes.func.isRequired,
  onEdit: PropTypes.func.isRequired,
};

// 8. Export
export default StudentList;
```

#### Hook Usage

```jsx
// Custom hooks
const useApi = (url) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const response = await fetch(url);
        const result = await response.json();
        setData(result);
      } catch (err) {
        setError(err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [url]);

  return { data, loading, error };
};
```

## 🔄 Git Workflow

### Branch Strategy

#### Main Branches

```bash
# Production branch
main

# Development branch
develop

# Feature branches
feature/user-authentication
feature/student-crud
feature/csv-upload

# Bug fix branches
bugfix/login-validation
bugfix/database-connection

# Hotfix branches
hotfix/security-patch
hotfix/critical-bug
```

### Commit Message Convention

#### Conventional Commits

```bash
# Format: <type>[optional scope]: <description>

# Types:
feat:     # New feature
fix:      # Bug fix
docs:     # Documentation changes
style:    # Code style changes (formatting, etc.)
refactor: # Code refactoring
test:     # Adding or updating tests
chore:    # Maintenance tasks

# Examples:
feat(auth): add JWT token validation
fix(students): resolve duplicate email issue
docs(api): update endpoint documentation
style(frontend): format code with prettier
refactor(backend): extract service layer
test(students): add unit tests for StudentService
chore(deps): update dependencies
```

### Pull Request Process

#### PR Template

```markdown
## Description

Brief description of changes

## Type of Change

- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing

- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Manual testing completed

## Checklist

- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] No console errors
- [ ] No linting errors

## Screenshots (if applicable)
```

## 🧪 Testing

### Backend Testing

#### Unit Tests

```java
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    @DisplayName("Should return student when valid ID provided")
    void getStudentById_ValidId_ReturnsStudent() {
        // Given
        Long studentId = 1L;
        Student student = Student.builder()
            .id(studentId)
            .firstName("John")
            .lastName("Doe")
            .build();

        when(studentRepository.findById(studentId))
            .thenReturn(Optional.of(student));

        // When
        StudentResponse result = studentService.getStudentById(studentId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("John");
        verify(studentRepository).findById(studentId);
    }
}
```

#### Integration Tests

```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb"
})
class StudentControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createStudent_ValidRequest_ReturnsCreatedStudent() {
        // Given
        CreateStudentRequest request = CreateStudentRequest.builder()
            .studentNumber("2024001")
            .firstName("John")
            .lastName("Doe")
            .email("john@example.com")
            .build();

        // When
        ResponseEntity<StudentResponse> response = restTemplate.postForEntity(
            "/api/v3/students",
            request,
            StudentResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getFirstName()).isEqualTo("John");
    }
}
```

### Frontend Testing

#### Component Tests

```jsx
import { render, screen, fireEvent } from "@testing-library/react";
import StudentForm from "./StudentForm";

describe("StudentForm", () => {
  test("renders form fields", () => {
    render(<StudentForm onSubmit={jest.fn()} />);

    expect(screen.getByLabelText(/first name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/last name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
  });

  test("submits form with valid data", () => {
    const mockOnSubmit = jest.fn();
    render(<StudentForm onSubmit={mockOnSubmit} />);

    fireEvent.change(screen.getByLabelText(/first name/i), {
      target: { value: "John" },
    });
    fireEvent.change(screen.getByLabelText(/last name/i), {
      target: { value: "Doe" },
    });
    fireEvent.change(screen.getByLabelText(/email/i), {
      target: { value: "john@example.com" },
    });

    fireEvent.click(screen.getByRole("button", { name: /submit/i }));

    expect(mockOnSubmit).toHaveBeenCalledWith({
      firstName: "John",
      lastName: "Doe",
      email: "john@example.com",
    });
  });
});
```

#### API Tests

```jsx
import { rest } from "msw";
import { setupServer } from "msw/node";
import { render, screen, waitFor } from "@testing-library/react";
import StudentList from "./StudentList";

const server = setupServer(
  rest.get("/api/v3/students", (req, res, ctx) => {
    return res(
      ctx.json({
        content: [
          {
            id: 1,
            firstName: "John",
            lastName: "Doe",
            email: "john@example.com",
          },
        ],
      })
    );
  })
);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

test("loads and displays students", async () => {
  render(<StudentList />);

  await waitFor(() => {
    expect(screen.getByText("John Doe")).toBeInTheDocument();
  });
});
```

## 👀 Code Review

### Review Checklist

#### Backend Review

- [ ] Code follows naming conventions
- [ ] Proper exception handling
- [ ] Input validation implemented
- [ ] Database queries optimized
- [ ] Security considerations addressed
- [ ] Unit tests written
- [ ] Integration tests written
- [ ] Documentation updated

#### Frontend Review

- [ ] Component structure follows guidelines
- [ ] Props validation implemented
- [ ] Error handling implemented
- [ ] Performance optimizations applied
- [ ] Accessibility considerations
- [ ] Unit tests written
- [ ] No console errors
- [ ] Responsive design verified

### Review Comments

#### Good Review Comments

```markdown
✅ Good: "This is a great approach! The separation of concerns is clear."

🔧 Suggestion: "Consider extracting this logic into a separate utility function for reusability."

❓ Question: "Why did you choose this approach over the alternative?"

🐛 Issue: "This might cause a memory leak. Consider using useEffect cleanup."

📚 Documentation: "Please add JSDoc comments for this function."
```

## ⚡ Performance

### Backend Performance

#### Database Optimization

```java
// Use indexes for frequently queried fields
@Entity
@Table(indexes = {
    @Index(name = "idx_student_email", columnList = "email"),
    @Index(name = "idx_student_number", columnList = "student_number")
})
public class Student {
    // entity fields
}

// Use pagination for large datasets
@GetMapping
public Page<StudentResponse> getStudents(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {
    return studentService.getStudents(PageRequest.of(page, size));
}
```

#### Caching Strategy

```java
@Service
@CacheConfig(cacheNames = "students")
public class StudentService {

    @Cacheable(key = "#id")
    public StudentResponse getStudentById(Long id) {
        // implementation
    }

    @CacheEvict(key = "#id")
    public void deleteStudent(Long id) {
        // implementation
    }
}
```

### Frontend Performance

#### React Optimization

```jsx
// Use React.memo for expensive components
const StudentCard = React.memo(({ student, onDelete }) => {
  return (
    <div className="student-card">
      <h3>
        {student.firstName} {student.lastName}
      </h3>
      <p>{student.email}</p>
      <button onClick={() => onDelete(student.id)}>Delete</button>
    </div>
  );
});

// Use useMemo for expensive calculations
const StudentList = ({ students }) => {
  const sortedStudents = useMemo(() => {
    return students.sort((a, b) => a.firstName.localeCompare(b.firstName));
  }, [students]);

  return (
    <div>
      {sortedStudents.map((student) => (
        <StudentCard key={student.id} student={student} />
      ))}
    </div>
  );
};
```

#### Bundle Optimization

```javascript
// Lazy loading for routes
const StudentList = lazy(() => import("./components/StudentList"));
const StudentForm = lazy(() => import("./components/StudentForm"));

// Code splitting
const App = () => {
  return (
    <Suspense fallback={<Loading />}>
      <Routes>
        <Route path="/students" element={<StudentList />} />
        <Route path="/students/new" element={<StudentForm />} />
      </Routes>
    </Suspense>
  );
};
```

## 🔒 Security

### Backend Security

#### Input Validation

```java
@RestController
public class StudentController {

    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(
            @Valid @RequestBody CreateStudentRequest request) {
        // implementation
    }
}

public class CreateStudentRequest {
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Email(message = "Email must be valid")
    private String email;
}
```

#### SQL Injection Prevention

```java
// Use JPA repositories (automatically handles SQL injection)
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    // Safe query methods
    List<Student> findByFirstNameContainingIgnoreCase(String firstName);
}

// If using @Query, use parameterized queries
@Query("SELECT s FROM Student s WHERE s.email = :email")
Optional<Student> findByEmail(@Param("email") String email);
```

### Frontend Security

#### XSS Prevention

```jsx
// Use React's built-in XSS protection
const StudentCard = ({ student }) => {
  return (
    <div>
      {/* React automatically escapes content */}
      <h3>
        {student.firstName} {student.lastName}
      </h3>

      {/* For HTML content, use dangerouslySetInnerHTML carefully */}
      <div
        dangerouslySetInnerHTML={{ __html: sanitizeHtml(student.description) }}
      />
    </div>
  );
};
```

#### CSRF Protection

```jsx
// Include CSRF token in requests
const apiClient = axios.create({
  baseURL: process.env.REACT_APP_API_URL,
  headers: {
    "X-CSRF-Token": getCsrfToken(),
  },
});
```

## 🔧 Troubleshooting

### Common Issues

#### Backend Issues

```bash
# Database connection issues
# Check application.yml configuration
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/student_management
    username: student_user
    password: your_password

# JWT token issues
# Check JWT secret configuration
jwt:
  secret: your_jwt_secret_key
  expiration: 3600

# CORS issues
# Check CORS configuration
@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

#### Frontend Issues

```bash
# API connection issues
# Check REACT_APP_API_URL environment variable
REACT_APP_API_URL=http://localhost:8080

# Build issues
# Clear node_modules and reinstall
rm -rf node_modules package-lock.json
npm install

# Port conflicts
# Change port in package.json
"scripts": {
  "start": "PORT=3001 react-scripts start"
}
```

### Debug Tools

#### Backend Debugging

```java
// Enable debug logging
logging:
  level:
    com.example.backend: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG

// Use @Slf4j for logging
@Slf4j
@Service
public class StudentService {
    public StudentResponse getStudentById(Long id) {
        log.debug("Fetching student with id: {}", id);
        // implementation
    }
}
```

#### Frontend Debugging

```jsx
// Use React DevTools
// Install React Developer Tools browser extension

// Use console.log for debugging
const StudentList = ({ students }) => {
  console.log("Students:", students);

  return (
    <div>
      {students.map((student) => (
        <StudentCard key={student.id} student={student} />
      ))}
    </div>
  );
};
```

---

**Not:** Bu rehber sürekli güncellenmektedir. Yeni best practice'ler ve araçlar eklendikçe dokümantasyon güncellenecektir.
