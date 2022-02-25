
import javafx.application.Application;
import javafx.stage.Stage;
import java.sql.*;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class ShowPublisherName extends Application {

    // datafields for performing database operations
    private Connection bookConnection;
    private Statement bookStatement;
    private ResultSet bookResultSet;

    // nodes for the pane
    private Label lblISBN;
    private TextField txtISBN;
    private Button btnShowPub;
    private Label lblResult;

    // main method
    public static void main(String[] args) {
        Application.launch();
    }

    // overriden start method
    @Override
    public void start(Stage primaryStage) throws SQLException {
        initializeDB();

        // using a gridpane to arrange hbox & lblResult in a row
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(10, 20, 10, 20));
        pane.setHgap(5);
        pane.setVgap(10);

        // initializing all the nodes
        lblISBN = new Label("ISBN");
        btnShowPub = new Button("Show Publisher");
        txtISBN = new TextField();
        txtISBN.setPrefColumnCount(6);
        lblResult = new Label();

        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(lblISBN, txtISBN, btnShowPub);
        hbox.setAlignment(Pos.CENTER);

        pane.add(hbox, 0, 0);
        pane.add(lblResult, 0, 1);
        GridPane.setHalignment(lblResult, HPos.CENTER);

        btnShowPub.setOnAction(e -> showPublisher());

        Scene scene = new Scene(pane, 420, 100);
        primaryStage.setTitle("Show Publisher");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * initializeDB() method to load the driver, establish a connection, and
     * create a statement to execute query
     *
     * @throws SQLException
     */
    public void initializeDB() throws SQLException {

        try {
            // load the OJDBC driver
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Oracle driver loaded.");

            // establish a connection
            bookConnection
                    = DriverManager.getConnection("jdbc:oracle:thin:@calvin.humber.ca:1521:grok",
                            UserPassOracle.USERNAME,
                            UserPassOracle.PASSWORD);
            System.out.println("Oracle Database connected.");

            // Create a statement
            bookStatement = bookConnection.createStatement();

        } catch (Exception e) {
            System.err.println(e);
            bookStatement.close();
            bookConnection.close();
        }

    }

    /**
     * showPublisher() method to execute query based on the user's input of ISBN
     * and return the publisher name by joining books table and publisher table.
     */
    public void showPublisher() {
        String isbn = txtISBN.getText();

        try {
            String sqlQuery = "SELECT name FROM";
            sqlQuery += "\nbooks JOIN publisher USING(pubid)";
            sqlQuery += "\nWHERE ISBN = '" + isbn + "'";

            bookResultSet = bookStatement.executeQuery(sqlQuery);

            if (bookResultSet.next()) {
                String pubName = bookResultSet.getString("name");

                // display the pubName in the lblResult label
                lblResult.setText("Publisher Name: " + pubName);
            } else {
                lblResult.setText("Book Not Found");
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }
}
