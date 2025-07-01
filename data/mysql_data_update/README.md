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
- 运行`python mysql_update.py`
  - 注意，此脚本会先将本地数据上传至临时表，而后运行 `INSERT ... ON DUPLICATE` 来更新旧表。
  - 注意，此脚本依赖于环境变量中的数据库链接设置，保证你在环境变量，或者当前的.env文件中包含:
    - `DB_HOST=`
    - `DB_NAME=`
    - `DB_USER=`
    - `DB_PASS=`
