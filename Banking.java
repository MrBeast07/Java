
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;



class BankManagement{
    
        Scanner scan=new Scanner(System.in);

    public BankManagement() {
        
        System.out.println("1.Get All Accounts");
        System.out.println("2.Print passbook");
        System.out.println("3.Create new Account");
        System.out.println("4.Delete a account");
        System.out.println("5.Deposit amount");
        System.out.println("6.Withdraw amount");

        
    }
    static String getAllAcc(){
        String query="SELECT* FROM account";
        return query;
    }   
    static String passbook(int cusid){
        String query = "SELECT * FROM account Where cus_id="+cusid;
        return query;
    }
    static String addAccount(){
        return "INSERT INTO account(cus_id,name,acc_no,acc_type,acc_bal) Values(?,?,?,?,?)";
    }
    static String deleteAccount(long accNum){
        return "DELETE FROM account where acc_no="+accNum +";" ;
    }

    static String deposit(Connection con) {
        String transactionType = "credited";
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Enter account number to deposit:");
        long acc_no = scanner.nextLong();
        
        System.out.println("Enter amount to deposit:");
        int amount = scanner.nextInt();
        
        System.out.println("Enter order number:");
        int order = scanner.nextInt();
        
        String query1 = "INSERT INTO transaction(trans_amt, trans_type, order_num, acc_no) VALUES(?, ?, ?, ?)";
        String query2 = "UPDATE account SET acc_bal = acc_bal + ? WHERE acc_no = ?";
        
        try {
            con.setAutoCommit(false);
            // Update account balance
            PreparedStatement pstmt2 = con.prepareStatement(query2);
            pstmt2.setInt(1, amount);
            pstmt2.setLong(2, acc_no);
            int i=pstmt2.executeUpdate();
            // Insert transaction record
            
            PreparedStatement pstmt1 = con.prepareStatement(query1);
            pstmt1.setInt(1, amount);
            pstmt1.setString(2, transactionType);
            pstmt1.setInt(3, order);
            pstmt1.setLong(4, acc_no);
            int j=pstmt1.executeUpdate();
            if(i==1 && j==1){
                con.commit();
            }else{
                con.rollback();
                System.out.println("Couldnt complete transaction");
            }
            
            
            
            System.out.println("Amount deposited successfully.");
            
        } catch (Exception e) {
            //e.printStackTrace();
            return "An error occurred while depositing the amount. Check acc no and try again ";
        } finally {
            
            scanner.close();
        }
        return "Deposit operation completed.";
    }

    static void withdraw(Connection con){
        String trans_type = "debited";
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter account number to withdraw:");
            long acc_no = scanner.nextLong();

            System.out.println("Enter amount to withdraw:");
            int amt = scanner.nextInt();

            System.out.println("Enter the order number:");
            int order = scanner.nextInt();

            try {
                // Start a transaction
                con.setAutoCommit(false); // Disable auto-commit mode
                
                Statement stmt = con.createStatement();
                
                // Fetch the current account balance
                ResultSet res = stmt.executeQuery("SELECT acc_bal FROM account WHERE acc_no = " + acc_no);
                
                if (res.next()) {
                    int avail = res.getInt(1); // Get the account balance
                    
                    System.out.println("Available balance: " + avail);
                    
                    // Check if the balance is sufficient
                    if (avail >= amt) {
                        // Prepare queries
                        String query1 = "INSERT INTO transaction (trans_amt, trans_type, order_num, acc_no) VALUES (?, ?, ?, ?)";
                        String query2 = "UPDATE account SET acc_bal = acc_bal - ? WHERE acc_no = ?";
                        
                        // Insert transaction record
                        PreparedStatement pstmt1 = con.prepareStatement(query1);
                        pstmt1.setInt(1, amt);
                        pstmt1.setString(2, trans_type);
                        pstmt1.setInt(3, order);
                        pstmt1.setLong(4, acc_no);
                        pstmt1.executeUpdate();
                        
                        // Update account balance
                        PreparedStatement pstmt2 = con.prepareStatement(query2);
                        pstmt2.setInt(1, amt);
                        pstmt2.setLong(2, acc_no);
                        pstmt2.executeUpdate();
                        
                        // Commit the transaction
                        con.commit();
                        System.out.println("Withdrawal successful.");
                    } else {
                        System.out.println("Insufficient funds. Available balance: " + avail);
                        con.rollback(); // Rollback if insufficient funds
                    }
                } else {
                    System.out.println("Account number not found.");
                    con.rollback(); // Rollback if account number not found
                }

                // Close resources
                res.close();
                stmt.close();
                
            } catch (Exception e) {
                try {
                    con.rollback(); // Rollback the transaction in case of any exception
                    System.out.println("Transaction failed. Rolled back.");
                } catch (Exception rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                e.printStackTrace();
            } finally {
                try {
                    con.setAutoCommit(true); // Restore auto-commit mode
                } catch (Exception autoCommitEx) {
                    autoCommitEx.printStackTrace();
                }
                
                // Close the scanner
                scanner.close();
            }

    }
}
    



class Banking{
    public static void main(String[] args) {

        Scanner scan=new Scanner(System.in);
        String url="jdbc:mysql://localhost:3306/bank";
            String un="root";
            String pwd="Bharath@2110";

         try{
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("Driver class is Loaded");
                Connection con=DriverManager.getConnection(url,un,pwd);
                System.out.println("Connection Established");
                Statement stmt=con.createStatement();
                
                String query="";
                BankManagement bm=new BankManagement();
                int choice=scan.nextInt();
                if(choice==1){
                    query=bm.getAllAcc();
                    
                }
                else if(choice==2){
                    System.out.println("Enter Customer ID");
                    int cusid=scan.nextInt();
                    query=bm.passbook(cusid);
                    
                }else if(choice==3){
                    Scanner scanner=new Scanner(System.in);
      
                
                    PreparedStatement pstmt=con.prepareStatement(bm.addAccount());
                    System.out.println("Enter id");
                pstmt.setInt(1,scanner.nextInt());
                System.out.println("Enter name");
                pstmt.setString(2,scanner.next());
                System.out.println("Enter account number");
                pstmt.setLong(3,scanner.nextLong());
                System.out.println("Enter acc type");
                pstmt.setString(4,scanner.next());
                System.out.println("Enter initial amount");
                pstmt.setInt(5,scanner.nextInt());
                int i=pstmt.executeUpdate();
                System.out.print("acc created with "+i+" "+"updates");
                return;
            }
            else if(choice==4){
                System.out.println("Enter account nnumber to delete");
                long accNum=scan.nextLong();
                String q=bm.deleteAccount(accNum);
                System.out.println("Account deleted");
                stmt.execute(q);
                return;
            }
            else if(choice==5){
                System.out.println(bm.deposit(con));
                return;
            }
            else if(choice==6){
                bm.withdraw(con);
                return;
            }
                //String query="SELECT* FROM account";
                ResultSet res=stmt.executeQuery(query);
                System.out.println("query executed");
                while(res.next()==true){
                    int cus_id=res.getInt(1);
                    String name=res.getString(2);
                    
                    long acc_no=res.getLong(3);
                    String acc_type=res.getString(4);
                    long acc_bal=res.getLong(5);
                    System.out.println(cus_id+" "+name+" "+acc_no+" "+acc_type+" "+acc_bal);
                   }



        } 
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
       catch(java.sql.SQLException e){
        e.printStackTrace();


       }   
    }
}