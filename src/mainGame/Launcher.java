package mainGame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Launcher {
	
	private static JFrame frame = new JFrame();
	public static double Version = 0.9;
	
	public static String userEntered = "";
	
	public static void LauncherLoader() {
		frame.setTitle("Project Eclipse" + " " +Version);
		//Image Size 854 , 480
		frame.setSize(858, 484);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		placeComponents(panel);
		
		
		String filename = "Launcher";
		InputStream in = Class.class.getResourceAsStream("/res/launcher/"+filename+".png");
		
		JLabel bgLabel;
		try {
			bgLabel = new JLabel(new ImageIcon(ImageIO.read(in)));
			bgLabel.setBounds(0, 0, 854, 480);
			panel.add(bgLabel);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		frame.setVisible(true);
	}

	private static void placeComponents(JPanel panel){
		panel.setLayout(null);
		
		JLabel userLabel = new JLabel("User");
		userLabel.setBounds(10, 375, 80, 25);
		panel.add(userLabel);
		userLabel.setForeground(Color.WHITE);
		
		JTextField userText = new JTextField(20);
		userText.setBounds(100, 375, 635, 25);
		panel.add(userText);
		userText.setForeground(Color.WHITE);
		userText.setBackground(Color.BLACK);
		userText.setBorder(null);
		userText.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.GRAY));
		
		JLabel passwordLabel = new JLabel("Password");
		passwordLabel.setBounds(10, 410, 80, 25);
		panel.add(passwordLabel);
		passwordLabel.setForeground(Color.WHITE);
		
		JPasswordField passwordText = new JPasswordField(20);
		passwordText.setBounds(100, 410, 635, 25);
		panel.add(passwordText);
		passwordText.setForeground(Color.WHITE);
		passwordText.setBackground(Color.BLACK);
		passwordText.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.GRAY));

		JButton loginButton = new JButton("login");
		loginButton.setBounds(750, 375, 80, 25);
		loginButton.setForeground(Color.WHITE);
		loginButton.setBackground(Color.BLACK);
		loginButton.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.GRAY));
		
		panel.add(loginButton);	
		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		JLabel forceLabel = new JLabel("ID");
		forceLabel.setBounds(10, 340, 80, 25);
		panel.add(forceLabel);
		forceLabel.setForeground(Color.WHITE);
		
		JTextField forceText = new JTextField(20);
		forceText.setBounds(100, 340, 635, 25);
		panel.add(forceText);
		forceText.setForeground(Color.WHITE);
		forceText.setBackground(Color.BLACK);
		forceText.setBorder(null);
		forceText.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.GRAY));
		
		JButton forceButton = new JButton("Force Login");
		forceButton.setBounds(750, 340, 80, 25);
		forceButton.setForeground(Color.WHITE);
		forceButton.setBackground(Color.BLACK);
		forceButton.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.GRAY));
		
		panel.add(forceButton);
		
		forceButton.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e){
				 
				 userEntered = forceText.getText();
	                if(userEntered.equalsIgnoreCase("1")){
	                	System.out.println("Game was forced logged in.");
	                	System.out.println("Account Details Correct");
	                	System.out.println("Welcome to Project Unknown");
	                	System.out.println("User " + userEntered + " Has Logged In To The Game.");
	                	System.out.println("Game Loading...");
	                	frame.dispose();
	                	GameLoop.sub();
	                }else if(userEntered.equals("")){
	                	System.out.println("Sorry Incorrect Data");
	                	JOptionPane.showMessageDialog(frame, "Please Enter a username and password!");
	                }else{
	                	System.out.println("Sorry Incorrect Data");
	                	JOptionPane.showMessageDialog(frame, "Sorry the username or password is incorrect!");
	                }
	            }
	        });    
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		loginButton.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e){
				 
				 userEntered = userText.getText();
				 @SuppressWarnings("deprecation")
				String passEntered = passwordText.getText();
	                if(userEntered.equalsIgnoreCase("Admin")&&passEntered.equals("password")){
	                	System.out.println("Account Details Correct");
	                	System.out.println("Welcome to Project Unknown");
	                	System.out.println("User " + userEntered + " Has Logged In To The Game.");
	                	System.out.println("Game Loading...");
	                	frame.dispose();
	                	GameLoop.sub();
	                }else if(userEntered.equals("Mohammed")&&passEntered.equals("Admin")){
	                	System.out.println("Admin Launcher Loading...");
	                	System.out.println("User " + userEntered + " Has Logged In To The Game.");
	                	frame.dispose();
	                	GameLoop.sub();
	                }else if(userEntered.equals("")&&passEntered.equals("")){
	                	System.out.println("Sorry Incorrect Data");
	                	JOptionPane.showMessageDialog(frame, "Please Enter a username and password!");
	                }else{
	                	System.out.println("Sorry Incorrect Data");
	                	JOptionPane.showMessageDialog(frame, "Sorry the username or password is incorrect!");
	                	passwordText.setText("");
	                }
	            }
	        });    
		
		JButton registerButton = new JButton("register");
		registerButton.setBounds(750, 410, 80, 25);
		registerButton.setForeground(Color.WHITE);
		registerButton.setBackground(Color.BLACK);
		registerButton.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.GRAY));
		panel.add(registerButton);
		
		registerButton.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e){
				 JOptionPane.showMessageDialog(frame, "Please visit ProjectEclipse.com/Register");
				 System.out.println("User atempted to register!");
			 }
		});
}}