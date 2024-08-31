import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Random;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
public class UserDeviceServlet extends HttpServlet {
    private String user_jdbcURL = "jdbc:mysql://localhost:3306/userdb";
    private String user_jdbcuserid = "root";
    private String user_jdbcPassword = "Cartoon";
    private Connection user_jdbcConnection;
static private int n=1;
String f, deviceId, R1sg;
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    // Step 1: Retrieve biometric information
    String bio1 = request.getParameter("bio1");
    String bio2 = request.getParameter("bio2");


    // Step 2: Retrieve Beta value from the database
    String Beta = getBetaFromDatabase(bio1, bio2);

    boolean verificationSuccessful = false; // Flag to indicate if nonce verification was successful
if(n==1){
    if (Beta != null) {
        String nonceN1 = NonceGenerator.generateNonce();
n++;


        sendToGateway(Beta, nonceN1, request, response);


    } else {
        // Redirect to the error page if Beta retrieval fails
        response.sendRedirect("errormsg.jsp");
        return;
    }

}else{
    // Now perform the verification logic for nonceN2
    String nonceN2 = (String) request.getAttribute("nonceN2");
String challengeCU = (String) request.getAttribute("challenge");
boolean isNonceN2Valid = NonceVerifier.verifyNonce(nonceN2);

        if (isNonceN2Valid) {
            // NonceN2 verification successful, proceed with response calculation

            byte[] challengeBytes = Base64.getDecoder().decode(challengeCU);
            byte challengeId = 123; // Replace with the actual challengeId

            // Calculate the expected response using the provided logic
            String responseCU = calculateExpectedResponse(challengeBytes, challengeId);

            // Set the verification flag to true
            verificationSuccessful = true;
try (FileWriter fileWriter = new FileWriter("C:/Program Files/apache-tomcat-8.5.87/webapps/AUTHENTICATION/Beta.log", true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println("checkhere22");
        }

String nonceN3 = NonceGenerator.generateNonce();
 int Q = new Random().nextInt();
int fInt = 0;
            fInt = Integer.parseInt(f);
        int Qf = fInt * Q;


int deviceIdInt = 0;
        
            deviceIdInt = Integer.parseInt(deviceId);
        
        int QIDu = deviceIdInt * Q;
String hashedBio1 = null;
String hashedresponseCU = null;
        try {
            hashedBio1 = calculateHash(bio1);
            hashedresponseCU=calculateHash(responseCU);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
// Step 3: Multiply the hashed value of bio1 with challengeCU
        int CU = Integer.parseInt(challengeCU);
        int hashedBio1Int = Integer.parseInt(hashedBio1, 16); // Convert hashedBio1 to int
int hashedRuInt = Integer.parseInt(hashedresponseCU, 16);
        int hashbio1Cu = hashedBio1Int * CU;
        int hashRuCu = hashedRuInt * CU;

        // Step 4: Calculate R1 = Q + hashbio1cu
        int R1 = Q + hashbio1Cu;
        int R2= Q + hashRuCu;
 sendToGateway(nonceN3, Qf, QIDu, R1, R2, request, response);


    // ... Rest of the verification logic and response calculation ...


    // Perform the final redirect based on the verification result
    if (verificationSuccessful) {
        response.sendRedirect("register.jsp");
    } else {
        response.sendRedirect("error.jsp");
    }
}

}}
private void sendToGateway(String nonceN3, int Qf,int QIDu, int R1, int R2, HttpServletRequest request, HttpServletResponse response)
throws ServletException, IOException {
// Get the servlet context
        ServletContext context = getServletContext();
        // Get the RequestDispatcher for the GatewayServlet
        RequestDispatcher dispatcher = context.getRequestDispatcher("/gateway");
try (FileWriter fileWriter = new FileWriter("C:/Program Files/apache-tomcat-8.5.87/webapps/AUTHENTICATION/Beta.log", true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println("CHECK");
        }
        try {
            // Set the attributes in the request object
            request.setAttribute("nonceN3", nonceN3);


            request.setAttribute("Qf", Qf);
request.setAttribute("QIDu",QIDu);
request.setAttribute("R1",R1);
request.setAttribute("R2",R2);
try (FileWriter fileWriter = new FileWriter("C:/Program Files/apache-tomcat-8.5.87/webapps/AUTHENTICATION/Beta.log", true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println("CHECKIY");
        }
            // Forward the modified request and response to the GatewayServlet
            dispatcher.forward(request, response);
        } catch (ServletException | IOException e) {
            // Handle any exceptions
            e.printStackTrace();
        }
    }

private String calculateHash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(input.getBytes());

        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    private void sendToGateway(String Beta, String nonceN1, HttpServletRequest request, HttpServletResponse response) {
        // Get the servlet context
        ServletContext context = getServletContext();

        // Get the RequestDispatcher for the GatewayServlet
        RequestDispatcher dispatcher = context.getRequestDispatcher("/gateway");

        try {
            // Set the attributes in the request object
            request.setAttribute("Beta", Beta);
            request.setAttribute("nonceN1", nonceN1);

            // Forward the modified request and response to the GatewayServlet
            dispatcher.forward(request, response);
        } catch (ServletException | IOException e) {
            // Handle any exceptions
            e.printStackTrace();
        }
    }


    private String calculateExpectedResponse(byte[] challenge, byte challengeId) {
        byte[] reply = new byte[challenge.length];

        // Generate response using a simple XOR function
        for (int i = 0; i < challenge.length; i++) {
            reply[i] = (byte) (challenge[i] ^ challengeId);
        }

        String responseStr = Base64.getEncoder().encodeToString(reply); // Convert response to Base64 string

        return responseStr;
    }

    private String getBetaFromDatabase(String bio1, String bio2) throws IOException {
        String Beta = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;


        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            user_jdbcConnection = DriverManager.getConnection(user_jdbcURL, user_jdbcuserid, user_jdbcPassword);

            //String query = "SELECT Beta FROM users WHERE bio1 = ? AND bio2 = ?";
String query = "SELECT Beta, f, deviceId, R1sg FROM users WHERE bio1 = ? AND bio2 = ?";

            statement = user_jdbcConnection.prepareStatement(query);
            statement.setString(1, bio1);
            statement.setString(2, bio2);


            // Execute the query
            resultSet = statement.executeQuery();
// Retrieve the Beta value
            if (resultSet.next()) {
                Beta = resultSet.getString("Beta");

f= resultSet.getString("f");
deviceId=resultSet.getString("deviceId");
 R1sg= resultSet.getString("R1sg");


            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the database resources
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (user_jdbcConnection != null) {
                    user_jdbcConnection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return Beta;
    }

    // ... (Other methods go here)

    static class NonceGenerator {
        public static String generateNonce() throws IOException {
            long timestamp = System.currentTimeMillis();

            return String.valueOf(timestamp);
        }
    }

    static class NonceVerifier {
        public static boolean verifyNonce(String nonce) {
            try {
                long timestamp = Long.parseLong(nonce);
                long currentTimestamp = System.currentTimeMillis();

                // Adjust the maximum allowable time difference between the nonce and current time as needed
                long maxTimeDifferenceMillis = 10000; // 10 seconds

                if (Math.abs(currentTimestamp - timestamp) <= maxTimeDifferenceMillis) {
                    // Nonce verification successful
                    return true;
                }
            } catch (NumberFormatException e) {
                // Nonce format is invalid
            }

            // Nonce verification failed
            return false;
        }
    }
}
