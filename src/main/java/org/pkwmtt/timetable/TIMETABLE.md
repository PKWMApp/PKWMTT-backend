# 📅 Timetable API

A Spring Boot REST API that provides academic timetable data by general group name. Includes optional filtering and utility endpoints.

---

## 🚀 Base URL

```
http://localhost:8080/pkmwtt/api/v1/timetables/
```
---

## 🔧 Endpoints

### 1. **Get Timetable by Group Name**

```
GET /{generalGroupName}

Optional Query Parameters:
Param	Example
k	K01
l	L01
p	P01
```
Example Request:
```
GET .../pkmwtt/api/v1/timetables/12K1?k=K01&l=L01&p=P01
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
          "name": "PInterfUż",
          "classroom": "J207.1",
          "rowId": 0,
          "type": "W"
        },
        "..."
      ],
      "even": [
        {
          "name": "Proj3D",
          "classroom": "J207.1",
          "rowId": 0,
          "type": "K01"
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
Example Response:
```json
{
  "hours": [
    "7:30–8:15",
    "8:15–9:00",
    "9:15–10:00",
    "10:00–10:45",
    "..."
  ]
}
```

### 3. **Get list of general groups**
```
GET /groups/general
```
Example Response:

```json
[
  "11A1",
  "11A2",
  "11A3",
  "11B1",
  "..."
]
```

### 4. **Get list of of available subgroups for general group**
```
GET /groups/{generalGroupName}
```
Example Response:

```json
[
  "K01",
  "K04",
  "L01",
  "L02",
  "L04",
  "P01",
  "P04"
]
```


