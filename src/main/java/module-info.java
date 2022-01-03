module dbapp.dbapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;


    opens dbapp.dbapp to javafx.fxml;
    exports dbapp.dbapp;
}