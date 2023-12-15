import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class SQL {
    private static String Url = "jdbc:postgresql://pgsql3.mif/studentu";
    private Connection connection;
    private String Username = "username";
    private String Password = "password";

    private PreparedStatement selectDealStmt;
    private PreparedStatement deleteDealStmt;
    private PreparedStatement selectREStmt;
    private PreparedStatement insertDealStmt;
    private PreparedStatement updateREPriceStmt;
    private PreparedStatement selectOwnersInfoByREIDStmt;
    private PreparedStatement selectAgentStmt;
    private PreparedStatement deleteContractStmt;
    private PreparedStatement deleteAgentStmt;
    private PreparedStatement selectREandAgentsByAgentIDStmt;
    private PreparedStatement selectBuyerStmt;
    private PreparedStatement selectAgentCountStmt;


    public SQL() throws SQLException{
        loadDriver();
        getConnection();
        prepareStatements();
    }

    private void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Driveris nerastas");
            System.exit(1);
        }
    }

    private void getConnection() {
        try {
            connection = DriverManager.getConnection(Url, Username, Password) ;
        } catch (SQLException e) {
            System.out.println("Nepavyko prisijungti prie duomenu bazes");
            System.exit(1);
        }
    }

    private void prepareStatements() throws SQLException{
        selectDealStmt = connection.prepareStatement("SELECT * FROM username.Sandoris");
        selectREStmt = connection.prepareStatement("SELECT * FROM username.NT");
        selectAgentStmt = connection.prepareStatement("SELECT * FROM username.Brokeris");
        deleteAgentStmt = connection.prepareStatement("DELETE FROM username.BrokerioSutartis WHERE BrokerioID = ?");
        deleteContractStmt = connection.prepareStatement( "DELETE FROM username.Brokeris WHERE ID = ?");
        selectOwnersInfoByREIDStmt = connection.prepareStatement("SELECT A.Adresas, B.Vardas, B.Pavarde, B.TelNumeris " +
                "FROM username.NT AS A, username.Savininkas AS B " +
                "WHERE A.ID = ? AND A.SavininkoAK = B.AK " +
                "ORDER BY B.Pavarde");
        selectREandAgentsByAgentIDStmt = connection.prepareStatement("SELECT C.TurtoID, A.Kaina, B.Vardas, B.Pavarde " +
                "FROM username.NT AS A, username.Brokeris AS B, username.BrokerioSutartis AS C " +
                "WHERE B.ID = ? AND C.TurtoID = A.ID AND C.BrokerioID = ? " +
                "ORDER BY B.Pavarde");
        deleteDealStmt = connection.prepareStatement("DELETE FROM username.Sandoris WHERE ID = ?");
        insertDealStmt = connection.prepareStatement("INSERT INTO username.Sandoris VALUES(DEFAULT, ? , ? , ? , ? )");
        selectBuyerStmt = connection.prepareStatement("SELECT * FROM username.Pirkejas");
        updateREPriceStmt = connection.prepareStatement("UPDATE username.NT SET Kaina = ? WHERE ID = ?");
        selectAgentCountStmt = connection.prepareStatement("SELECT COUNT(DISTINCT ID) FROM username.Brokeris");
    }

    public List<List> getAllREsAssignedToAgent (int id) throws SQLException{
        List<List> res = new LinkedList<List>();
        selectREandAgentsByAgentIDStmt.setInt(1, id);
        selectREandAgentsByAgentIDStmt.setInt(2, id);
        ResultSet resultSet = selectREandAgentsByAgentIDStmt.executeQuery();
        while (resultSet.next())
        {
            List<String> row = new LinkedList<String>();
            for (int i = 1; i < resultSet.getMetaData().getColumnCount() + 1; i++)
            {
                row.add(resultSet.getString(i));
            }
            res.add(row);
        }
        return res;
    }

    public void updateREprice(int reID, double price) throws Exception{
        updateREPriceStmt.setDouble(1, price);
        updateREPriceStmt.setInt(2, reID);
        updateREPriceStmt.executeUpdate();
    }

    public void signNewDeal(int reID, String buyersNo, String date, double taxes) throws SQLException{
        insertDealStmt.setInt(1, reID);
        insertDealStmt.setString(2, buyersNo);
        if(date != "")
            insertDealStmt.setDate(3, Date.valueOf(java.time.LocalDate.parse(date)));
        else
            insertDealStmt.setDate(3,Date.valueOf(java.time.LocalDate.now()));
        insertDealStmt.setDouble(4, taxes);
        int i = insertDealStmt.executeUpdate();
        if(i <= 0)
            throw new SQLException("Nepavyko prideti sandorio");
    }

    public List<List> getOwnersInfoByREID(int id) throws SQLException{
        List<List> ownersInfo = new LinkedList<List>();
        selectOwnersInfoByREIDStmt.setInt(1,  id);
        ResultSet resultSet = selectOwnersInfoByREIDStmt.executeQuery();
        while (resultSet.next())
        {
            List<String> row = new LinkedList<String>();
            for (int i = 1; i < resultSet.getMetaData().getColumnCount() + 1; i++)
            {
                row.add(resultSet.getString(i));
            }
            ownersInfo.add(row);
        }
        return ownersInfo;
    }

    public void removeDeal(int id) throws SQLException{
        deleteDealStmt.setInt(1,id);
        deleteDealStmt.executeUpdate();
    }

