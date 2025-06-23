#### 获取全部 department 列表
- **Endpoint**: `GET /departments`
- **参数**：无
- **完整请求示例**：
	- `/departments`
- **成功返回 (200 OK)**
	```json
	{
	    "departments": [
	        "CSE", "MATH", "PHYS", ...
	    ]
	}
	```

#### 获取全部 department course_id 列表
- 注意，这个单次请求的返回体会很大，但是如果你缓存整个列表的话，发送请求的数量会比 "不停查找特定department的course_id" 少很多。
- **Endpoint**: `GET /departments/courses`
- **参数**：无
- **完整请求示例**：
	- `/departments/courses/`
- **成功返回 (200 OK)**
	```json
	{
	    "departments": [
	        {
                "department": "CSE", 
                "course_id": ["30", "120", ...]}, 
            {
                "department": "MATH", 
                "course_id": ["10", "100", ...]},
            {
                "department": "PHYS", 
                "course_id": ["23", "100D", ...]},
	    ]
	}
	```

#### 返回特定 department 下的全部 course_id 列表
- **Endpoint**: `GET /departments/courses`
- **参数**：
	- `department`: （必填）系的标识符，例如 `CSE`, `MATH`
- **完整请求示例**：
	- `/departments/courses?department=CSE`
- **成功返回 (200 OK)**
	```json
	{
	    "department": "CSE",
	    "course_id":[
	        "15L", "30", "120", ...
	    ]
	}
	```

#### 获取特定 department course 信息 (年份，季度，instructor)
- **Endpoint**: `GET /courses`
- **参数(查询参数 Query Parameters)**
	- `department`: （必填）系的标识符，例如 `CSE`, `MATH`
	- `course_id`: （必填）课程id，例如 `120`, `110`
- **完整请求示例**: 
	- 忽略大小写（后端会统一转为小写比较）
	- `/courses?department=CSE&course_id=120`
- **成功返回 (200 OK)**:
	```json
		{
	    "courses":[
	        {
	            "year":2024, "quarter":"Fall", "total": 200,
	            "instructor":["prof_first_name prof_last_name", "prof_first_name prof_last_name"]
	        },
	        {
	            "year":2024, "quarter":"Fall", "total": 200,
	            "instructor":["prof_first_name prof_last_name", "prof_first_name prof_last_name"]
	        }
	    ]
	}
	```


#### 获取特定系课程的注册数据
- 为了避免多次请求的开销，你可以选择一次性请求 `department` + `course_id` 的全部数据，缓存到浏览器，然后根据需求在前端过滤。不然每一门特定的教授/季度都发一次请求，压力会比较大。其他的请求也是同理。
- **Endpoint**: `GET /enrollment_snapshots`
- **参数(查询参数 Query Parameters)**
	- `department`: （必填）系的标识符，例如 `CSE`, `MATH`
	- `course_id`: （必填）课程id，例如 `120`, `110`
	- `professor`: （可选）教授名字，格式为 `prof_first_name prof_last_name`
	- `year`: （可选）
	- `quarter`：（可选）
- **完整请求示例**: 
	- 忽略大小写（后端会统一转为小写比较）
	- `/enrollment_snapshots?department=CSE&course_id=120`
	- `/enrollment_snapshots?department=CSE&course_id=120&professor=Geoffrey%20Voelker&year=2024&quarter=fall`
