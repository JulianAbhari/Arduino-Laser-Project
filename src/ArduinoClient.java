import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.net.UnknownHostException;

public class ArduinoClient {
	// Create scanner object to read user's input
	static Scanner input = new Scanner(System.in);

	@SuppressWarnings("resource")
	public static void main(String[] args) throws UnknownHostException, IOException {
		System.out.println("Send a coordinate to the server.");

		// Create a new socket to connect to the ServerSocket with the correct ip address and port
		Socket socket = new Socket("98.160.112.205", 4848);
		// Create a printwriter to output the user's input to the socket's output stream
		PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
		// Create a bufferedReader to read the inputStream
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

		// Create an infinite loop to read the user's input and output it
		while (true) {
			String readerInput = bufferedReader.readLine();
			output.println(readerInput);
		}
	}
}
