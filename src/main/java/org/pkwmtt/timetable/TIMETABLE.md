# 📅 Timetable API

A Spring Boot REST API that provides academic timetable data by general group name. Includes optional filtering and utility endpoints.

---

## 🚀 Base URL

```
http://localhost:8080/pkmwtt/api/v1/timetable/
```
---

## 🔧 Endpoints

### 1. **Get Timetable by Group Name**

```http
GET /{generalGroupName}

Optional Query Parameters:
Param	Example
k	K01
l	L01
p	P01
```
Example Request:
```
GET .../pkmwtt/api/v1/timetable/12K1?k=K01&l=L01&p=P01
```
Example Response:

```json
{
  "name": "12K1",
  "data": [
    {
      "name": "Poniedziałek",
      "odd": [
        {
          "name": "PInterfUż W-(N)",
          "classroom": "J207.1-n",
          "rowId": 0
        },
        "..."
      ],
      "even:": [
        {
          "name": "Proj3D W-(P",
          "classroom": "J207.1-p",
          "rowId": 0
        },
        "..."
      ]
    }
  ]
}
```

### 2. **Get List of Academic Hours**
```
GET /hours
```
