# MockController Test Suite

This repository contains a unit test for the `MockController` in a Spring Boot application using `@WebMvcTest`. It focuses on verifying the `/api/v1/hello` endpoint and includes Spring Security bypass configuration for testing purposes.

## 📄 Overview

- **Test Type**: Unit test (controller layer only)
- **Frameworks**: Spring Boot, JUnit 5, MockMvc
- **Security**: Bypassed using `@WithMockUser` and custom `SecurityConfig`
- **Target Endpoint**: `GET /api/v1/hello`

## 🧪 How It Works

The test class uses:
- `@WebMvcTest` to load only the web layer
- `MockMvc` to simulate HTTP requests
- `@WithMockUser` to mock an authenticated user
- `@Import(SecurityConfig.class)` to override security filters for testing

## ✅ Example Test Case

```java
@WithMockUser
@Test
public void getHello() throws Exception {
    mockMvc.perform(get("/api/v1/hello"))
           .andExpect(status().isOk())
           .andExpect(content().string("Hello"));
}
