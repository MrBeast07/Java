package com.banking.Backend;

import com.banking.Servlets.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Scanner;



class BankManagement{
    
        Scanner scan=new Scanner(System.in);

    public BankManagement(){
        
        System.out.println("1.Get All Accounts");
        System.out.println("2.Print passbook");
        System.out.println("3.Create new Account");
        System.out.println("4.Delete a account");
        System.out.println("5.Deposit amount");
        System.out.println("6.Withdraw amount");
        System.out.println("7.Transfer");

        
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
        return "INSERT INTO account(cus_id,name,acc_type,acc_bal) Values(?,?,?,?)";
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

class TransferAmount extends Banking{
    
    static void transfer(long accNum1, long accNum2, int amt) {
        String check1="Select acc_bal from account where acc_no="+accNum1;
        
        String query1="update account set acc_bal=acc_bal-? where acc_no=?";
        String query2="update account set acc_bal=acc_bal+? where acc_no=?";
        
        try {
            con=DriverManager.getConnection(url,un,pwd);
            Statement stmt = con.createStatement();
            ResultSet res = stmt.executeQuery(check1);
            
            int balance=0;
            while(res.next()){
                balance=res.getInt("acc_bal");
            }
            if(balance>=amt){
                PreparedStatement pstmt1=con.prepareStatement(query1);
                pstmt1.setInt(1,amt);
                pstmt1.setLong(2, accNum1);
                
                PreparedStatement pstmt2=con.prepareStatement(query2);
                pstmt2.setInt(1,amt);
                pstmt2.setLong(2, accNum2);
                
                con.setAutoCommit(false);
                int i=pstmt1.executeUpdate();
                int j=pstmt2.executeUpdate();
                if(i==j){
                    con.commit();
                    System.out.println("Transaction Successful");
                }
                else{
                    con.rollback();
                    System.out.println("Transaction Failed amount will be refunded");
                }
            }
            else{
                System.out.println("Insufficient balance");
            }
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}


    

public class Banking{
    static Scanner scan=new Scanner(System.in);
    static Connection con;
    static   Statement stmt;
    static PreparedStatement pstmt;
    static ResultSet res;
    static String query="";
    static String url="jdbc:mysql://localhost:3306/bank";
    static String un="root";
    static String pwd="Bharath@2110";

    static{
        try{
            
            Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("Driver class is Loaded");
                Connection con=DriverManager.getConnection(url,un,pwd);
                System.out.println("Connection Established");
                stmt=con.createStatement();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        

    }
    public static void main(String[] args) {

         try{
                
                BankManagement bm=new BankManagement();
                int choice=scan.nextInt();
                
                if(choice==1){
                    query=BankManagement.getAllAcc();
                    
                }
                else if(choice==2){
                    System.out.println("Enter Customer ID");
                    int cusid=scan.nextInt();
                    query=BankManagement.passbook(cusid);
                    
                }else if(choice==3){
                    Scanner scanner=new Scanner(System.in);
                    pstmt=con.prepareStatement(BankManagement.addAccount());
                    System.out.println("Enter id");
                pstmt.setInt(1,scanner.nextInt());
                System.out.println("Enter name");
                pstmt.setString(2,scanner.next());
                
                System.out.println("Enter acc type");
                pstmt.setString(3,scanner.next());
                System.out.println("Enter initial amount");
                pstmt.setInt(4,scanner.nextInt());
                int i=pstmt.executeUpdate();
                System.out.print("acc created with "+i+" "+"updates");
                return;
            }
            else if(choice==4){
                System.out.println("Enter account nnumber to delete");
                long accNum=scan.nextLong();
                String q=BankManagement.deleteAccount(accNum);
                System.out.println("Account deleted");
                stmt.execute(q);
                return;
            }
            else if(choice==5){
                System.out.println(BankManagement.deposit(con));
                return;
            }
            else if(choice==6){
                BankManagement.withdraw(con);
                return;
            }
            else if(choice==7){
                System.out.println("Enter sender account no");
                long accNum1=scan.nextLong();
                System.out.println("Enter amount to transfer");
                int amt=scan.nextInt();
                System.out.println("Enter receiver acc no");
                long accNum2=scan.nextLong();
                TransferAmount.transfer(accNum1, accNum2, amt);
                return;
            }
                //String query="SELECT* FROM account";
                res=stmt.executeQuery(query);
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
        
       catch(java.sql.SQLException e){
        e.printStackTrace();


       }   
         
         
    }
}
