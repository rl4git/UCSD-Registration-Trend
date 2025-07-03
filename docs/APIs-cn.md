### 获取全部 department 列表

- **Endpoint**: `GET /api/courses/departments`
- **参数**：无
- **完整请求示例**：
  - `/api/courses/departments`
- **成功返回 (200 OK)**
  ```json
  [
  	"SE",
  	"GLBH",
  	"BGRD",
  	"LTSP",
  	"MUS",
  	...,
  	"MATH",
  ]
  ```

### 返回特定 department 下的全部 course_id 列表

- **Endpoint**: `GET /api/courses/{department}/ids`
- **参数**：
  - `department`: （必填）系的标识符，例如 `CSE`, `MATH`
- **完整请求示例**：
  - `api.ucsdregistration.com/api/courses/CSE/ids`
- **成功返回 (200 OK)**
  ```json
  [
  	"176E",
  	"293",
  	"290",
  	"150A",
  	"182",
  	"209B",
  	"140L",
  	"20",
  	"253R",
  	...,
  	"118"
  ]
  ```

### 获取特定教授的全部课程信息

- **Endpoint**: `GET /api/courses/search`
- **参数(查询参数 Query Parameters)**
  - `profFirstName`: 教授名
  - `profLastName`: 教授姓
- **完整请求示例**:
  - `/api/courses/search?profFirstName=Richard&profLastName=Averitt`
- **成功返回 (200 OK)**:
  ```json
  [
    {
      "courseOfferingId": "1340ab5c862a6667ff7e74eff8ac11121d30eb90bdb1a89880c0708df451c9a4",
      "department": "PHYS",
      "courseId": "4A",
      "instructor": "Richard Averitt",
      "year": 2025,
      "quarter": "Winter",
      "total": 180,
      "professors": [
        {
          "profId": "2df9d063f34fd89a694854c336b313b541477244795bc6a28f31b1e8c6ccefc4",
          "profFirstName": "Richard",
          "profLastName": "Averitt",
          "profMiddleName": "Douglas"
        }
      ]
    },
    {
      "courseOfferingId": "177c688f3e702254655cfcfb45144c34b82de86840f4226659238db712787f46",
      "department": "PHYS",
      "courseId": "4A",
      "instructor": "Richard Averitt",
      "year": 2024,
      "quarter": "Winter",
      "total": 146,
      "professors": [
        {
          "profId": "2df9d063f34fd89a694854c336b313b541477244795bc6a28f31b1e8c6ccefc4",
          "profFirstName": "Richard",
          "profLastName": "Averitt",
          "profMiddleName": "Douglas"
        }
      ]
    }
  ]
  ```

### 返回特定课程的信息（给定 department + course id）

- **Endpoint**: `GET /api/courses/{department}/{courseId}`
- **参数**
  - `department`: （必填）系的标识符，例如 `CSE`, `MATH`
  - `courseId`: （必填）课程 id，例如 `120`, `110`
- **完整请求示例**:
  - 忽略大小写（后端会统一转为小写比较）
  - `/api/courses/CSE/120`
- **成功返回 (200 OK)**:
  ```json
  [
    {
      "courseOfferingId": "08bb2302174fab75dd2f588e20aa9226ff8d99412c634ace7785e843ebfe52eb",
      "department": "CSE",
      "courseId": "120",
      "instructor": "Joseph Pasquale",
      "year": 2025,
      "quarter": "Winter",
      "total": 202,
      "professors": [
        {
          "profId": "7665d26836e9dcc64aec6a1f10bf871e9452e2792738870ff6dc145a6919c229",
          "profFirstName": "Joseph",
          "profLastName": "Pasquale",
          "profMiddleName": null
        }
      ]
    },
    {
      "courseOfferingId": "b51a6cff4a8e9edeb1501e1a89043ee08e57bc16f5ba44d6df4377a2c445b83e",
      "department": "CSE",
      "courseId": "120",
      "instructor": "Amy Ousterhout",
      "year": 2025,
      "quarter": "Spring",
      "total": 328,
      "professors": [
        {
          "profId": "99df9b38a03b154e2d9adfd54ee9ce1787a1497c707885b06f277999d800eaa6",
          "profFirstName": "Amy",
          "profLastName": "Ousterhout",
          "profMiddleName": null
        }
      ]
    }
  ]
  ```

### 获取特定系课程的注册数据

- **Endpoint**: `GET /api/enrollments/{courseOfferingId}`
- **参数(Path Variables)**
  - `course_offering_id`: 课程的主键字符串，可以根据其他接口获取
- **完整请求示例**:
  - 忽略大小写（后端会统一转为小写比较）
  - `/api/enrollments/08bb2302174fab75dd2f588e20aa9226ff8d99412c634ace7785e843ebfe52eb`
