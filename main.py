import psycopg2

# Connect to the PostgreSQL database
def connect():
    try:
        conn = psycopg2.connect(
            dbname="Fitness-Club-System-Database",
            user="postgres",
            password="admin",
            host="localhost",
            port="5432"
        )
        return conn
    except psycopg2.Error as e:
        print("Unable to connect to the database:", e)
        return None

def main():
    conn = connect()
    if not conn:
        return

    # while True:
    #     Show menu and whatnot

    conn.close()

if __name__ == "__main__":
    main()
