
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
  private int currentUserID;

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
    Scanner scn = new Scanner(System.in);
    String option;

    while(true){
      termProject.optionList();
      option = scn.next();
      option = option.toLowerCase();
      if(option.compareTo("login") == 0) {

        if(termProject.login() > 0) {
          System.out.println();
          System.out.println("Login Successful");
          System.out.println();
          System.out.println("Welcome to Faces@Pitt");
          termProject.mainPage(termProject);
        }else{
          System.out.println();
          System.out.println("Login Unsuccessful");
          termProject.optionList();
        }

      }else if(option.compareTo("register") == 0){
        
        termProject.register();
        System.out.println();
        System.out.println("Account Created");
        System.out.println();
        System.out.println("Welcome to Faces@Pitt");
        termProject.mainPage(termProject);

      }else if (option.compareTo("exit") == 0) {
        termProject.exit();
        System.exit(1);
      }

    }
  }

  public void exit(){
    try{
      state.close();
      System.exit(1);
    }catch(Exception Ex){
      System.out.println("Error connecting to database.  Machine Error: " + Ex.toString());
      Ex.printStackTrace();
    }
  }

  // MainPage 
  public void mainPage(team01 termProject){
    Scanner scn = new Scanner(System.in);
    String option;
    try{
      while(true){
        termProject.optionListLogin();
        option = scn.next().toLowerCase();
        if(option.compareTo("out") == 0){
          currentUserID = 0;
          return;
        }else if (option.compareTo("all") == 0) {
          termProject.getAllMessages();
        }else if (option.compareTo("group") == 0) {
          termProject.addToGroup(termProject);
        }
      }
    }catch(Exception Ex){
      System.out.println("Error connecting to database.  Machine Error: " + Ex.toString());
      Ex.printStackTrace();
    }
  }

  // TODO: If a user tries to join a group that they have already joined, deal with that problem.
  public void addToGroup(team01 termProject) {
    try{
      termProject.listGroups();
      String groupID;
      Scanner scn = new Scanner(System.in);
      System.out.println();
      System.out.println("Please Enter a Group ID Number:");
      groupID = scn.nextLine();
      int num = 0;

      // check to see if the user is already a member of the group and return to the main page menu
      query = "SELECT * FROM GroupMembership WHERE gID=" + groupID + "AND userID=" + currentUserID;
      rs = state.executeQuery(query);

      if(rs.next()) {
        System.out.println();
        System.out.println("You are already a member of this group.");
        return;
      }

      // grab the total number of members of the selected group
      query = "SELECT count(*) FROM GroupMembership WHERE gID=" + groupID;  
      rs = state.executeQuery(query);

      // store the number
      if(rs.next()) {
        num = rs.getInt(1);
      }

      // if there are less than 10 members, add the user to the group
      if(num < 10){
        query = "INSERT INTO GroupMembership VALUES (" + groupID + "," + currentUserID + ")";
        int result = state.executeUpdate(query);
        if(result > 0){
          System.out.println();
          System.out.println("You are now added to the group.");
        }else{
          System.out.println();
          System.out.println("Unable to add you to the group.");
        }
      // otherwise let the user know that the group is full
      }else{
        System.out.println();
        System.out.println("Sorry, the group is full.");
      }
    }catch(Exception Ex){
      System.out.println("Error connecting to database.  Machine Error: " + Ex.toString());
      Ex.printStackTrace();
    }

  }

  public void listGroups() {
    try{
      query = "SELECT * FROM Groups";  
      rs = state.executeQuery(query);


      while(rs.next()) {
        System.out.println();
        System.out.println("Group ID         : " + rs.getInt(1));
        System.out.println("Group Name       : " + rs.getString(2));
        System.out.println("Group Description: " + rs.getString(3));
      }

      System.out.println();
    }catch(Exception Ex){
      System.out.println("Error connecting to database.  Machine Error: " + Ex.toString());
      Ex.printStackTrace();
    }
  }

  public void getAllMessages() {
    try{

      // getting all of the msgID's of the messages the current user received
      query = "SELECT msgID FROM MessageRecipients WHERE userID =" + currentUserID;
      rs = state.executeQuery(query);
      ArrayList<Integer> msgIDs = new ArrayList<Integer>();
      int numMsgID;
      while(true) {
        if(rs.next()){
          msgIDs.add(rs.getInt(1));
        }else{
          break;
        }
      }

      numMsgID = msgIDs.size();

      if(numMsgID == 0){
        System.out.println();
        System.out.println("You have no messages.");
        return;
      }

      // getting all of the messages that corresponds to the msgID
      SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
      int msgID;
      int fromID;
      String message;
      int toUserID;
      int toGroupID;
      java.util.Date dateSent;
      for(int x = 0; x < numMsgID; ++x) {
        query = "SELECT * FROM Messages WHERE msgID =" + msgIDs.get(x);
        ResultSet result = state.executeQuery(query);
        ResultSet extra;
        if(result.next()){
          msgID = result.getInt(1);
          fromID = result.getInt(2);
          message = result.getString(3);
          toUserID = result.getInt(4);
          toGroupID = result.getInt(5);
          dateSent = result.getDate(6);

          // print out the message ID
          System.out.println();
          System.out.println("Message ID: " + msgID);

          // grab the name of the person who sent the message and print it out
          query = "SELECT name FROM Profile WHERE userID = " + fromID;
          extra = state.executeQuery(query);
          extra.next();
          System.out.println("From      : " + extra.getString(1));

          // print out the actual message
          System.out.println("Message   : "  + message);

          // if there is a toUserID set, get the name of the person with
          // that ID, otherwise, print out blank
          if(toUserID == 0){ 
            System.out.println("To User   : ");
          }else{
            query = "SELECT name FROM Profile WHERE userID = " + toUserID;
            extra = state.executeQuery(query);
            extra.next();
            System.out.println("To User   : " + extra.getString(1));
          }

          // if there is a toGroupID set, get the name of the group with
          // that ID, otherwise, print out blank
          if(toGroupID == 0){
            System.out.println("To Group  :");
          }else{
            query = "SELECT name FROM Groups WHERE gID = " + toGroupID;
            extra = state.executeQuery(query);
            extra.next();
            System.out.println("To Group  : " + extra.getString(1));            
          }

          // print out the date sent
          System.out.println("Date Sent : "  + df.format(dateSent));

        }else{
          System.out.println("ERRROOORRRRR");
        }

      }

    }catch(Exception Ex){
      System.out.println("Error connecting to database.  Machine Error: " + Ex.toString());
      Ex.printStackTrace();
    }
  }

  public int login(){
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

        // save the current userID
        currentUserID = rs.getInt(1);

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

  public void register() {

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
      currentUserID = currentMaxID;
    }catch(Exception Ex)  //What to do with any exceptions
    {
      System.out.println("Error connecting to database.  Machine Error: " + Ex.toString());
      Ex.printStackTrace();
    }

  }

  public void optionList(){
    System.out.println();
    System.out.println("What would you like to do?");
    System.out.println();
    System.out.println("LOGIN");
    System.out.println("REGISTER");
    System.out.println("EXIT");
    System.out.println();
    System.out.println("Option: (Please Enter an Upper Case Key Word) ");
  }

  public void optionListLogin(){
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
    System.out.println();
    System.out.println("Option: (Please Enter an Upper Case Key Word) ");

  }
}
