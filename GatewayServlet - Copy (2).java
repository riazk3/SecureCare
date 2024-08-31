import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;

public class GatewayServlet extends HttpServlet {
    private String jdbcURL = "jdbc:mysql://localhost:3306/mydb";
    private String jdbcuserid = "root";
    private String jdbcPassword = "Cartoon";
    private Connection jdbcConnection;
    private String userID;
    private String challenge;
    private String f;
    private String z;
    private String R1sg;
    private String deviceId;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Step 2: Receive (β, N1) from the user device
        String Beta = (String) request.getAttribute("Beta");
        String nonceN1 = (String) request.getAttribute("nonceN1");

        // Write the value of n1 to a log file
        
        // Step 3: Verify β and nonce N1
        boolean BetaExistsInDatabase = checkBetaInDatabase(Beta, response);
        boolean isNonceValid = NonceVerifier.verifyNonce(nonceN1);

        if (BetaExistsInDatabase && isNonceValid) {
            // β and N1 are valid, continue processing
            String nonceN2 = NonceGenerator.generateNonce();
            String challengeCU = generateChallenge();
            String responseRU = generateResponse(challengeCU);

            // Step 4: Transmit (N2, CU) to the user device
            sendToUserDevice(nonceN2, challengeCU, request, response);

 
        String nonceN3 = (String) request.getAttribute("nonceN3");
boolean isNonceValid = NonceVerifier.verifyNonce(nonceN3);


            
        } else {
            // β or N1 is invalid, handle the error
            response.sendRedirect("error.jsp");
        }
    }

    public static class NonceVerifier {
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

    public boolean checkBetaInDatabase(String Beta, HttpServletResponse response)
            throws ServletException, IOException {
        boolean BetaExists = false;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            jdbcConnection = DriverManager.getConnection(jdbcURL, jdbcuserid, jdbcPassword);

            // Prepare the SQL statement
            String sql = "SELECT UserID, challenge, f, z, R1sg, deviceId FROM users WHERE Beta = ?";
            statement = jdbcConnection.prepareStatement(sql);
            statement.setString(1, Beta);

            // Execute the query
            resultSet = statement.executeQuery();

            // Check if the result set has any rows
            if (resultSet.next()) {
                // The database has the specified Beta value
                BetaExists = true;

                // Retrieve the additional values from the result set and assign them to class variables
                userID = resultSet.getString("UserID");
                challenge = resultSet.getString("challenge");
                f = resultSet.getString("f");
                z = resultSet.getString("z");
                R1sg = resultSet.getString("R1sg");
                deviceId = resultSet.getString("deviceId");
            } else {
                response.sendRedirect("error.jsp");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the resources
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (jdbcConnection != null) {
                    jdbcConnection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return BetaExists;
    }

    private String generateChallenge()    throws ServletException, IOException {
        byte[] challengeBytes = new byte[16];
        new Random().nextBytes(challengeBytes);

        return Base64.getEncoder().encodeToString(challengeBytes);
    }

    private String generateResponse(String challenge)    throws ServletException, IOException {
        byte[] challengeBytes = Base64.getDecoder().decode(challenge);
        byte challengeId = 0x10; // Replace with your desired challenge ID

        return calculateExpectedResponse(challengeBytes, challengeId);
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

    private void sendToUserDevice(String nonceN2, String challenge, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
         ServletContext context = getServletContext();

        // Get the RequestDispatcher for the GatewayServlet
        RequestDispatcher dispatcher = context.getRequestDispatcher("/user");
try{
        // Set the attributes in the request object
        request.setAttribute("nonceN2", nonceN2);
        request.setAttribute("challenge", challenge);


        // Forward the modified request and response to the UserDeviceServlet
        dispatcher.forward(request, response);
} catch (ServletException | IOException e) {
            // Handle any exceptions
            e.printStackTrace();
        }
    }
}

class NonceGenerator {
    public static String generateNonce() throws IOException {
        long timestamp = System.currentTimeMillis();
       
        return String.valueOf(timestamp);
    }
}
