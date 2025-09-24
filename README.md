# 🚀 PKWMTT Backend

---

## 📦 Tech Stack

- **Backend Framework:** [Java Spring](https://spring.io/)
- **Language:** [Java](https://www.java.com/pl/)
- **Database:** [MySQL](https://www.mysql.com/)
- **Authentication:** [JWT](https://jwt.io/)
- **Project Manager:** [Maven](https://maven.apache.org/)
- **Containerization:** [Docker](https://www.docker.com/)

---

## ⚙️ Setup & Installation

### 1. Clone the repository

```shell
docker pull ghcr.io/pkttteam/pkwmtt-backend:latest
```

### 2. Run

```shell
docker run -d --name [image_name] -p 8080:8080 ghcr.io/pkttteam/pkwmttt-backend:[PACKAGE_NUMBER]
```

---

## 📮 API Overview

The backend exposes various RESTful endpoints to manage:

- Timetable:
    - Schedule for specific general group with optional filters (K,L,P groups)
    - List of available general groups (f.e. 12K1)
    - List of subjects hours
    - List of available KLP groups for specified general group (f.e. K01)

The API follows standard REST conventions and uses JWT for authentication. Headers typically include:

```
Authorization: Bearer <token>
Content-Type: application/json
```

> ⚠️ API documentation with Swagger may be available [here](http://localhost:8080/swagger-ui/index.html) if enabled in
> the application.

---

## 🧪 Testing

```shell
mvn test
```

---

## 🤝 Contributing

Contributions are welcome! Follow these steps:

1. Fork the repository
2. Create a new branch: `git checkout -b feature/your-feature-name`
3. Make your changes
4. Commit and push: `git commit -m "feat: add new feature" && git push`
5. Submit a pull request 🚀

---

## 📄 License

This project is licensed under the **MIT License**. See the [LICENSE](./LICENSE) file for details.

---

## 💬 Contact

For questions, suggestions, or collaboration:

- GitHub Issues: [Submit here](https://github.com/PKWMApp/PKWMTT-backend/issues)
- Team: [@PKTTTeam](https://github.com/PKWMApp
  )

---

## 🌐 Related Projects

---

## 📸 Screenshots (Optional)

---



