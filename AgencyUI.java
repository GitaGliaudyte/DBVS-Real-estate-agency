import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AgencyUI {

    private Scanner scan;
    private SQL sql;

    public void start() throws SQLException{
        sql = new SQL();
        scan = new Scanner(System.in);

        menuOptions();

        while (true) {
            try {
                int input;
                while (true) {
                    try {
                        input = scan.nextInt();
                        scan.nextLine();
                        break;
                    } catch (Exception e) {
                        System.out.println("Pasirinkimas privalo buti skaicius, iveskite dar karta:");
                        scan.nextLine();
                    }
                }
                switch (input) {
                    case 0:
                        findREassignedToAgent(sql);
                        break;
                    case 1:
                        dissmissAgent(sql);
                        break;
                    case 2:
                        findOwnersInfoByREid(sql);
                        break;
                    case 3:
                        updateREprice(sql);
                        break;
                    case 4:
                        makeNewDeal(sql);
                        break;
                    case 5:
                        viewREobjects(sql);
                        break;
                    case 6 :
                        viewDeal(sql);
                        break;
                    case 7 :
                        deleteDeal(sql);
                        break;
                    case 8 :
                        sql.close();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Skaicius turi egzistuoti prie menu pasirinkimo, iveskite dar karta:");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Ivesties skaitymo klaida");
            }
        }
    }

    public void menuOptions () {
        System.out.println("Pasirinkite veiksma:");
        System.out.println("0 - rasti brokeriui priskirtus nekilnojamo turto objektus");
        System.out.println("1 - atleisti brokeri");
        System.out.println("2 - rasti informacija apie nekilnojamo turto savininka ir turta pagal turto ID");
        System.out.println("3 - atnaujinti nekilnojamo turto kaina");
        System.out.println("4 - sudaryti nauja sandori");
        System.out.println("5 - perziureti visus nekilnojamo turto objektus");
        System.out.println("6 - perziureti visus sandorius");
        System.out.println("7 - pasalinti sandori");
        System.out.println("8 - iseiti");
    }

    public void findREassignedToAgent (SQL sql) throws SQLException{
        List<List> result;
        printAgentTable(sql.getAllAgents());
        System.out.println("Iveskite brokerio ID:");
        int agentID;
        while (true) {
            try {
                agentID = scan.nextInt();
                scan.nextLine();
                break;
            } catch (Exception e) {
                System.out.println("Brokerio ID privalo buti skaicius, iveskite dar karta:");
                scan.nextLine();
            }
        }

        try {
            result = sql.getAllREsAssignedToAgent(agentID);
            printREanAgentsInfo(result);
        } catch (Exception e) {
            System.out.println("Klaida: " + e.getMessage());
        }
    }

    public void dissmissAgent (SQL sql){
        try {
            printAgentTable(sql.getAllAgents());
            int agentID;
            System.out.println("Iveskite brokerio, kuri norite atleisti, ID:");
            while (true) {
                try {
                    agentID = scan.nextInt();
                    scan.nextLine();
                    break;
                } catch (Exception e) {
                    System.out.println("Brokerio ID privalo buti skaicius, iveskite dar karta:");
                    scan.nextLine();
                }
            }
            sql.dissmissAgentandRemoveContract(agentID);
            System.out.println("Brokeris istrintas sekmingai");
            printAgentTable(sql.getAllAgents());
            printREanAgentsInfo(sql.getAllREsAssignedToAgent(agentID));
        } catch (SQLException e){
            System.out.println("Klaida: " + e.getMessage());
        }
    }

    public void findOwnersInfoByREid (SQL sql) throws SQLException{
        List<List> result;
        printRETable(sql.getAllREs());
        System.out.println("Iveskite turto ID:");
        int reID;
        while (true) {
            try {
                reID = scan.nextInt();
                scan.nextLine();
                break;
            } catch (Exception e) {
                System.out.println("Turto ID privalo buti skaicius, iveskite dar karta:");
                scan.nextLine();
            }
        }
        try {
            result = sql.getOwnersInfoByREID(reID);
            printREandOwnersInfo(result);
        } catch (Exception e) {
            System.out.println("Klaida: " + e.getMessage());
        }
    }

    public void updateREprice (SQL sql){
        viewREobjects(sql);
        System.out.println("Pateikite reikiama informacija");
        System.out.println("Turto, kurio informacija norite atnaujinti, ID:");
        int reID;
        while (true) {
            try {
                reID = scan.nextInt();
                scan.nextLine();
                break;
            } catch (Exception e) {
                System.out.println("Turto ID privalo buti skaicius, iveskite dar karta:");
                scan.nextLine();
            }
        }

        System.out.println("Nauja kaina:");
        double price;
        while (true) {
            try {
                price = scan.nextDouble();
                if(price <= 0){
                    System.out.println("Kaina turi buti didesne uz nuli, iveskite dar karta:");
                    scan.nextLine();
                }
                else{
                    scan.nextLine();
                    break;
                }
            } catch (Exception e) {
                System.out.println("Kaina turi buti skaicius, iveskite dar karta:");
                scan.nextLine();
            }
        }

        try {
            sql.updateREprice(reID, price);
            System.out.println("Kaina atnaujinta sekmingai");
        } catch (Exception e) {
            System.out.println("Klaida: " + e.getMessage());
        }
    }

    public void makeNewDeal (SQL sql) throws SQLException{
        Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
        int reID;
        long no;
        String personalNO;
        String date;
        double taxes;

        printRETable(sql.getAllREs());
        printBuyerTable(sql.getAllBuyers());
        printDealTable(sql.getAllDeals());

        System.out.println("Pateikite sandoriui reikalinga informacija");
        System.out.println("Turto ID:");
        while (true) {
            try {
                reID = scan.nextInt();
                scan.nextLine();
                break;
            } catch (Exception e) {
                System.out.println("Turto ID privalo buti skaicius, iveskite dar karta:");
                scan.nextLine();
            }
        }
        System.out.println("Pirkejo asmens kodas:");
        while (true) {
            try {
                no = scan.nextLong();
                if((Long.toString(no)).length() != 11) {
                    System.out.println("Asmens koda turi sudaryti 11 skaiciu, iveskite dar karta:");
                    scan.nextLine();
                }
                else{
                    scan.nextLine();
                    break;
                }
            } catch (Exception e) {
                System.out.println("Pirkejo a.k. privalo buti skaicius, iveskite dar karta:");
                scan.nextLine();
            }
        }
        personalNO = Long.toString(no);
        System.out.println("Data YYYY-MM-DD formatu (galite praleisti - bus irasyta siandienos data):");
        while (true) {
            try {
                date = scan.nextLine();
                if(DATE_PATTERN.matcher(date).matches() || date == "")
                    break;
                else
                    throw new Exception("Datos formatas turi buti YYYY-MM-DD iveskite dar karta:");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Mokesciai:");
        while (true) {
            try {
                taxes = scan.nextDouble();
                if(taxes < 0){
                    System.out.println("Mokesciu suma negali buti neigiama, iveskite dar karta:");
                    scan.nextLine();
                }
                else{
                    scan.nextLine();
                    break;
                }
            } catch (Exception e) {
                System.out.println("Mokesciu suma turi buti skaicius, iveskite dar karta:");
                scan.nextLine();
            }
        }

        try {
            sql.signNewDeal(reID, personalNO, date, taxes);
            System.out.println("Sandoris pridetas sekmingai");
        } catch (Exception e) {
            System.out.println("Klaida: " + e.getMessage());
        }
    }

    public void viewREobjects (SQL sql){
        List<List> result;

        try {
            result = sql.getAllREs();
            printRETable(result);
        } catch (Exception e) {
            System.out.println("Klaida: " + e.getMessage());
        }
    }

    public void viewDeal(SQL sql){
        List<List> result;

        try {
            result = sql.getAllDeals();
            printDealTable(result);
        } catch (Exception e) {
            System.out.println("Klaida: " + e.getMessage());
        }
    }

    public void deleteDeal (SQL sql) {
        List<List> result;

        try {
            printDealTable(sql.getAllDeals());
            int idToDelete;
            System.out.println("Iveskite norimo istrinti sandorio ID:");
            while (true) {
                try {
                    idToDelete = scan.nextInt();
                    scan.nextLine();
                    break;
                } catch (Exception e) {
                    System.out.println("Sandorio ID privalo buti skaicius, iveskite dar karta:");
                    scan.nextLine();
                }
            }
            sql.removeDeal(idToDelete);
            System.out.println("Sandoris istrintas sekmingai");
        } catch (Exception e) {
            System.out.println("Klaida: " + e.getMessage());
        }
    }

    public void printREanAgentsInfo (List<List> info){
        System.out.println("Rasta informacija:");
        System.out.println("--------------------------------------------------------------");
        System.out.printf("| %-10s | %-12s | %-12s | %-15s | %n", "Turto ID", "Turto kaina", "Vardas", "Pavarde");
        System.out.println("--------------------------------------------------------------");
        for (List r : info) {
            System.out.printf("| %-10s | %-12s | %-12s | %-15s |%n", r.get(0), r.get(1), r.get(2), r.get(3));
        }
        System.out.println("--------------------------------------------------------------");
    }


    public void printREandOwnersInfo (List<List> info){
        System.out.println("Rasta informacija:");
        System.out.println("----------------------------------------------------------------------------------");
        System.out.printf("| %-30s | %-12s | %-15s | %-12s | %n", "Turto adresas", "Vardas", "Pavarde", "Tel. numeris");
        System.out.println("----------------------------------------------------------------------------------");
        for (List r : info) {
            System.out.printf("| %-30s | %-12s | %-15s | %-12s |%n", r.get(0), r.get(1), r.get(2), r.get(3));
        }
        System.out.println("----------------------------------------------------------------------------------");
    }

    public void printRETable (List<List> REobjects){
        System.out.println("Nekilnojamo turto objektai:");
        System.out.println("------------------------------------------------------------------------------");
        System.out.printf("| %-10s | %-10s | %-15s | %-30s | %n", "ID", "Kaina", "Savininko Ak", "Adresas");
        System.out.println("------------------------------------------------------------------------------");
        for (List r : REobjects) {
            System.out.printf("| %-10s | %-10s | %-15s | %-30s |%n", r.get(0), r.get(1), r.get(2), r.get(3));
        }
        System.out.println("------------------------------------------------------------------------------");
    }

    public void printDealTable (List<List> deals){
        System.out.println("Sandoriai:");
        System.out.println("---------------------------------------------------------------------");
        System.out.printf("| %-3s | %-10s | %-15s | %-15s | %-10s | %n", "ID", "Turto ID", "Pirkejo AK", "Sandorio data", "Mokesciai");
        System.out.println("---------------------------------------------------------------------");
        for (List r : deals) {
            System.out.printf("| %-3s | %-10s | %-15s | %-15s | %-10s | %n", r.get(0), r.get(1), r.get(2), r.get(3), r.get(4));
        }
        System.out.println("---------------------------------------------------------------------");
    }

    public void printAgentTable (List<List> agents){
        System.out.println("Brokeriai:");
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.printf("| %-10s | %-12s | %-15s | %-35s | %n", "ID", "Vardas", "Pavarde", "El. pastas");
        System.out.println("-------------------------------------------------------------------------------------");
        for (List r : agents) {
            System.out.printf("| %-10s | %-12s | %-15s | %-35s | %n", r.get(0), r.get(1), r.get(2), r.get(3));
        }
        System.out.println("-------------------------------------------------------------------------------------");
    }
    public void printBuyerTable (List<List> buyers){
        System.out.println("Pirkejai:");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-15s | %-12s | %-15s | %-35s | %-12s | %n", "Asmens kodas", "Vardas", "Pavarde", "El. pastas", "Tel. numeris");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        for (List r : buyers) {
            System.out.printf("| %-15s | %-12s | %-15s | %-35s | %-12s | %n", r.get(0), r.get(1), r.get(2), r.get(3), r.get(4));
        }
        System.out.println("---------------------------------------------------------------------------------------------------------");
    }
}