//    public void removeContract(int id) throws SQLException{
//        deleteContractStmt.setInt(1,id);
//        deleteContractStmt.executeUpdate();
//    }

    public void dissmissAgentandRemoveContract(int id) throws SQLException {
        try{
            connection.setAutoCommit(false);
            deleteAgentStmt.setInt(1,id);
            deleteAgentStmt.executeUpdate();
            deleteContractStmt.setInt(1,id);
            deleteContractStmt.executeUpdate();

            List<List> agents = new LinkedList<List>();
            ResultSet result = selectAgentCountStmt.executeQuery();
            while (result.next())
            {
                List<String> row = new LinkedList<String>();
                for (int i = 1; i < result.getMetaData().getColumnCount() + 1; i++)
                {
                    row.add(result.getString(i));
                }
                agents.add(row);
            }
            if(Integer.valueOf((String) agents.get(0).get(0)) == 0)
                throw new SQLException("Negalima istrinti paskutinio brokerio");
            connection.commit();
        } catch (SQLException e){
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public List<List> getAllDeals() throws SQLException{
        List<List> deals = new LinkedList<List>();
        ResultSet resultSet = selectDealStmt.executeQuery();
        while (resultSet.next())
        {
            List<String> row = new LinkedList<String>();
            for (int i = 1; i < resultSet.getMetaData().getColumnCount() + 1; i++)
            {
                row.add(resultSet.getString(i));
            }
            deals.add(row);
        }
        return deals;
    }

    public List<List> getAllREs() throws SQLException{
        List<List> res = new LinkedList<List>();
        ResultSet resultSet = selectREStmt.executeQuery();
        while (resultSet.next())
        {
            List<String> row = new LinkedList<String>();
            for (int i = 1; i < resultSet.getMetaData().getColumnCount() + 1; i++)
            {
                row.add(resultSet.getString(i));
            }
            res.add(row);
        }
        return res;
    }

    public List<List> getAllAgents() throws SQLException{
        List<List> agents = new LinkedList<List>();
        ResultSet resultSet = selectAgentStmt.executeQuery();
        while (resultSet.next())
        {
            List<String> row = new LinkedList<String>();
            for (int i = 1; i < resultSet.getMetaData().getColumnCount() + 1; i++)
            {
                row.add(resultSet.getString(i));
            }
            agents.add(row);
        }
        return agents;
    }

    public List<List> getAllBuyers() throws SQLException{
        List<List> buyers = new LinkedList<List>();
        ResultSet resultSet = selectBuyerStmt.executeQuery();
        while (resultSet.next())
        {
            List<String> row = new LinkedList<String>();
            for (int i = 1; i < resultSet.getMetaData().getColumnCount() + 1; i++)
            {
                row.add(resultSet.getString(i));
            }
            buyers.add(row);
        }
        return buyers;
    }

    public void close() {
        try {
            selectDealStmt.close();
            deleteDealStmt.close();
            selectREStmt.close();
            insertDealStmt.close();
            updateREPriceStmt.close();
            selectOwnersInfoByREIDStmt.close();
            selectAgentStmt.close();
            deleteContractStmt.close();
            deleteAgentStmt.close();
            selectREandAgentsByAgentIDStmt.close();
            selectBuyerStmt.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println("Kilo klaida uzdarant duomenu baze");
        }
    }
}