- **成功返回 (200 OK)**:
  ```json
  [
  	{
  		"date": "2024-11-11",
  		"enrolled": 0,
  		"waitlist": 0
  	},
  	{
  		"date": "2024-11-12",
  		"enrolled": 0,
  		"waitlist": 0
  	},
  	...,
  	{
  		"date": "2025-01-19",
  		"enrolled": 199,
  		"waitlist": 1
  	}
  ]
  ```
- **成功返回 (200 OK) 特殊情况**:
  如果相关课程不存在，我也会返回 200，但是 json 会为空
  ```json
  {}
  ```
---

### 获取所有已知的 passtime 和 passtag
- **注意**: 这个请求返回的数据比较大，在网站开始加载的时候请求一次，然后放到缓存里- **注意**: 这个请求返回的数据比较大，在网站开始加载的时候请求一次，然后放到缓存里，供后续使用即可。
- **Endpoint**: `GET /api/passtimes`
- **完整请求示例**: `/api/passtimes`
- **成功返回** (请自行用JSON FORMAT工具查看)
  ```json
  [{"passtime":"2024-03-01","year":2024,"quarter":"Spring","passtag":"Second Pass First-Year Start"},{"passtime":"2024-05-28","year":2024,"quarter":"Fall","passtag":"First Pass Sophmores Start"},{"passtime":"2024-11-22","year":2025,"quarter":"Winter","passtag":"Second Pass Sophmores Start"},{"passtime":"2023-11-21","year":2024,"quarter":"Winter","passtag":"Second Pass Priorities & Seniors Start"},{"passtime":"2025-02-14","year":2025,"quarter":"Spring","passtag":"Prior"},{"passtime":"2024-11-19","year":2025,"quarter":"Winter","passtag":"Second Pass Priorities & Seniors Start"},{"passtime":"2025-02-28","year":2025,"quarter":"Spring","passtag":"Second Pass First-Year Start"},{"passtime":"2024-02-17","year":2024,"quarter":"Spring","passtag":"First Pass Priorities & Seniors Start"},{"passtime":"2024-11-12","year":2025,"quarter":"Winter","passtag":"First Pass Priorities & Seniors Start"},{"passtime":"2024-02-22","year":2024,"quarter":"Spring","passtag":"First Pass First-Year Start"},{"passtime":"2023-11-18","year":2024,"quarter":"Winter","passtag":"First Pass First-Year Start"},{"passtime":"2025-02-19","year":2025,"quarter":"Spring","passtag":"First Pass Sophmores Start"},{"passtime":"2024-02-26","year":2024,"quarter":"Spring","passtag":"Second Pass Priorities & Seniors Start"},{"passtime":"2023-11-14","year":2024,"quarter":"Winter","passtag":"First Pass Priorities & Seniors Start"},{"passtime":"2024-11-11","year":2025,"quarter":"Winter","passtag":"Prior"},{"passtime":"2024-11-21","year":2025,"quarter":"Winter","passtag":"Second Pass Juniors Start"},{"passtime":"2025-02-15","year":2025,"quarter":"Spring","passtag":"First Pass Priorities & Seniors Start"},{"passtime":"2024-01-11","year":2024,"quarter":"Winter","passtag":"A Week After Quarter Start"},{"passtime":"2024-02-28","year":2024,"quarter":"Spring","passtag":"Second Pass Juniors Start"},{"passtime":"2024-11-15","year":2025,"quarter":"Winter","passtag":"First Pass Sophmores Start"},{"passtime":"2024-02-20","year":2024,"quarter":"Spring","passtag":"First Pass Juniors Start"},{"passtime":"2024-06-04","year":2024,"quarter":"Fall","passtag":"Second Pass Juniors Start"},{"passtime":"2025-03-26","year":2025,"quarter":"Spring","passtag":"Quarter Start"},{"passtime":"2024-02-21","year":2024,"quarter":"Spring","passtag":"First Pass Sophmores Start"},{"passtime":"2025-01-18","year":2025,"quarter":"Winter","passtag":"A Week After Quarter Start"},{"passtime":"2025-01-02","year":2025,"quarter":"Winter","passtag":"Quarter Start"},{"passtime":"2024-02-29","year":2024,"quarter":"Spring","passtag":"Second Pass Sophmores Start"},{"passtime":"2023-11-17","year":2024,"quarter":"Winter","passtag":"First Pass Sophmores Start"},{"passtime":"2025-04-02","year":2025,"quarter":"Spring","passtag":"A Week After Quarter Start"},{"passtime":"2025-02-26","year":2025,"quarter":"Spring","passtag":"Second Pass Juniors Start"},{"passtime":"2024-05-24","year":2024,"quarter":"Fall","passtag":"First Pass Priorities & Seniors Start"},{"passtime":"2025-02-24","year":2025,"quarter":"Spring","passtag":"Second Pass Priorities & Seniors Start"},{"passtime":"2025-02-18","year":2025,"quarter":"Spring","passtag":"First Pass Juniors Start"},{"passtime":"2024-05-29","year":2024,"quarter":"Fall","passtag":"First Pass First-Year Start"},{"passtime":"2025-02-20","year":2025,"quarter":"Spring","passtag":"First Pass First-Year Start"},{"passtime":"2024-11-16","year":2025,"quarter":"Winter","passtag":"First Pass First-Year Start"},{"passtime":"2024-06-01","year":2024,"quarter":"Fall","passtag":"Second Pass Priorities & Seniors Start"},{"passtime":"2024-11-23","year":2025,"quarter":"Winter","passtag":"Second Pass First-Year Start"},{"passtime":"2024-05-23","year":2024,"quarter":"Fall","passtag":"Prior"},{"passtime":"2024-06-06","year":2024,"quarter":"Fall","passtag":"Second Pass First-Year Start"},{"passtime":"2023-11-16","year":2024,"quarter":"Winter","passtag":"First Pass Juniors Start"},{"passtime":"2024-05-27","year":2024,"quarter":"Fall","passtag":"First Pass Juniors Start"},{"passtime":"2024-06-05","year":2024,"quarter":"Fall","passtag":"Second Pass Sophmores Start"},{"passtime":"2024-09-30","year":2024,"quarter":"Fall","passtag":"A Week After Quarter Start"},{"passtime":"2023-11-27","year":2024,"quarter":"Winter","passtag":"Second Pass First-Year Start"},{"passtime":"2024-02-16","year":2024,"quarter":"Spring","passtag":"Prior"},{"passtime":"2024-03-27","year":2024,"quarter":"Spring","passtag":"Quarter Start"},{"passtime":"2024-04-04","year":2024,"quarter":"Spring","passtag":"A Week After Quarter Start"},{"passtime":"2024-09-23","year":2024,"quarter":"Fall","passtag":"Quarter Start"},{"passtime":"2023-11-24","year":2024,"quarter":"Winter","passtag":"Second Pass Juniors Start"},{"passtime":"2024-01-04","year":2024,"quarter":"Winter","passtag":"Quarter Start"},{"passtime":"2025-02-27","year":2025,"quarter":"Spring","passtag":"Second Pass Sophmores Start"},{"passtime":"2023-11-13","year":2024,"quarter":"Winter","passtag":"Prior"},{"passtime":"2024-11-14","year":2025,"quarter":"Winter","passtag":"First Pass Juniors Start"},{"passtime":"2023-11-25","year":2024,"quarter":"Winter","passtag":"Second Pass Sophmores Start"}]
  ```


