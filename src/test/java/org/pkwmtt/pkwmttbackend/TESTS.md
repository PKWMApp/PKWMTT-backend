# 🧪 MockController Integration Test

This project demonstrates how to write a Spring Boot integration test using `@SpringBootTest` with a random port, `TestRestTemplate`, and JUnit 5. The `MockControllerTest` class tests the basic functionality of a REST controller.

## 🚀 Setup

The test class uses several Spring testing annotations to configure the environment:

- `@ExtendWith(SpringExtension.class)`: Integrates Spring's testing support with JUnit 5.
- `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)`: Boots up the full Spring application context with a random port to simulate real HTTP requests.
- `@LocalServerPort`: Injects the generated port into the test.
- `@Autowired TestRestTemplate`: Used to make HTTP requests to the running application.

## ✅ Test Case

### `getHello()`

This method tests the `/api/v1/hello` endpoint by making an HTTP GET request and checking:

- The response status is `200 OK`.
- The response body contains `"Hello"`.

## 📂 Running Tests

You can run the tests using your IDE or the command line:

```bash
./mvnw test
