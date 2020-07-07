package sample.employeePage;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import sample.Main;
import sample.PayslipServer;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class SalaryRecEmployeePageController implements Initializable {
    @FXML
    private Label lbl_familyHelp;
    @FXML
    private Label lbl_childrenHelp;
    @FXML
    private Label lbl_houseHelp;
    @FXML
    private Label lbl_transfer;
    @FXML
    private Label lbl_totalPayments;
    @FXML
    private Label lbl_mission;
    @FXML
    private Label lbl_jobSpecial;
    @FXML
    private Label lbl_grocery;
    @FXML
    private Label lbl_lunch;
    @FXML
    private Label lbl_jobDifficulty;

    PayslipServer ps;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try
        {
            ps = new PayslipServer();

            ps.createConnection();
            ResultSet payslip = ps.getPersonPayslip( Main.getAuthenticatedUser() );
            payslip.last();

            String houseHelp = payslip.getString(1);
            String transferHelp = payslip.getString(2);
            String groceryHelp = payslip.getString(3);
            String missionHelp = payslip.getString(4);
            String lunchHelp = payslip.getString(5);
            String totalPayment = payslip.getString(6);
            String childrenHelp = payslip.getString(7);

            ResultSet action = ps.getPersonAction( Main.getAuthenticatedUser() );
            action.last();

            String jobSpecial = action.getString(2);
            String familyHelp = action.getString(3);
            String jobDifficultyAllowance = action.getString(4);

            lbl_houseHelp.setText(houseHelp);
            lbl_transfer.setText(transferHelp);
            lbl_grocery.setText(groceryHelp);
            lbl_mission.setText(missionHelp);
            lbl_lunch.setText(lunchHelp);
            lbl_childrenHelp.setText(childrenHelp);
            lbl_jobSpecial.setText(jobSpecial);
            lbl_familyHelp.setText(familyHelp);
            lbl_jobDifficulty.setText(jobDifficultyAllowance);
            lbl_totalPayments.setText(totalPayment);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