### 获取特定年份季度的 Passtime 和 Passtag
- **Endpoint**: `GET /api/passtimes/{year}/{quarter}`
- **参数(Path Variable)**
  - `year`: 年份，必填
  - `quarter`: 季度，必填
- **完整请求示例**: `/api/passtimes/2024/fall`
- **成功返回**
  ```json
  
  ```


---

### 获取特定课程（教授）的评论

- **Endpoint**: `GET /api/comments/search`
- **参数(查询参数 Query Parameters)**
  - `courseOfferingId`: 课程主键id，注意不是课程id。
  - `profId`: (可选): 教授主键id，注意不是教授名。可选。
- **完整请求示例**:
  - 忽略大小写（后端会统一转为小写比较）
  - `/api/comments/search?courseOfferingId=08bb2302174fab75dd2f588e20aa9226ff8d99412c634ace7785e843ebfe52eb`
  - `api.ucsdregistration.com/api/comments/search?courseOfferingId=08bb2302174fab75dd2f588e20aa9226ff8d99412c634ace7785e843ebfe52eb&profId=7665d26836e9dcc64aec6a1f10bf871e9452e2792738870ff6dc145a6919c229`
- **成功返回 (200 OK)**
  ```json
  
  ```
- **失败返回 (400 )**:
  ```json
  
  ```

### 上传用户评价

- **Endpoint**: `POST /api/comments`
- **请求体 Request Body**
  ```json
  {
    "courseOfferingId": "课程主键id",
    "profId": "教授主键id",
    "commentContent": "评论内容"
  }
  ```
- **成功响应 (200 OK)**
  ```json
  
  ```
- **失败响应 (400)**
  ```json
  
  ```

### 给评论点赞

- **注释**：点赞和点踩都要加延迟，不然有人恶意连续快速点击点赞点踩，每一次点击都发一个请求就卡死了。
- **Endpoint**: `PUT /api/comments/{commentId}/like`
- **参数 (路径参数)**

  - `commentId`: 必选

- **成功响应**:
  ```json
  
  ```

### 给评论点踩

- **注释**：点赞和点踩都要加延迟，不然有人恶意连续快速点击点赞点踩，每一次点击都发一个请求就卡死了。
- **Endpoint**: `PUT /api/comments/{commentId}/dislike`
- **参数 (路径参数)**

  - `commentId`: 必选

- **成功响应**:
  ```json
  
  ```
