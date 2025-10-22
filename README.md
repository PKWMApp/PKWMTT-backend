# ⚙ Trybik — Backend

Trybik (Server) – timetable, exam calendar & ECTS calculator for students of Mechanical Engineering @ Cracow University of Technology

---

## 📦 Tech Stack

- Framework: Java Spring Boot 3.5+
- Language: Java 21
- Database: MySQL (H2 used for tests)
- Authentication: JWT (JSON Web Tokens)
- API Docs: Swagger / OpenAPI
- Caching: Caffeine
- Build / Project: Maven (mvn / ./mvnw)
- Containerization: Docker

---

## ⚙️ Setup & Installation

### 1. Clone the repository

```bash
git clone https://github.com/TrybikDevelopers/Trybik-backend.git
cd Trybik-backend
```

### 2. Build the project

If the Maven wrapper is present:

```bash
./mvnw clean package
```

Or with your system Maven:

```bash
mvn clean package
```

### 3. Run with Docker

Build and run locally:

```bash
docker build -t trybik-backend .
docker run -d --name trybik-backend -p 8080:8080 trybik-backend
```

If an official container image is published (check the Releases or container registry), you can pull and run:

```bash
docker pull ghcr.io/trybikdevelopers/trybik-backend:latest
docker run -d --name trybik-backend -p 8080:8080 ghcr.io/trybikdevelopers/trybik-backend:latest
```

---

## 📮 API Overview

This backend exposes RESTful endpoints for:

- Timetable management (by study group, with filters)
- Exam calendar and exam types
- ECTS calculator
- Group and subject listings
- (Other endpoints may exist — check the controller packages / OpenAPI docs)

Authentication
- Endpoints are protected using JWT tokens.
- Example header:

```
Authorization: Bearer <token>
Content-Type: application/json
```

API documentation (Swagger UI / OpenAPI) is usually available at:
`http://localhost:8080/swagger-ui/index.html` or `http://localhost:8080/v3/api-docs` (if Swagger/OpenAPI is enabled in configuration).

---

## 🧪 Testing

Run unit and integration tests:

```bash
./mvnw test
# or
mvn test
```

The project may use H2 for tests — check test configuration files for details.

---

## 🤝 Contributing

We welcome contributions!

1. Fork the repository
2. Create a new branch: `git checkout -b feature/your-feature`
3. Make your changes and add tests where appropriate
4. Commit and push:
   ```bash
   git commit -m "feat: short description"
   git push origin feature/your-feature
   ```
5. Open a pull request against the main branch and describe your changes

Please follow the existing code style and include tests for new behavior when possible.

---

## 📄 License

This project is licensed under the MIT License. See [LICENSE](./LICENSE) for details.

---

## 💬 Contact / Support

- Issues: https://github.com/TrybikDevelopers/Trybik-backend/issues
- Organization: https://github.com/TrybikDevelopers

If you have questions about API usage or want to report bugs, please open an issue with reproduction steps and relevant logs.

---

## 🌐 Related Projects

- Frontend / mobile apps (if maintained under the TrybikDevelopers organization) — check the organization repositories for matching frontend projects.

---

## 📸 Screenshots

Add screenshots or API examples here if helpful.
