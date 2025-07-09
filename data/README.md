### Introduction

This folder contains scripts for cleaning data and uploading it to RDS.

You should run these scripts once after each registration season, following the order below, to update the database.

**Folder Contents:**

- `./data_cleaning/`: Contains scripts for cleaning raw data from S3 and saving it back to S3.
- `./cleaned_data/`: Stores data downloaded from S3 (this is the cleaned data, ready for direct upload to RDS).
- `./mysql_data_update/`: Contains scripts for uploading the data from `./cleaned_data/` to RDS.

---

### 1\. Clean Data

#### Clean Raw Data

1.  First, download the raw data for the required quarter from the original database to S3.

    - Original database link: `https://github.com/UCSD-Historical-Enrollment-Data/UCSDHistEnrollData.git`
    - S3 path: `ucsd/raw/{year}{Quarter}/*.csv` (The first letter of "Quarter" should be capitalized, e.g., `ucsd/raw/2024Winter/....csv`)

2.  Open the first Python notebook: `./data_cleaning/Data Cleaning ...`

    - In the initial cells, set the `access key`, `secret key`, `region`, and `bucket name` needed for AWS S3 connection.
    - Scroll down and check the basic passtime settings. Modify the year, quarter, and passtime to match your raw data.

3.  Run the entire `Data Cleaning` notebook. The script will read the necessary raw data from S3 based on the passtimes, clean it, and then save it back to S3.

4.  The data should be saved to:

    - Cleaned data for each quarter: `ucsd/cleaned/{}year{quarter}/*.csv`
    - Summary of cleaned data for each quarter: `ucsd/final/fianl/*.csv`
    - Passtimes (JSON format): `ucsd/final/passtimes.json`
    - Passtimes (CSV format): `ucsd/final_table/passtimes/*.csv`

    > For more information about the data, please read the notebook.

#### Make Tables

1.  Open the second notebook: `./data_cleaning/Table Creation ...`

    - In the initial cells, set the `access key`, `secret key`, `region`, and `bucket name` needed for AWS S3 connection.

2.  Run the notebook. The script will read the necessary data from S3 based on passtimes, clean it, and then save it back to S3.

3.  The data should be saved to:

    - Data for each table: `ucsd/final_table/{corresponding_table_name}/*.csv`

---

### 2\. Download Data to EC2

- Save the table data to `./cleaned_data/ucsd/final_table/{corresponding_table_name}/*.csv`.
- You can manually download and upload the data or use the **AWS CLI**.

#### AWS CLI Configuration

- **Install AWS CLI**

  ```bash
  # Add 'sudo' if you encounter permission issues.
  # Update package manager
  sudo apt update

  # Install awscli, which provides a series of commands to interact with AWS.
  # [Install or update the latest version of the AWS CLI - AWS Command Line Interface](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)
  # According to the AWS official documentation, run the following commands in order:
  curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
  sudo unzip awscliv2.zip
  sudo ./aws/install
  ```

- Go to **AWS IAM** and create a new role for AWS CLI.

  - It needs `aws s3 read only` permission.
  - It needs `aws rds full access` permission.

- Create an **access key** to get your access key and secret key.

- Go back to EC2 and enter the command `aws configure`. Then, input the following in order:

  - AWS access key
  - AWS secret key
  - Region
  - Return data format (optional, e.g., `json`)

- **Delete old data from the local path**, as AWS CLI does not overwrite files with different names by default.

- **Download data from S3**

  ```bash
  # You can directly execute the script
  ./cleaned_data/download_s3_data.sh

  # Or manually execute the commands
  # Note: AWS CLI does not automatically delete old files; you need to manually delete them first.
  # Download a single file
  aws s3 cp s3://your_bucket_name/path/file_name local_path/file_name
  # Download a folder
  aws s3 cp s3://your_bucket_name/path/ local_path/ --recursive
  ```

---

### 3\. Run Script to Upload Data to RDS

> For the RDS table structure, please refer to [RDS table structure](https://www.google.com/search?q=../docs/table_structure.md).

This script reads files from `../data/ucsd` and **replaces** the corresponding MySQL tables. This includes:

- `../cleaned_data/ucsd/final_table/courses/`
- `../cleaned_data/ucsd/final_table/professors/`
- `../cleaned_data/ucsd/final_table/courses_professors/`
- `../cleaned_data/ucsd/final_table/enrollment_snapshots/`
- `../cleaned_data/ucsd/final_table/passtimes.csv`

> **Important**: The files in `../data/ucsd` should be the latest ones downloaded from S3. If they're not, please update the data first.

#### Process

- Activate the conda environment: `conda activate mysql_import`
- If you're not on my local machine, you can recreate the environment using `environment.yml`.
- Run `python mysql_update.py`
  - Note that this script will first upload local data to **temporary tables**, and then run `INSERT ... ON DUPLICATE` to update the existing tables.
  - Be aware that this script relies on database connection settings in your **environment variables**. Ensure that your environment variables or current `.env` file include:
    - `DB_HOST=`
    - `DB_NAME=`
    - `DB_USER=`
    - `DB_PASS=`
