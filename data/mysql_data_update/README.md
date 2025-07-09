### Script Explanation

This script reads files from `../data/ucsd` and **replaces** the corresponding MySQL tables. This includes:

- `../cleaned_data/ucsd/final_table/courses/`
- `../cleaned_data/ucsd/final_table/professors/`
- `../cleaned_data/ucsd/final_table/courses_professors/`
- `../cleaned_data/ucsd/final_table/enrollment_snapshots/`
- `../cleaned_data/ucsd/final_table/passtimes.csv`

> **Important**: The files in `../data/ucsd` should be the latest ones downloaded from S3. If they're not, please update the data first.

---

### Process

- Activate the conda environment: `conda activate mysql_import`
- If you're not on my local machine, you can recreate the environment using `environment.yml`.
- Run `python mysql_update.py`
  - Note that this script will first upload local data to **temporary tables**, and then run `INSERT ... ON DUPLICATE` to update the existing tables.
  - Be aware that this script relies on database connection settings in your **environment variables**. Ensure that your environment variables or current `.env` file include:
    - `DB_HOST=`
    - `DB_NAME=`
    - `DB_USER=`
    - `DB_PASS=`
