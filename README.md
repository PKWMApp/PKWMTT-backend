# 🚀 PKWM App Backend

Backend for the PKWM mobile app, providing timetable, exam calendar, and ECTS calculator services for students of Mechanical Engineering at Cracow University of Technology.

---

## 📦 Tech Stack

- **Framework:** Java Spring Boot 3.5+
- **Language:** Java 21
- **Database:** MySQL (H2 for tests)
- **Authentication:** JWT (JSON Web Tokens)
- **API Docs:** Swagger (OpenAPI)
- **Caching:** Caffeine
- **Project Management:** Maven
- **Containerization:** Docker

---

## ⚙️ Setup & Installation

### 1. Clone the repository

```shell
git clone https://github.com/PKTTTeam/PKWMTT-backend.git
cd PKWMTT-backend
```

### 2. Build the project

```shell
./mvnw clean package
```

### 3. Run with Docker

```shell
docker build -t pkwmtt-backend .
docker run -d --name pkwmtt-backend -p 8080:8080 pkwmtt-backend
```

Or pull the latest image:

```shell
docker pull ghcr.io/pkttteam/pkwmtt-backend:latest
docker run -d --name pkwmtt-backend -p 8080:8080 ghcr.io/pkttteam/pkwmtt-backend:latest
```

---

## 📮 API Overview

The backend exposes RESTful endpoints for:

- Timetable management (by group, with filters)
- Exam calendar and types
- ECTS calculator
- Group and subject listings

All endpoints use JWT authentication. Example headers:

```
Authorization: Bearer <token>
Content-Type: application/json
```

API documentation (Swagger UI) is available at:  
`http://localhost:8080/swagger-ui/index.html` (if enabled)

---

## 🧪 Testing

Run all tests:

```shell
./mvnw test
```

---

## 🤝 Contributing

1. Fork the repository
2. Create a new branch: `git checkout -b feature/your-feature`
3. Make your changes
4. Commit and push: `git commit -m "feat: your message" && git push`
5. Open a pull request

---

## 📄 License

MIT License. See [LICENSE](./LICENSE) for details.

---

## 💬 Contact

- Issues: [GitHub Issues](https://github.com/PKWMApp/PKWMTT-backend/issues)
- Team: [@PKWMApp](https://github.com/PKWMNTeam)

---

## 🌐 Related Projects

- [PKWM Mobile App](https://github.com/PKWMApp/PKWMTT-frontend-mobile)
- [PKWM Web App](https://github.com/PKWMApp/PKWMTT-frontend-web)

---

## 📸 Screenshots

*(Add screenshots here if desired)*
