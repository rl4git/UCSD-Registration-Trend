# 静态化迁移方案

这个项目目前的数据量很小，网站交互也主要是按课程查询并画图。因此当前推荐架构是：

```text
old_frontend/public
├── index.html
├── css/
├── js/
└── data/
    ├── course-manifest.json
    └── courses/
        ├── CSE_120.json
        ├── CSE_110.json
        └── ...
```

## 为什么这样改

之前的 AWS EC2 + Spring Boot + RDS MySQL 架构适合练习完整后端部署，但对当前个人网站来说维护成本和云费用都偏高。

静态化以后，线上运行时不再需要：

- EC2
- RDS / MySQL
- Spring Boot 服务
- Nginx 后端反向代理
- 后端 API 常驻进程

用户访问页面时，浏览器直接请求预生成的 JSON 文件。

## 生成静态数据

在项目根目录运行：

```bash
python data/generate_static_course_data.py
```

脚本会读取：

```text
data/cleaned_data/ucsd/final_table/
```

然后输出：

```text
old_frontend/public/data/course-manifest.json
old_frontend/public/data/courses/*.json
```

脚本会按主键去重，因为当前 `final_table` 目录里有一些重复的 Spark/Delta 导出文件。

## 前端查询方式

旧前端现在会把用户输入的课程转换成静态文件名。

例如：

```text
CSE 120 -> data/courses/CSE_120.json
MATH 20C -> data/courses/MATH_20C.json
```

如果文件不存在，前端会显示课程不存在，而不是报网络错误。

## 推荐部署

首选 Cloudflare Pages：

- Build command: 留空
- Build output directory: `old_frontend/public`
- 自定义域名指向 Pages 项目

备选 GitHub Pages：

- 发布 `old_frontend/public`
- 或者用 GitHub Actions 把这个目录部署到 Pages

确认线上静态站点工作后，可以停止 AWS EC2 和 RDS，避免继续产生费用。

## 暂时不处理的内容

课程数据清洗和更新流程暂时保持原样。

评论功能、点赞、点踩等动态功能当前不迁移。如果以后需要恢复，建议使用 Cloudflare Workers + D1，而不是重新启用 EC2 + RDS。
