import os
import uuid, glob, time
import pandas as pd
from sqlalchemy import create_engine, text
from sqlalchemy.engine import URL
from dotenv import load_dotenv

# load enviroment
load_dotenv()

def upsert_table(engine, df, table_name, primary_keys):
    """
    使用 "INSERT ... ON DUPLICATE KEY UPDATE" 逻辑更新或插入数据到 MySQL 表。

    Args:
        engine: SQLAlchemy 引擎实例。
        df (pd.DataFrame): 包含最新数据的 DataFrame。
        table_name (str): 目标数据库表名。
        primary_keys (list[str]): 用于判断重复的列名列表（通常是主键）。
    """
    if df.empty:
        print(f"  🟡 [警告] DataFrame 为空，跳过对表 '{table_name}' 的操作.")
        return

    temp_table_name = f"temp_{table_name}_{uuid.uuid4().hex[:8]}"
    print(f"- 1: 将 {len(df)} 行数据加载到临时表 '{temp_table_name}'...")

    with engine.connect() as connection:
        try:
            # 开启事务性保证原子操作
            with connection.begin() as transaction:
                # 本地数据写入临时表
                df.to_sql(
                    name=temp_table_name,
                    con=connection,
                    if_exists='replace',
                    index=False
                )

                print(f"- 2: 临时表构建成功")

                # 构造 upsert sql
                all_cols = [f"`{col}`" for col in df.columns]
                all_cols_str = ", ".join(all_cols)

                # 构建UPDATE语句部分，我们需要更新主键之外的所有键
                update_cols = [col for col in df.columns if col not in primary_keys]
                if not update_cols:
                    print("  🟡 [警告] 所有列都是主键，将只执行插入新数据操作。")
                    update_clause = ""
                else:
                    update_clause = "ON DUPLICATE KEY UPDATE " + ", ".join(
                        [f"`{col}` = VALUES(`{col}`)" for col in update_cols]
                    )

                # 构建完整的 SQL 语句，key重复的更新，不重复的插入
                upsert_sql = f"""
                    INSERT INTO `{table_name}` ({all_cols_str})
                    SELECT {all_cols_str} FROM `{temp_table_name}`
                    {update_clause}
                """

                print(f"- 3: 准备执行 UPSERT SQL...")
                connection.execute(text(upsert_sql))
            
            # 事务结束
            print(f"- 4: UPSERT 操作成功，事务已提交。")

        except Exception as e:
            print(f"  🔴 [错误] 数据库操作失败! 事务已自动回滚。")
            print(f"      错误详情: {e}")
            # 事务在异常时会自动回滚
            raise # 重新抛出异常，让调用者知道发生了错误

        finally:
            # 无论成功与否，删除临时表
            try:
                with connection.begin() as transaction:
                    connection.execute(text(f"DROP TABLE IF EXISTS `{temp_table_name}`;"))
                    print(f"- 清理: 临时表 `{temp_table_name}` 已删除。")
            except Exception as e:
                print(f"  🔴 [错误] 清理临时表失败: {e}")


def process_files_and_upsert(engine, file_path, file_type, table_name, primary_keys):
    """
    包装函数，整合文件读取和upsert逻辑
    """
    print("-" * 80)
    print(f"▶️ [任务开始] 准备更新/插入表: '{table_name}'")
    start_time = time.time()

    local_df = None
    try:
        print(f"- 正在从路径 '{file_path}' 读取文件...")
        files = glob.glob(file_path)
        if not files:
            print(f"  🟡 [警告] 在路径 '{file_path}' 未找到匹配的文件。跳过此任务。")
            return
        df_list = []
        if file_type.lower() == 'csv':
            df_list = [pd.read_csv(f) for f in files]
        elif file_type.lower() == 'json':
            df_list = [pd.read_json(f, orient="records") for f in files]
        else:
            print(f"  🔴 [错误] 不支持的文件类型: {file_type}")
            return

        local_df = pd.concat(df_list, ignore_index=True)
        local_df.drop_duplicates(subset=primary_keys, keep='last', inplace=True)
        print(f"- 成功从 {len(files)} 个文件中读取并合并了 {len(local_df)} 行唯一数据。")

    except Exception as e:
        print(f"  🔴 [错误] 读取文件时失败: {e}")
        return

    # 开始执行 Upsert 操作
    try:
        upsert_table(engine, local_df, table_name, primary_keys)
        end_time = time.time()
        duration = end_time - start_time
        print(f"✅ [任务成功] 表 '{table_name}' 已成功更新/插入。总耗时: {duration:.2f} 秒。")
    except Exception as e:
        print(f"❌ [任务失败] 处理表 '{table_name}' 时发生严重错误。")


def main():
    print("================================================================================")
    print("                      数据库批量更新/插入 (UPSERT) 脚本启动")
    print("================================================================================")
    
    engine = None
    try:
        print("正在创建数据库引擎...")
        engine = create_engine(URL.create(
            "mysql+mysqlconnector",
            username=os.getenv('DB_USER'),
            password=os.getenv('DB_PASS'),
            host=os.getenv('DB_HOST'),
            database=os.getenv('DB_NAME')
        ))
        with engine.connect() as connection:
            print("数据库引擎创建成功，连接测试通过")
    except Exception as e:
        print(f"🔴 [致命错误] 创建数据库引擎失败: {e}")
        return

    try:
        current_dir = os.path.dirname(os.path.abspath(__file__))
    except NameError:
        current_dir = os.getcwd()

    path_final_table = os.path.join(current_dir, "../cleaned_data/ucsd/final_table")

    tasks = [
    
        {"table": "courses",
         "path": f"{path_final_table}/courses/*.csv",
         "type": "csv",
         "primary_keys": ["course_offering_id"]},
        
        {"table": "professors",
         "path": f"{path_final_table}/professors/*.csv",
         "type": "csv",
         "primary_keys": ["prof_id"]},
        
        {"table": "courses_professors",
         "path": f"{path_final_table}/courses_professors/*.csv",
         "type": "csv",
         "primary_keys": ["id"]},
        
        {"table": "enrollment_snapshots",
         "path": f"{path_final_table}/enrollment_snapshots/*.csv",
         "type": "csv",
         "primary_keys": ["course_offering_id", "date"]},

        {"table": "passtimes",
         "path": f"{path_final_table}/passtimes/*.csv",
         "type": "csv",
         "primary_keys": ["id"]}
    ]

    for task in tasks:
        process_files_and_upsert(
            engine,
            task["path"],
            task["type"],
            task["table"],
            task["primary_keys"]
        )

    if engine:
        engine.dispose()

    print("-" * 80)
    print("所有任务执行完毕。脚本退出。")


if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("\n\n捕获到程序中断信号 (Ctrl+C)。正在优雅地退出...")
