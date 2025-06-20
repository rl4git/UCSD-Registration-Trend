import os
import uuid
import glob
import time # 引入 time 模块用于计时
import pandas as pd
from sqlalchemy import create_engine, text
from sqlalchemy.engine import URL
from dotenv import load_dotenv

# 加载环境变量
load_dotenv()

def update_table_with_replace(engine, file_path, file_type, table_name):
    """
    用本地文件的数据替换数据库中的表，增加了详细的日志和事务回滚功能。
    
    Args:
        engine: 一个已创建的 SQLAlchemy 引擎实例。
        file_path (str): 文件的路径模式 (例如 'data/*.csv').
        file_type (str): 文件类型 ('csv' 或 'json').
        table_name (str): 目标数据库表名。
    """
    print("-" * 80)
    print(f"▶️ [任务开始] 准备处理表: '{table_name}'")
    start_time = time.time()

    # --- 1. 读取并合并所有本地数据文件 ---
    local_df = None
    try:
        print(f"  - 步骤 1/3: 正在从路径 '{file_path}' 读取文件...")
        files = glob.glob(file_path)
        if not files:
            print(f"  🟡 [警告] 未找到匹配的文件。跳过此任务。")
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
        print(f"    - 成功从 {len(files)} 个文件中读取了 {len(local_df)} 行数据。")
        print("    - 数据预览 (前5行):")
        print(local_df.head(5).to_string())

    except Exception as e:
        print(f"  🔴 [错误] 读取文件时失败: {e}")
        return

    # --- 2. 连接数据库并执行替换操作 (带事务) ---
    print(f"  - 步骤 2/3: 准备连接数据库并替换表 '{table_name}'...")
    
    # 使用 with...as 语句确保连接被正确关闭
    with engine.connect() as connection:
        # 使用 begin() 来创建一个事务块
        # 如果块内代码成功执行，事务会自动提交 (commit)
        # 如果块内发生任何异常 (包括 Ctrl+C)，事务会自动回滚 (rollback)
        try:
            with connection.begin() as transaction:
                print("    - 事务已开始。正在执行 to_sql(if_exists='replace')...")
                
                # 执行核心操作
                local_df.to_sql(
                    name=table_name,
                    con=connection,
                    if_exists='replace', # 仍然使用 replace 逻辑
                    index=False
                )
                
                print(f"    - to_sql 操作完成。正在提交事务...")
            # 当 with connection.begin() 块成功退出时，事务已提交
            print(f"    - 事务已成功提交。")
            
        except Exception as e:
            # 如果 with 块内部发生错误，会自动回滚，然后代码会跳到这里
            print(f"  🔴 [错误] 数据库操作失败! 事务已自动回滚。")
            print(f"     错误详情: {e}")
            return # 出错后直接返回，不执行后续步骤

    # --- 3. 任务完成 ---
    end_time = time.time()
    duration = end_time - start_time
    print(f"  - 步骤 3/3: 任务完成。")
    print(f"✅ [任务成功] 表 '{table_name}' 已被成功替换。总耗时: {duration:.2f} 秒。")


def main():
    """
    主执行函数
    """
    print("================================================================================")
    print("                        数据库批量替换脚本启动")
    print("================================================================================")
    
    # --- 在主流程中只创建一次引擎 ---
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
        
        # 测试连接
        with engine.connect() as connection:
            print("数据库引擎创建成功，连接测试通过！")

    except Exception as e:
        print(f"🔴 [致命错误] 创建数据库引擎失败: {e}")
        return # 引擎创建失败，无法继续

    # --- 定义文件路径和要处理的表 ---
    try:
        # __file__ 在某些环境（如Jupyter）下可能不可用
        current_dir = os.path.dirname(os.path.abspath(__file__)) 
    except NameError:
        current_dir = os.getcwd() # 提供一个备用方案
        
    path_final_table = os.path.join(current_dir, "../cleaned_data/ucsd/final_table")
    
    # 定义任务列表
    tasks = [
        {"table": "courses", "path": f"{path_final_table}/courses/*.csv", "type": "csv"},
        {"table": "professors", "path": f"{path_final_table}/professors/*.csv", "type": "csv"},
        {"table": "courses_professors", "path": f"{path_final_table}/courses_professors/*.csv", "type": "csv"},
        {"table": "enrollment_snapshots", "path": f"{path_final_table}/enrollment_snapshots/*.csv", "type": "csv"},
        {"table": "passtimes", "path": f"{path_final_table}/passtimes/*.csv", "type": "csv"},
    ]
    
    # --- 依次执行任务 ---
    for task in tasks:
        update_table_with_replace(engine, task["path"], task["type"], task["table"])
    
    # --- 在所有操作完成后关闭引擎连接池 ---
    if engine:
        engine.dispose()
    
    print("-" * 80)
    print("所有任务执行完毕。脚本退出。")


if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("\n\n捕获到程序中断信号 (Ctrl+C)。正在优雅地退出...")