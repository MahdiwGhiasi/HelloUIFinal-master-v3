package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class PayslipServer {
    private final static String databaseName = "171.22.26.135/svopir_temp";
    private final static String databaseConfig = "?useUnicode=yes&characterEncoding=UTF-8";
    private final static String databaseUsername = "svopir_application";
    private final static String databasePassword = "QdSh14Mt";

    private Connection con;

    public void createConnection() throws SQLException /* Creates new Database Connection */
    {
        con = DriverManager.getConnection("jdbc:mysql://"+databaseName + databaseConfig, databaseUsername, databasePassword);
        System.out.println("Connection to database Successfully created!");
    }
    public Connection getConnection() throws SQLException
    {
        Connection connection = con;
        return connection;
    }
    public void closeConnection() throws SQLException /* Closes current database Connection */
    {
        con.close();
        System.out.println("Connection closed!");
    }

    public void createPayslip(String personId, String currentTime) throws SQLException
    {
        updateTime(currentTime);

        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery("select minimumSalary from systemConstants;");
        rs.next();
        int minimumSalary = (int)rs.getFloat(1);

        int houseHelp = (int) getHouseHelp(personId, minimumSalary);
        int transferHelp = (int) getTransferHelp(personId, minimumSalary);
        int lunchHelp = (int) (0.3 * minimumSalary);
        int groceryHelp = (int) (0.55 * minimumSalary);
        int missionHelp = (int) (0.13 * minimumSalary);

        ResultSet action = getPersonAction(personId);
        action.last();

        int jobSpecial = Integer.parseInt(action.getString(1));
        int familyHelp = Integer.parseInt(action.getString(2));
        int jobDifficulty = Integer.parseInt(action.getString(3));
        int childrenHelp = getChildrenHelp(personId, minimumSalary);
        int actionId = action.getInt(5);

        int salary = houseHelp + transferHelp + lunchHelp + groceryHelp + missionHelp + jobSpecial
                + familyHelp + jobDifficulty + childrenHelp;

        PreparedStatement prs = con.prepareStatement("insert into payslipAction " +
                "(payslipId, owner, houseHelp, transferHelp, groceryHelp, missionHelp, salary, actionId, lunchHelp, childrenHelp) " +
                "values(?,?,?,?,?,?,?,?,?,?); ");
        prs.setString(1, null);
        prs.setString(2, personId);
        prs.setString(3, String.valueOf(houseHelp));
        prs.setString(4, String.valueOf(transferHelp));
        prs.setString(5, String.valueOf(groceryHelp));
        prs.setString(6, String.valueOf(missionHelp));
        prs.setString(7, String.valueOf(salary));
        prs.setInt(8, actionId);
        prs.setString(9, String.valueOf(lunchHelp));
        prs.setString(10, String.valueOf(childrenHelp));

        prs.executeUpdate();
    }
    public double getTransferHelp(String personId, int minimumSalary) throws SQLException
    {
        Statement stm = con.createStatement();
        String query = String.format("select city from teachers where personId='%s';", personId);
        ResultSet rs = stm.executeQuery(query);
        rs.next();

        if(isInDevelopedCity(Integer.parseInt(rs.getString(1))))
            return 0.4 * minimumSalary;
        else
            return 0.2 * minimumSalary;
    }
    public double getHouseHelp(String personId, int minimumSalary) throws SQLException
    {
        Statement stm = con.createStatement();
        String query = String.format("select city from teachers where personId='%s';", personId);
        ResultSet rs = stm.executeQuery(query);
        rs.next();

        if(isInDevelopedCity(Integer.parseInt(rs.getString(1))))
            return 0.26 * minimumSalary;
        else
            return 0.1 * minimumSalary;
    }

    public static boolean isInDevelopedCity(int cityCode)
    {
        if (cityCode == 119 || cityCode == 559 || cityCode == 22 ||
                cityCode == 89 || cityCode == 180 || cityCode == 334 ||cityCode == 258)
            return true;

        else return false;
    }
    public void updateTime(String newTime) throws SQLException
    {
        Statement st = con.createStatement();
        String query = String.format("update time set currentTime='%s' WHERE time.variable='currentTime';",newTime);
        st.executeUpdate(query);
    }
    public ObservableList<String> getEmployees() throws SQLException
    {
        ObservableList<String> personIds = FXCollections.observableArrayList();
        Statement st = con.createStatement();
        String query = "select personId from teachers;";
        ResultSet resultset = st.executeQuery(query);
        while(resultset.next())
        {
            personIds.add(resultset.getString(1));
        }

        return personIds;
    }
    public void createPayslipAll(String date) throws SQLException
    {
        ObservableList<String> employees = getEmployees();

        for(String employee : employees)
        {
            createPayslip(employee, date);
        }
    }
    public ResultSet getPersonAction(String personId) throws SQLException
    {
        Statement stm = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        String query = String.format("select jobAllowance,jobSpecial,familyHelp,jobDifficultnessAllowance,actionId from employmentAction where owner='%s'",personId);
        ResultSet action = stm.executeQuery(query);

        return action;
    }
    public ResultSet getPersonPayslip(String personId) throws SQLException
    {
        Statement stm = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        String query = String.format("select houseHelp, transferHelp, groceryHelp, missionHelp, lunchHelp, salary, childrenHelp from payslipAction where owner='%s'",personId);
        ResultSet payslip = stm.executeQuery(query);

        return payslip;
    }
    public int getChildrenHelp(String personId, int minimumSalary) throws SQLException
    {
        Statement stm = con.createStatement();
        String query = String.format("select numberOfChildren from teachers where personId='%s';", personId);
        ResultSet rs = stm.executeQuery(query);
        rs.next();
        int numberOfChildren = rs.getInt(1);

        return (int) (numberOfChildren * (0.1 * minimumSalary));
    }
}
