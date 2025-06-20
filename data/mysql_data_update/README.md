#### 脚本解释
读取 `../data/ucsd` 内的文件，替换MySQL的表，包括
- `../cleaned_data/ucsd/final_table/courses/`
- `../cleaned_data/ucsd/final_table/professors/`
- `../cleaned_data/ucsd/final_table/courses_professors/`
- `../cleaned_data/ucsd/final_table/enrollment_snapshots/`
- `../cleaned_data/ucsd/final/passtimes.json`

#### 过程
- 启用conda环境 `conda activate mysql_import`
- 如果不是我的本机，可以根据`enviroment.yml`重建环境
- 运行`python mysql_data_update.py`
    - 注意，此脚本目前会对数据库的表直接替换，而非更新。
