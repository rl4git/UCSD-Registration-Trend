### Introduction

This folder contains scripts for cleaning data and uploading it to RDS.

It should be run once after each registration season, in the following order, to update the database.

**Folder Contents:**

- `./data_cleaning/`: Contains scripts for cleaning raw data from S3 and saving it back to S3.
- `./cleaned_data/`: Stores data downloaded from S3 (cleaned data, ready for direct upload to RDS).
- `./mysql_data_update/`: Contains scripts for uploading data from `./cleaned_data/` to RDS.

---

### 1\. Clean Data

#### Clean raw data

1.  First, download the required raw data for the relevant quarter from the original database to S3.

    - Original database link: `https://github.com/UCSD-Historical-Enrollment-Data/UCSDHistEnrollData.git`
    - S3 path: `ucsd/raw/{year}{Quarter}/*.csv` (Quarter should be capitalized, e.g., `ucsd/raw/2024Winter/....csv`)

2.  Open the first Python notebook: `./data_cleaning/Data Cleaning ...`

    - In the initial cells, set the `access key`, `secret key`, `region`, and `bucket name` required for AWS S3 connection.
    - Scroll down and check the basic passtime settings. Modify the year, quarter, and passtime to correspond to your raw data.

3.  Run the entire `Data Cleaning` notebook. The script will read the necessary raw data from S3 based on passtimes, clean it, and save it back to S3.

4.  Data should be saved to:

    - Cleaned data for each quarter: `ucsd/cleaned/{}year{quarter}/*.csv`
    - Summary of cleaned data for each quarter: `ucsd/final/fianl/*.csv`
    - Passtimes (JSON format): `ucsd/final/passtimes.json`
    - Passtimes (CSV format): `ucsd/final_table/passtimes/*.csv`

    > For information about the data, please read the notebook.

#### Make tables

1.  Open the second notebook: `./data_cleaning/Table Creation ...`

    - In the initial cells, set the `access key`, `secret key`, `region`, and `bucket name` required for AWS S3 connection.

2.  Run the notebook. The script will read the necessary data from S3 based on passtimes, clean it, and save it back to S3.

3.  Data should be saved to:

    - Data for each table: `ucsd/final_table/{corresponding_table_name}/*.csv`

---

### 2\. Download Data to EC2

- Save the table data to `./cleaned_data/ucsd/final_table/{corresponding_table_name}/*.csv`
- You can download and upload manually or use the AWS CLI.

#### AWS CLI Configuration

- Install AWS CLI

  ```bash
  # Add 'sudo' if you encounter permission issues.
  # Update package manager
  sudo apt update

  # Install awscli, providing a series of commands to interact with AWS.
  # [Install or update the latest version of the AWS CLI - AWS Command Line Interface](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)
  # According to the AWS official documentation, run the following in order:
  curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
  sudo unzip awscliv2.zip
  sudo ./aws/install
  ```

- Go to AWS IAM and create a new role for AWS CLI.

  - Requires `aws s3 read only` permission.
  - Requires `aws rds full access` permission.

- Create an access key to obtain the access key and secret key.

- Return to EC2 and enter the command `aws configure`. Input the following in order:

  - AWS access key
  - AWS secret key
  - Region
  - Return data format (optional, e.g., json)

- **Delete old data from the local path, as AWS CLI does not overwrite files with different names by default.**

- Download data from S3:

  ```bash
  # Download a single file
  aws s3 cp s3://your_bucket_name/path/file_name local_path/file_name
  # Download a folder
  aws s3 cp s3://your_bucket_name/path/ local_path/ --recursive
  ```

---

### 3\. Run Script to Upload Data to RDS

- `mysql_data_update.py` will read files within `../data/ucsd` and replace tables in AWS RDS MySQL, including:
  - `../cleaned_data/ucsd/final_table/courses/`
  - `../cleaned_data/ucsd/final_table/professors/`
  - `../cleaned_data/ucsd/final_table/courses_professors/`
  - `../cleaned_data/ucsd/final_table/enrollment_snapshots/`
  - `../cleaned_data/ucsd/final_table/passtimes/`
    > Note: Files in `../data/ucsd` should be the latest downloaded from S3. If they are not, please update the data first.

#### Process

- Activate the conda environment `conda activate mysql_import`.
- If this is not my local machine, the environment can be recreated using `environment.yml`.
- Run `python mysql_data_update.py`.
  - Note that the script will read RDS database parameters from the `.env` file.
  - Note that this script currently **replaces** database tables directly, rather than updating them.
  - For the `courses_professors` and `enrollment_snapshots` tables, this will be time-consuming, approximately 2 minutes each.
  - This script needs to be updated in the future to use a simpler, more efficient, and secure method.
