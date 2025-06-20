#### 脚本解释

读取 `../data/ucsd` 内的文件，替换 MySQL 的表，包括

- `../cleaned_data/ucsd/final_table/courses/`
- `../cleaned_data/ucsd/final_table/professors/`
- `../cleaned_data/ucsd/final_table/courses_professors/`
- `../cleaned_data/ucsd/final_table/enrollment_snapshots/`
- `../cleaned_data/ucsd/final_table/passtimes.csv`
  > 注意，`../data/ucsd` 内的文件应当是从 S3 下载的最新文件。如果不是最新文件，请首先更新其数据。

#### 过程

- 启用 conda 环境 `conda activate mysql_import`
- 如果不是我的本机，可以根据`enviroment.yml`重建环境
- 运行`python mysql_data_update.py`
  - 注意，此脚本目前会对数据库的表直接替换，而非更新。
  - 对于 `courses_professors` 和 `enrollment_snapshots` 两张表，耗时会很长，约各 2 分钟。
  - 此脚本需要在未来进行更新，使用更为简便，节约，安全的方式
