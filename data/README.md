### Introduction

这是一个用来清理数据，上传数据到 RDS 的脚本的文件夹。

应当在每个注册季后，按照下面的顺序运行一次，以更新数据库。

文件夹的内容：

- `./data_cleaning/`: 包含用来清理 S3 的原始数据，并保存到 S3 的脚本
- `./cleaned_data/`：保存从 S3 下载的数据（清理后的数据，可以直接上传到 RDS）
- `./mysql_data_update/`: 包含将 `./cleaned_data/` 的数据上传到 RDS 的脚本。

### 1. Clean Data

#### Clean raw data

1. 先从原始数据库下载需要的季度的 raw data 到 S3

   - 原始数据库链接：`https://github.com/UCSD-Historical-Enrollment-Data/UCSDHistEnrollData.git`
   - S3 路径：`ucsd/raw/{year}{Quarter}/*.csv` Quarter 的首字母大写，例如 `ucsd/raw/2024Winter/....csv`

2. 打开第一个 python notebook: `./data_cleaning/Data Cleaning ...`

   - 在前面的单元格内设置 AWS S3 链接需要的 `access key`, `secret key`, `region`, `bucket name`
   - 向下，检查 passtimes 的基础设置。将年份，季度，passtime 修改为你的原始数据所对应的 passtime。

3. 运行整个 `Data Cleaning` notebook，脚本会从 S3，根据 passtimes 读取需要的原始数据，清理后保存回 S3。

4. 数据应当会被保存到：

- 每个季度的清理数据：`ucsd/cleaned/{}year{quarter}/*.csv`
- 每个季度清理后数据的汇总：`ucsd/final/fianl/*.csv`
- passtimes (json 格式): `ucsd/final/passtimes.json`
- passtimes (csv 格式): `ucsd/final_table/passtimes/*.csv`
  > 关于数据的信息，请阅读 notebook

#### Make tables

1. 打开第二个 notebook：`./data_cleaning/Table Creation ...`

- 在前面的单元格内设置 AWS S3 链接需要的 `access key`, `secret key`, `region`, `bucket name`

2. 运行 notebook，脚本会从 S3，根据 passtimes 读取需要的数据，清理后保存回 S3。

3. 数据应当会被保存到：

- 每张表的数据：`ucsd/final_table/{对应的表名}/*.csv`

### 2. 下载数据到 EC2

- 将表的数据保存到 `./cleaned_data/ucsd/final_table/{对应的表名}/*.csv`
- 可以手动下载上传，或者使用 aws cli

#### AWS CLI 配置

- 安装 AWS CLI

  ```bash
  # 如果遇到权限不足，就在指令前面加个 sudo
  # 更新包管理软件
  sudo apt update

  # 安装awscli，提供一系列和aws交互的命令
  # [安装或更新最新版本的 AWS CLI - AWS Command Line Interface](https://docs.aws.amazon.com/zh_cn/cli/latest/userguide/getting-started-install.html)
  # 根据AWS官网上的描述，依次运行：
  curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
  sudo unzip awscliv2.zip
  sudo ./aws/install
  ```

- 进入 AWS IAM，为 AWS CLI 创建新角色
  - 需要 `aws s3 read only` 权限
  - 需要 `aws rds full access` 权限
- 创建 access key，获取 access key 和 secret key
- 回到 EC2，输入命令 `aws configure`，依次输入
  - aws access key
  - aws secret key
  - region
  - 返回数据格式，可选，比如 json
- 从 S3 下载数据
  ```bash
  # 下载单个文件
  aws s3 cp s3://桶名/路径名/文件名 本地路径/文件名
  # 下载文件夹
  aws s3 cp s3://桶名/路径名/ 本地路径/ --recursive
  ```

### 3. 运行脚本，上传数据至 RDS

- `mysql_data_update.py` 会读取 `../data/ucsd` 内的文件，替换 AWS RDS MySQL 的表，包括
  - `../cleaned_data/ucsd/final_table/courses/`
  - `../cleaned_data/ucsd/final_table/professors/`
  - `../cleaned_data/ucsd/final_table/courses_professors/`
  - `../cleaned_data/ucsd/final_table/enrollment_snapshots/`
  - `../cleaned_data/ucsd/final_table/passtimes/`
    > 注意，`../data/ucsd` 内的文件应当是从 S3 下载的最新文件。如果不是最新文件，请首先更新其数据。

#### 过程

- 启用 conda 环境 `conda activate mysql_import`
- 如果不是我的本机，可以根据`enviroment.yml`重建环境
- 运行`python mysql_data_update.py`
  - 注意，脚本会从 `.env` 文件中读取 RDS 数据库的参数
  - 注意，此脚本目前会对数据库的表直接替换，而非更新。
  - 对于 `courses_professors` 和 `enrollment_snapshots` 两张表，耗时会很长，约各 2 分钟。
  - 此脚本需要在未来进行更新，使用更为简便，节约，安全的方式
