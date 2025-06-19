import os
import mysql.connector
from dotenv import load_dotenv

# load the enviroment data
load_dotenv()

db_config = {
    'host': os.getenv('DB_HOST'),
    'user': os.getenv('DB_USER'),
    'password': os.getenv('DB_PASS'),
    'database': os.getenv('DB_NAME')
}

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

