#### Data cleaning script

这个脚本是用来清理原始数据这个脚本是用来清理原始数据，生成可直接上传的表数据的。

#### Data Cleaning

1. 先从原始数据库下载需要的季度的 raw data 到 S3

   - 原始数据库链接：`https://github.com/UCSD-Historical-Enrollment-Data/UCSDHistEnrollData.git`
   - S3 路径：`ucsd/raw/{year}{Quarter}/*.csv` Quarter 的首字母大写，例如 `ucsd/raw/2024Winter/....csv`
   - 可以手动下载上传，或者用 aws cli 下载。
   - aws cli 使用：

2. 打开第一个 python notebook: `Data Cleaning ...`

   - 首先设置 AWS S3 链接需要的 `access key`, `secret key`, `region`, `bucket name`
   - 向下，检查 passtimes 的基础设置，将年份，季度，passtime 修改为你的原始数据所对应的。

3. 运行整个 `Data Cleaning` notebook，数据应当会被保存到：

- 每个季度的清理数据：`ucsd/cleaned/{}year{quarter}/*.csv`
- 每个季度清理后数据的汇总：`ucsd/final/fianl/*.csv`
- passtimes (json 格式): `ucsd/final/passtimes.json`
- passtimes (csv 格式): `ucsd/final_table/passtimes/*.csv`
  > 表的信息，请阅读 notebook

####
