import os
import csv, json, uuid, glob
import mysql.connector
import pandas as pd
from mysql.connector import Error
from dotenv import load_dotenv
from sqlalchemy import create_engine, text
from sqlalchemy.engine import URL

# load the enviroment data
load_dotenv()

db_config = {
    'host': os.getenv('DB_HOST'),
    'user': os.getenv('DB_USER'),
    'password': os.getenv('DB_PASS'),
    'database': os.getenv('DB_NAME')
}

def testConnection():

    print("Test connection.")

    try:
        conn = mysql.connector.connect(**db_config)
        cursor = conn.cursor()

        cursor.execute("SELECT * FROM test_tb")
        rows = cursor.fetchall()

        print("Result of test_tb: ")
        for row in rows:
            print(row)

        cursor.close()
        conn.close()

    except mysql.connector.Error as err:
        print(f"Error happened: {err}")


# testConnection()

# filePath: local csv or json file path
# filetype: local file type, string, such as "csv" or "json"
# tableName: the table name in mysql
# pk_columns: the primary columns, a list? such as ["year", "course_offering_id"]
def updateTableWithLocalFile(filePath, fileType, tableName, pk_columns):
    # 读取本地数据文件
    try: 
        if fileType.lower() == 'csv':
            csv_files = glob.glob(filePath)
            local_df = pd.read_csv(csv_files[0])
        elif fileType.lower() == 'json':
            json_files = glob.glob(filePath)
            local_df = pd.read_json(json_files[0], orient="records")
        else:
            print("File type does not exist.")
            return
    except Exception as e:
        print(f"Error while reading local data file: {e}")
    # print(local_df.head(10))
    

    # 创建数据库链接
    try:
        engine = create_engine(URL.create(
            "mysql+mysqlconnector",
            username = os.getenv('DB_USER'),
            password = os.getenv('DB_PASS'),
            host = os.getenv('DB_HOST'),
            database = os.getenv('DB_NAME')
        ))
    except Exception as e:
        print(f"Failed to create database connection: {e}")
        return
    
    with engine.connect() as connection:
        print("Engine connection test.")
        result = connection.execute(text("SELECT 1"))
        scalar_result = result.scalar()

        print(f"SELECT 1 : {scalar_result}")

    print("Engine connection test finish.")
        
    # 首先创建一个临时表，将本地数据放进去，为 INSERT INTO ... ON DUPLICATE... 做准备
    temp_table_name = f"temp_upsert_{uuid.uuid4().hex}"

    with engine.connect() as connection:
        try:
            print(f"Uploading file {filePath} to tanble {tableName}...")
            local_df.to_sql(
                tableName,
                con=connection,
                if_exists='replace',
                index=False
            )
            print(f"{tableName} already replaced by local file.")
        
        except Exception as e:
            print(f"Replace table failed: {e}")

        finally:
            if 'engine' in locals() and engine:
                engine.dispose()

current_dir = os.path.dirname(os.path.abspath(__file__))
path_final_table = os.path.join(current_dir, "../cleaned_data/ucsd/final_table")
path_final = os.path.join(current_dir, "../cleaned_data/ucsd/final")

updateTableWithLocalFile(f"{path_final_table}/courses/*.csv", "csv", "courses", "")


