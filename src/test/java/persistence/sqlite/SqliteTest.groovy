package persistence.sqlite

import org.junit.jupiter.api.Test

import java.sql.DriverManager

class SqliteTest {

    static String create_table_stmt(String table_name, List<String> col_defs) {
        StringWriter sw = new StringWriter()
        sw.append("CREATE TABLE IF NOT EXISTS $table_name (")
        def sep = ""
        col_defs.each {
            sw.append(sep + it)
            sep = ", "
        }
        return sw.toString() + ")"
    }

    @Test
    void test_create_table_stmt() {
        assert create_table_stmt("T1", ["id integer PRIMARY KEY", "x text"]) ==
                "CREATE TABLE IF NOT EXISTS T1 (id integer PRIMARY KEY, x text)"
    }

    @Test
    void test_create_db_and_table() {
        String db_file = System.getProperty("java.io.tmpdir")+File.separator+"test_create_db_and_table.db"
        new File(db_file).delete()
        String url = "jdbc:sqlite:$db_file"
        DriverManager.getConnection(url).with {
            createStatement().with {
                def create_table_sql = create_table_stmt(
                        "warehouses",
                        ["id integer PRIMARY KEY", "name text NOT NULL", "capacity real"])
                execute(create_table_sql)
                def data = ["Raw Materials": 3000, "Semifinished Goods": 4000, "Finished Goods": 5000]
                def insert = "INSERT INTO warehouses(name,capacity) VALUES(?,?)"
                def pstmt = prepareStatement(insert)
                data.each { name, capacity ->
                    pstmt.setString(1, name)
                    pstmt.setDouble(2, capacity)
                    pstmt.executeUpdate()
                }
                executeQuery("SELECT count(*) FROM warehouses").with {
                    assert next()
                    assert getInt(1) == 3
                }
                executeQuery("SELECT count(*) FROM warehouses WHERE capacity > 3000").with {
                    assert next()
                    assert getInt(1) == 2
                }
                close()
            }
        }
    }
}