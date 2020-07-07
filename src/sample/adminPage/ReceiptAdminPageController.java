package sample.adminPage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import sample.PayslipServer;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ReceiptAdminPageController implements Initializable {
    @FXML
    private TextField txtMinimumSalary;
    @FXML
    private Button btnChangeConstants;
    @FXML
    private Label lblMission;
    @FXML
    private Label lblLunch;
    @FXML
    private TextField txtReceiptDate;
    @FXML
    private Button btnPayslipActions;
    @FXML
    private Label lblTransfer;
    @FXML
    private Label lblGrocery;
    @FXML
    private Label lblHouse;
    @FXML
    void changeConstants(ActionEvent event) {

    }

    @FXML
    void payslipAction(ActionEvent event) throws SQLException {
        String newDate = txtReceiptDate.getText();
        PayslipServer ps = new PayslipServer();
        ps.createConnection();
        ps.createPayslipAll(newDate);
        ps.closeConnection();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        String dialog = "فیش حقوقی برای همه کار مندان صادر شد";
        alert.setContentText(dialog);
        alert.show();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try
        {
            Connection con = DriverManager.getConnection
                    ("jdbc:mysql://171.22.26.135/svopir_temp", "svopir_application", "QdSh14Mt");

            Statement stm =  con.createStatement();
            ResultSet rs = stm.executeQuery("select minimumSalary from systemConstants;");
            rs.next();

            txtMinimumSalary.setText(String.valueOf(rs.getInt(1)));

            rs.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
