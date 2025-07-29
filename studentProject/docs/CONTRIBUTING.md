# Katkıda Bulunma Rehberi

Bu dokümantasyon, Öğrenci Yönetim Sistemi projesine katkıda bulunmak isteyen geliştiriciler için hazırlanmıştır.

## 📋 İçindekiler

- [Başlarken](#başlarken)
- [Geliştirme Süreci](#geliştirme-süreci)
- [Kod Standartları](#kod-standartları)
- [Test Yazma](#test-yazma)
- [Pull Request Süreci](#pull-request-süreci)
- [Issue Raporlama](#issue-raporlama)
- [İletişim](#iletişim)

## 🚀 Başlarken

### Ön Gereksinimler

Projeye katkıda bulunmadan önce aşağıdaki araçların kurulu olduğundan emin olun:

```bash
# Java Development Kit
java -version  # JDK 21 gerekli

# Maven
mvn -version   # 3.9+ gerekli

# Node.js
node --version # 18+ gerekli

# npm
npm --version  # 8+ gerekli

# Git
git --version  # 2.0+ gerekli

# PostgreSQL
psql --version # 12+ gerekli
```

### Projeyi Fork Etme

1. GitHub'da projeyi fork edin
2. Fork'unuzu local'e clone edin:

```bash
git clone https://github.com/YOUR_USERNAME/studentProject.git
cd studentProject
```

3. Upstream remote'u ekleyin:

```bash
git remote add upstream https://github.com/ORIGINAL_OWNER/studentProject.git
```

### Geliştirme Ortamı Kurulumu

#### Backend Kurulumu

```bash
cd backend

# Dependencies'leri yükleyin
mvn clean install

# Veritabanını kurun
# PostgreSQL'de yeni veritabanı oluşturun
createdb student_management_dev

# Uygulamayı çalıştırın
mvn spring-boot:run
```

#### Frontend Kurulumu

```bash
cd frontend

# Dependencies'leri yükleyin
npm install

# Uygulamayı çalıştırın
npm start
```

#### Config Server Kurulumu

```bash
cd config-server

# Uygulamayı çalıştırın
mvn spring-boot:run
```

## 🔄 Geliştirme Süreci

### Branch Stratejisi

Projemizde aşağıdaki branch stratejisini kullanıyoruz:

```bash
# Ana branch'ler
main          # Production branch
develop       # Development branch

# Feature branch'ler
feature/user-authentication
feature/student-crud
feature/csv-upload

# Bug fix branch'ler
bugfix/login-validation
bugfix/database-connection

# Hotfix branch'ler
hotfix/security-patch
hotfix/critical-bug
```

### Yeni Feature Geliştirme

1. **Develop branch'ini güncelleyin:**

```bash
git checkout develop
git pull upstream develop
```

2. **Feature branch oluşturun:**

```bash
git checkout -b feature/your-feature-name
```

3. **Geliştirme yapın ve commit edin:**

```bash
git add .
git commit -m "feat: add new feature description"
```

4. **Branch'inizi push edin:**

```bash
git push origin feature/your-feature-name
```

5. **Pull Request oluşturun:**
   - GitHub'da Pull Request oluşturun
   - Template'i doldurun
   - Reviewers atayın

### Commit Message Convention

[Conventional Commits](https://www.conventionalcommits.org/) standardını kullanıyoruz:

```bash
# Format: <type>[optional scope]: <description>

# Types:
feat:     # Yeni özellik
fix:      # Hata düzeltmesi
docs:     # Dokümantasyon değişiklikleri
style:    # Kod stil değişiklikleri
refactor: # Kod refactoring
test:     # Test ekleme veya güncelleme
chore:    # Bakım görevleri

# Examples:
feat(auth): add JWT token validation
fix(students): resolve duplicate email issue
docs(api): update endpoint documentation
style(frontend): format code with prettier
refactor(backend): extract service layer
test(students): add unit tests for StudentService
chore(deps): update dependencies
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

// Interface names: PascalCase
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

### IDE Konfigürasyonu

#### IntelliJ IDEA

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

#### VS Code

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

## 🧪 Test Yazma

### Backend Test Yazma

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

### Frontend Test Yazma

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

### Test Çalıştırma

```bash
# Backend tests
cd backend
mvn test

# Frontend tests
cd frontend
npm test

# Coverage report
npm test -- --coverage
```

## 🔄 Pull Request Süreci

### PR Template

Pull Request oluştururken aşağıdaki template'i kullanın:

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
- [ ] No console errors
- [ ] No linting errors

## Checklist

- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] No breaking changes (or documented)
- [ ] Performance impact considered

## Screenshots (if applicable)
```

### Review Süreci

1. **Self Review:** Kendi kodunuzu gözden geçirin
2. **Tests:** Tüm testlerin geçtiğinden emin olun
3. **Documentation:** Gerekli dokümantasyonu güncelleyin
4. **Submit:** Pull Request'i gönderin
5. **Address Feedback:** Review feedback'lerini düzeltin

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

## 🐛 Issue Raporlama

### Bug Report Template

```markdown
## Bug Description

Clear and concise description of the bug

## Steps to Reproduce

1. Go to '...'
2. Click on '...'
3. Scroll down to '...'
4. See error

## Expected Behavior

What you expected to happen

## Actual Behavior

What actually happened

## Environment

- OS: [e.g. Windows 10, macOS 12.0]
- Browser: [e.g. Chrome 96, Firefox 95]
- Version: [e.g. 1.0.0]

## Additional Context

Any other context about the problem
```

### Feature Request Template

```markdown
## Feature Description

Clear and concise description of the feature

## Problem Statement

What problem does this feature solve?

## Proposed Solution

How should this feature work?

## Alternative Solutions

Any alternative solutions you've considered

## Additional Context

Any other context or screenshots
```

### Issue Labels

- `bug` - Hata raporu
- `enhancement` - Özellik isteği
- `documentation` - Dokümantasyon
- `good first issue` - Yeni başlayanlar için
- `help wanted` - Yardım gerekli
- `priority: high` - Yüksek öncelik
- `priority: medium` - Orta öncelik
- `priority: low` - Düşük öncelik

## 📞 İletişim

### Geliştirici Topluluğu

- **GitHub Issues:** [Proje Issues](https://github.com/username/studentProject/issues)
- **Discussions:** [GitHub Discussions](https://github.com/username/studentProject/discussions)
- **Email:** [email@example.com]

### Katkıda Bulunanlar

Projeye katkıda bulunan herkes [CONTRIBUTORS.md](CONTRIBUTORS.md) dosyasında listelenir.

### Code of Conduct

Projemizde [Contributor Covenant Code of Conduct](CODE_OF_CONDUCT.md) uygulanır.

## 🏆 Katkı Türleri

### Kod Katkısı

- Bug fixes
- New features
- Performance improvements
- Code refactoring

### Dokümantasyon

- API documentation
- User guides
- Code comments
- README updates

### Test

- Unit tests
- Integration tests
- End-to-end tests
- Test documentation

### Design

- UI/UX improvements
- Wireframes
- Mockups
- Design system

### Infrastructure

- CI/CD improvements
- Deployment scripts
- Monitoring setup
- Security enhancements

## 🎯 İlk Katkı

Yeni başlayanlar için önerilen adımlar:

1. **Good First Issues** etiketli issue'ları inceleyin
2. **Documentation** ile başlayın
3. **Small bug fixes** yapın
4. **Test writing** ile devam edin
5. **Feature development** ile ilerleyin

### Önerilen İlk Issue'lar

- [ ] README dosyasını güncelleme
- [ ] Test coverage artırma
- [ ] Small UI improvements
- [ ] Code comments ekleme
- [ ] Error message improvements

---

**Teşekkürler!** Projemize katkıda bulunduğunuz için teşekkür ederiz. Her katkı değerlidir ve topluluğumuzu güçlendirir.
