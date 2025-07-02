-----

## Introduction

This folder contains scripts for **cleaning data** and **uploading it to AWS RDS**.

You should run these scripts once after each registration period to **update the database**.

### Folder Contents:

  - `./data_cleaning/`: Contains scripts for cleaning raw data from S3 and saving the cleaned data back to S3.
  - `./cleaned_data/`: Stores data downloaded from S3 (cleaned and ready for direct upload to RDS).
  - `./mysql_data_update/`: Contains scripts for uploading the data from `./cleaned_data/` to RDS.

-----

## 1\. Clean Data

### Clean Raw Data

1.  **Download raw data from the original database to S3 for the required quarter.**

      * Original database link: `https://github.com/UCSD-Historical-Enrollment-Data/UCSDHistEnrollData.git`
      * S3 path: `ucsd/raw/{year}{Quarter}/*.csv` (e.g., `ucsd/raw/2024Winter/....csv`, with the first letter of the Quarter capitalized).

2.  **Open the first Python notebook: `./data_cleaning/Data Cleaning ...`**

      * In the initial cells, set your AWS S3 connection details: `access key`, `secret key`, `region`, and `bucket name`.
      * Scroll down and **verify the `passtimes` configuration**. Modify the year, quarter, and passtime to match your raw data.

3.  **Run the entire `Data Cleaning` notebook.** The script will read the necessary raw data from S3 based on your `passtimes` settings, clean it, and save it back to S3.

4.  **Cleaned data will be saved to:**

      * Cleaned data for each quarter: `ucsd/cleaned/{}year{quarter}/*.csv`
      * Aggregated cleaned data for all quarters: `ucsd/final/final/*.csv`
      * Passtimes (JSON format): `ucsd/final/passtimes.json`
      * Passtimes (CSV format): `ucsd/final_table/passtimes/*.csv`

    > For more details on the data, please refer to the notebook.

### Make Tables

1.  **Open the second notebook: `./data_cleaning/Table Creation ...`**

      * In the initial cells, set your AWS S3 connection details: `access key`, `secret key`, `region`, and `bucket name`.

2.  **Run the notebook.** The script will read the necessary data from S3 based on `passtimes`, process it into the required table structures, and save it back to S3.

3.  **Table data will be saved to:**

      * Data for each table: `ucsd/final_table/{table_name}/*.csv`

-----

## 2\. Download Data to EC2

  - Save the table data to `./cleaned_data/ucsd/final_table/{corresponding_table_name}/*.csv`.
  - You can download and upload manually or use the AWS CLI.

### AWS CLI Configuration

  - **Install AWS CLI**

    ```bash
    # If you encounter permission issues, prepend 'sudo' to the command.
    # Update package manager
    sudo apt update

    # Install awscli, which provides commands for interacting with AWS.
    # [Install or update the latest version of the AWS CLI - AWS Command Line Interface](https://docs.aws.com/cli/latest/userguide/getting-started-install.html)
    # As described on the AWS official website, run the following commands sequentially:
    curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
    sudo unzip awscliv2.zip
    sudo ./aws/install
    ```

  - **Go to AWS IAM and create a new role for AWS CLI.**

      * Requires `aws s3 read only` permissions.
      * Requires `aws rds full access` permissions.

  - **Create an access key to obtain your access key and secret key.**

  - **Back on your EC2 instance, run `aws configure` and enter the following:**

      * AWS Access Key ID
      * AWS Secret Access Key
      * Default region name
      * Default output format (optional, e.g., `json`)

  - **Delete old data from the local path, as AWS CLI does not overwrite files with different names by default.**

  - **Download data from S3**

    ```bash
    # You can execute the script directly
    ./cleaned_data/download_s3_data.sh

    # Or execute commands manually
    # Note: AWS CLI does not proactively delete old files; you need to delete them manually first.
    # Download a single file
    aws s3 cp s3://your-bucket-name/path/to/file.csv /local/path/to/file.csv
    # Download a folder recursively
    aws s3 cp s3://your-bucket-name/path/to/folder/ /local/path/to/folder/ --recursive
    ```

-----

## 3\. Run Script to Upload Data to RDS

> For the RDS table structure, please refer to [RDS table structure](https://www.google.com/search?q=../docs/table_structure.md).

This process reads files within `../data/ucsd` and updates the MySQL tables, including:

  - `../cleaned_data/ucsd/final_table/courses/`
  - `../cleaned_data/ucsd/final_table/professors/`
  - `../cleaned_data/ucsd/final_table/courses_professors/`
  - `../cleaned_data/ucsd/final_table/enrollment_snapshots/`
  - `../cleaned_data/ucsd/final_table/passtimes.csv`
    > **Note**: The files in `../data/ucsd` should be the latest downloaded from S3. If they are not up-to-date, please update them first.

### Process

  - **Activate the conda environment:** `conda activate mysql_import`
  - If you are not on my local machine, you can recreate the environment using `environment.yml`.
  - **Run `python mysql_update.py`**
      * **Note**: This script will first upload local data to temporary tables, then execute `INSERT ... ON DUPLICATE KEY UPDATE` to refresh the existing tables.
      * **Note**: This script relies on database connection settings in your environment variables. Ensure that your environment variables or current `.env` file include:
          * `DB_HOST=`
          * `DB_NAME=`
          * `DB_USER=`
          * `DB_PASS=`
