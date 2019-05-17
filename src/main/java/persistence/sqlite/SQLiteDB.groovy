package persistence.sqlite

import datamodel.relational.Column
import datamodel.relational.RelationalModel
import datamodel.relational.Table

import java.sql.Connection
import java.sql.DriverManager

class SQLiteDB {

    final String db_file_name
    Connection cnx

    SQLiteDB(String db_file_name) {
        this.db_file_name = db_file_name
        cnx = DriverManager.getConnection("jdbc:sqlite:$db_file_name")
    }

    void create_schema(RelationalModel rm) {
        rm.tables.values().each { Table t ->
            create_table(t)
        }
    }

    void create_table(Table t) {
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS $t.table_name (")
        def sep = ""
        t.cols.each { Column c ->
            sb << sep + column_def(c)
            sep = ", "
        }
        sb << ")"
        cnx.createStatement().execute(sb.toString())
    }

    void load_sample(Table t) {

    }

    static String column_def(Column c) {
        def nullable = c.is_nullable ? "" : " NOT NULL"
        switch (c.type) {
            case "string": return "$c.name TEXT $nullable"
            case "integer": return "$c.name INTEGER $nullable"
            case "number": return "$c.name REAL $nullable"
            case "boolean": return "$c.name INTEGER $nullable"
        }
        throw new Exception("invalid column data type: $c.type")
    }

}
