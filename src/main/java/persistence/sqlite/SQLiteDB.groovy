package persistence.sqlite

import datamodel.Datamodel
import datamodel.Entity
import datamodel.Relation
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

    void load_samples(Datamodel dm) {
        // load entity samples starting by the less dependent first
        def rm = dm.relational()
        dm.getEntitiesByDependencyOrder().each { Entity e ->
            Table t = rm.tables[Table.normalize(e.name)]
            t.load(e.samples)
        }
        dm.getRelations() { Relation r ->
            Table t = rm.tables[Table.normalize(r.name)]
            t.load(r.samples)
        }
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
