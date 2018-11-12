import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.fazecast.jSerialComm.SerialPort;

public class ArduinoReciever {
	public static final int PORT = 4848;
	static SerialPort chosenPort;

	public static void main(String[] args) throws IOException {
		// Create and configure the window
		JFrame window = new JFrame();
		window.setTitle("Arduino Laser");
		window.setSize(400, 75);
		window.setLayout(new BorderLayout());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create a drop-down box and connect button, then place them at the top of the
		// window
		JComboBox<String> portList = new JComboBox<String>();
		JButton connectButton = new JButton("Connect");
		JPanel topPanel = new JPanel();
		topPanel.add(portList);
		topPanel.add(connectButton);
		window.add(topPanel, BorderLayout.CENTER);

		// Populate the drop-down box with user's SerialPorts
		SerialPort[] portNames = SerialPort.getCommPorts();
		for (int i = 0; i < portNames.length; i++)
			portList.addItem(portNames[i].getSystemPortName());

		// Configure connectButton and set chosenPort
		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// If the connectButton isn't connected to a serialPort
				if (connectButton.getText().equals("Connect")) {
					// Attempt to connect to the serial port
					chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
					chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
					// If successfully connected to port, configure connectButton to say Disconnect
					if (chosenPort.openPort()) {
						connectButton.setText("Disconnect");
						portList.setEnabled(false);
					}
					// If the connectButton is clicked and the text says Disconnect
				} else {
					// Disconnect from the serialPort
					chosenPort.closePort();
					portList.setEnabled(true);
					connectButton.setText("Connect");
				}
			}
		});

		// Show the window
		window.setVisible(true);
		// Run the server
		runServer();
	}

	@SuppressWarnings("resource")
	public static void runServer() throws IOException {
		// Create a new serverSocket on the PORT
		ServerSocket serverSocket = new ServerSocket(PORT);
		System.out.println("Server up & ready for connections...");

		// Accept all incoming data and start a new ServerThread
		while (true) {
			Socket socket = serverSocket.accept();
			new ServerThread(socket).start();
		}
	}
}

class ServerThread extends Thread {
	Socket socket;

	// Set the serverSocket to the local socket
	ServerThread(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		// Attempt to read InputStream
		try {
			String message;
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// Create a printWriter to output the recieved message to the chosenSerialPort
			PrintWriter output = new PrintWriter(ArduinoReciever.chosenPort.getOutputStream());

			// Set the message to the next line in the inputStream. If it is not null
			while ((message = bufferedReader.readLine()) != null) {
				// Print the message then output it to the serialPort
				System.out.println(message);
				output.print(message);
				output.flush();
			}
			// If the message in the inputStream is null, close the socket
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