- **成功返回 (200 OK)**:
	```json
	{
		"department": "CSE",
		"course_id": "120",
	    // 每个不同教授，不同季度的课程的注册数据都会被放到 courses 数组里
		"courses":[
	    {
	        "year": "2024",
	        "quarter": "Fall",
	        "total": 250,
	        "instructor":["prof_first_name prof_last_name", "prof_first_name prof_last_name"]
	        "enrollment_snapshots": [
	            {"passtag": "Prior", "date": "2024-05-23",  "enrolled_ct": 0, "waitlist": 0},
	            {"passtag": "First Pass Priorities & Seniors Start", "date": "2024-05-24",  "enrolled_ct": 0, "waitlist": 0},
	            // ...
	            {"passtag": "A Week After Quarter Start", "date": "2024-09-30",  "enrolled_ct": 251, "waitlist": 31}
	        ]
	    },
	    { 
	        "year": "2024",
	        "quarter": "Winter",
	        "instructor":["prof_first_name prof_last_name", "prof_first_name prof_last_name"]
	        "total": 250,
	        "enrollment_snapshots": [
	            {"passtag": "Prior", "date": "2024-05-23",  "enrolled_ct": 0, "waitlist": 0},
	            {"passtag": "First Pass Priorities & Seniors Start", "date": "2024-05-24",  "enrolled_ct": 0, "waitlist": 0},
	            // ...
	            {"passtag": "A Week After Quarter Start", "date": "2024-09-30",  "enrolled_ct": 251, "waitlist": 31},
	        ]
	    }]
	}
	```
- **成功返回 (200 OK) 特殊情况**:
		如果相关课程不存在，我也会返回 200，但是json会为空
	```json
	{}
	```
- **失败返回 (400 )**:
	```json
	{
		"error":"错误信息，例如 department cannot be empty."
	}
	```

#### 获取特定课程（教授）的评论
- **Endpoint**: `GET /comments`
- **参数(查询参数 Query Parameters)**
	- `department`: （必填）系的标识符，例如 `CSE`, `MATH`
	- `course_id`: （必填）课程id，例如 `120`, `110`
	- `professor`: （可选）教授名字，格式为 `prof_first_name prof_last_name`
- **完整请求示例**: 
	- 忽略大小写（后端会统一转为小写比较）
	- `/comments?department=CSE&course_id=120`
	- `/comments?department=CSE&course_id=120&professor=Geoffrey%20Voelker`
- **成功返回 (200 OK)**
	```json
	{
		"department": "CSE",
		"course_id": "120",
		"comments":[
	        {
		        "comment_id": "asdjbaskndjafsbams",
	            "professor": "prof_first_name prof_last_name", 
	            "year": 2024,
	            "quarter": "Fall", 
	            "comment_date": "2025-05-11",
	            "comment": "comment content",
	            "rate": 2.5,
	            "like": 5,
	            "dislike": 5
	        },
	        {
		        "comment_id": "fdjgnsdujdhknkasdka",
	            "professor": "prof_first_name prof_last_name",
	            "year": 2024,
	            "quarter": "Fall", 
	            "comment_date": "2025-06-12",
	            "comment": "comment content",
	            "rate": 5.0,
	            "like": 12,
	            "dislike": 8
	        }
	    ]
	}
	```
- **失败返回 (400 )**:
	```json
	{
		"error":"错误信息，例如 department cannot be empty."
	}
	```

#### 上传用户评价
- **Endpoint**: `POST /comments`
- **请求体 Request Body**
	```json
	{
	    "department": "CSE",
	    "course_id": "120",
	    "professor": "prof_first_name prof_last_name",
	    "year": 2024,
	    "quarter": "Fall",
	    "rate": 2.5,
	    "comment": "This course sucks."
	}
	```
- **成功响应 (200 OK)**
	```json
	{
		"message": "Comment created.",
		"comment_id": "asdnkhabjdsna"
	}
	```
- **失败响应 (400)**
	```json
	{
		"message": "Request body incorrect format."
	}
	```

#### 给评论点赞
- **注释**：点赞和点踩都要加延迟，不然有人恶意连续快速点击点赞点踩，每一次点击都发一个请求就卡死了。
- **Endpoint**:  `POST /comments/{comment_id}/likes`
- **参数 (路径参数)**
	- `comment_id`: 必选 

- **成功响应**:
	```json
	{
		"message": "Like added successfully."
	}
	```

#### 给评论点踩
- **注释**：点赞和点踩都要加延迟，不然有人恶意连续快速点击点赞点踩，每一次点击都发一个请求就卡死了。
- **Endpoint**:  `POST /comments/{comment_id}/unlikes`
- **参数 (路径参数)**
	- `comment_id`: 必选 

- **成功响应**:
	```json
	{
		"message": "Unlike added successfully."
	}
	```
