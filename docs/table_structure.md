#### Table Deploy

- AWS RDS (MySQL)

#### Table Structure

- Professors

  - 存储教授信息
  - `prof_id`: 主键
  - `prof_last_name`: 教授的姓, 不可为 null
  - `prof_first_name`: 教授的名, 可为 null
  - `prof_middle_name`: 中间名, 可谓 null

- Courses

  - 存储课程信息
  - `course_offering_id`: 主键
  - `department`: 部门
  - `course_id`: 课程编号
  - `instructor`: 关键属性，讲师就是原表的 `prof` 列，一门课的讲师是识别其的重要属性之一。
    - 教授表只包含单独的教授信息
    - 原表中的课程有 `department`, `course_id` 等信息
    - 多个教授可能教授多门课程，同一门 `department-course_id` 可能同时开了多门课，由不同教授教授
    - 因此，如果没有 `instructor` 列，那么多门课 (`department-course_id-instructor`) 就会被认定为一门课 ((`department-course_id`))，被连接至所有教授这门课的教授
  - `year`: 学年
  - `quarter`: 季度
  - `total`: 总座位数

- Course_Professors

  - 链接表，链接课程和教授。为什么不做列？：因为同一门课可能有多个教授。
  - `course_offering_id`: 外键，连接到 Course 表
  - `prof_id`: 外键，链接到 Professors 表

- Enrollment_Snapshots

  - 注册数据快照表
  - `course_offering_id`: 外键，连接到 Course 表
  - `date`: 日期
  - `enrolled_ct`: 注册人数
  - `waitlist`: 候补名单人数

- passtimes

  - 每个年份-季度的注册时间
  - `year`: 年份
  - `quarter`: 季度
  - `passtag`: 对应的 passtag，例如 Prior, First Pass ...
  - `passtime`: 对应 年份-季度-passtag 的注册时间，格式为 `YYYY-MM-DD`

- course_professor_comments
  - `comment_id`: 评论 id，主键，**_自增，无需手动处理_**
  - `updated_at`: 上传日期，timestamp，**_自增，无需手动处理_**
  - `course_offering_id`: 对应的课程 id
  - `prof_id`: 对应的教授 id
  - `comment`: 评论内容
