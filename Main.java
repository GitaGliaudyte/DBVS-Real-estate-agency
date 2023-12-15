import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        AgencyUI ui = new AgencyUI();
        try{
            ui.start();
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
