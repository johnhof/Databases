
import java.sql.*; 
import java.util.*;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;

public class team01
{
  private Connection connection;
  private Statement state;
  private ResultSet rs;
  private String query;
  private String username, password;
  private int currentMaxID;

  public team01()
  {
    username = "sag89"; //This is your username in oracle
    password = "dumble3#"; //This is your password in oracle
    try{
      DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
      String url = "jdbc:oracle:thin:@db10.cs.pitt.edu:1521:dbclass"; 
      
      connection = DriverManager.getConnection(url, username, password); 
      state = connection.createStatement();
      query = "SELECT max(userID) FROM Profile";
      rs = state.executeQuery(query);

      if(rs.next()) {
        currentMaxID = rs.getInt(1);
      }
      //create a connection to DB on db10.cs.pitt.edu
    }
    catch(Exception Ex)  //What to do with any exceptions
    {
      System.out.println("Error connecting to database.  Machine Error: " + Ex.toString());
      Ex.printStackTrace();
    }
  }

  public static void main(String args[])
  {
    team01 termProject = new team01();
    termProject.OptionList();
    Scanner scn = new Scanner(System.in);
    String option;

    while(true){
      option = scn.next();
      option = option.toLowerCase();
      if(option.compareTo("login") == 0) {

        if(termProject.Login() > 0) {
          System.out.println("Login Successful");
          System.out.println();
        }else{
          System.out.println("Login Unsuccessful");
          System.out.println();
          termProject.OptionList();
        }

      }else if(option.compareTo("register") == 0){
        
        termProject.Register();
        System.out.println("Account Created");
        termProject.MainPage(termProject);

      }else if (option.compareTo("exit") == 0) {
        System.exit(1);
      }

    }
  }

  public void MainPage(team01 termProject){
    Scanner scn = new Scanner(System.in);
    String option;
    try{
      while(true){
        option = scn.next().toLowerCase();
        termProject.OptionListLogin();
        if(option.compareTo("out") == 0){
          state.close();
          System.exit(1);
        } 
      }
    }catch(Exception Ex){
      System.out.println("Error connecting to database.  Machine Error: " + Ex.toString());
      Ex.printStackTrace();
    }
  }

  public int Login(){
    Scanner sc = new Scanner(System.in);
    int retval = 0;

    // get email
    System.out.print("Username: ");
    String email = sc.next();

    // get password
    System.out.print("Password: ");
    String pass = sc.next();

    try{
      query = "SELECT * FROM Profile WHERE email='" + email + "' AND password='" + pass + "'";
      rs = state.executeQuery(query);

      // if the email/password combo exists sign them in
      if(rs.next()){
        // this is the get todays date
        long time = System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String today = df.format(new java.util.Date(time));
        
        // Updating the Profile table with the date the user logged in if they typed in the correct 
        // user information   
        query = "UPDATE Profile SET lastlogin = '" + today + "' WHERE email='" + email + "'";
        int result = state.executeUpdate(query); 
        retval = 1;
      }
      else{
        retval = 0;
      }
    }catch(Exception Ex)  //What to do with any exceptions
    {
      System.out.println("Error connecting to database.  Machine Error: " + Ex.toString());
      Ex.printStackTrace();
    }

    return retval;
  }

  public void Register() {

    try{
      String regex = "^\\w\\w\\w(\\d\\d|\\d|)@pitt.edu";
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher; 
      Scanner scn = new Scanner(System.in);
      boolean matches;

      //getting first name
      System.out.println();
      System.out.print("Enter your first name: ");
      String fname = scn.nextLine();

      // getting pitt email address
      String email;
      while(true){
        System.out.println();
        System.out.print("Enter your Pitt email: ");
        email = scn.nextLine();

        matcher = pattern.matcher(email);

        // if the pitt email pattern matches
        if(matcher.matches()) {
          query = "SELECT * FROM Profile WHERE email = '" + email + "'";
          rs = state.executeQuery(query);

          // check to see if the email already exists
          if(rs.next())
          {
            // if it does, ask for a new email address
            System.out.println();
            System.out.println("Email already exists.");
          // otherwise, ask for the rest of the information  
          }else{
            break;
          }
        // if it's not a proper email address format, ask again  
        }else{
          System.out.println();
          System.out.println("Incorrect Pitt email address");
        }
      }

      // ask for a password
      System.out.println();
      System.out.print("Enter a password: ");
      String password = scn.nextLine();

      regex = "^\\d\\d-(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)-\\d\\d\\d\\d";
      pattern = Pattern.compile(regex);
      String birthday;

      // keep asking for a birthday until they enter a birthday in the proper format
      while(true) {
        System.out.println();
        System.out.print("Enter your birthday (dd-MMM-yyyy): ");
        birthday = scn.nextLine(); 

        matcher = pattern.matcher(birthday);

        if(matcher.matches()) {
          birthday = birthday.toUpperCase();
          break;
        }else{
          System.out.println();
          System.out.println("Incorrect birth date format");
        }
      }

      // ask for a short description
      System.out.println();
      System.out.print("Enter a short self description: ");
      
      String description = scn.nextLine();


      // at this point we got all of our information so we can add it into
      // the Profile table in the database

      long time = System.currentTimeMillis();
      SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
      String today = df.format(new java.util.Date(time));

      query = "INSERT INTO Profile (userID, name, email, password, date_of_birth, picture_URL, aboutme, lastlogin) VALUES (" + (currentMaxID +1) + ", '" + fname + "','"  + email + "', '" 
        + password + "', '" + birthday + "', 'NULL', '" + description + "', '" + today + "')"; 
      int rs = state.executeUpdate(query);

      currentMaxID++;
    }catch(Exception Ex)  //What to do with any exceptions
    {
      System.out.println("Error connecting to database.  Machine Error: " + Ex.toString());
      Ex.printStackTrace();
    }

  }

  public void OptionList(){
    System.out.println();
    System.out.println("What would you like to do?");
    System.out.println();
    System.out.println("LOGIN");
    System.out.println("REGISTER");
    System.out.println("EXIT");
    System.out.println();
    System.out.println("Option: (Please Enter an Upper Case Key Word): ");
  }

  public void OptionListLogin(){
    System.out.println();
    System.out.println("Welcome to Faces@Pitt");
    System.out.println();
    System.out.println("SEND Message");
    System.out.println("ADD Friend");
    System.out.println("Display ALL Messages"); 
    System.out.println("Display NEW Messages");
    System.out.println("Display FRIENDS");
    System.out.println("FIND User");
    System.out.println("CONFIRM Friend Requests");
    System.out.println("THREE Degrees of Separation");
    System.out.println("Join a GROUP");
    System.out.println("My STATISTICS");
    System.out.println("DROP Account");
    System.out.println("Log OUT");

  }
}
