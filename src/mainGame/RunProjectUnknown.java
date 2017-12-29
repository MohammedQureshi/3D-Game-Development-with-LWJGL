package mainGame;

public class RunProjectUnknown {
	
	private static void print(String text, String text2){
		System.out.printf("%-30.30s  %-30.30s%n", text, text2);
	}
	private static void printDouble(String text, double number){
		System.out.printf("%-30.30s  %-30.30s%n", text, number);
	}
	
	public static void main(String args[]){
		Launcher.LauncherLoader();
		System.out.println("--------------------------------------------------------------");
		print("Launcher Status:" , "Loaded Sucessfully");
		print("Project Unknown", "2015/18");
		printDouble("Version:" , Launcher.Version);
		print("Creator:" , "Mohammed Faisal Qureshi");
		print("Email:" , "MohammedGamingFTW@Gmail.com");
		print("Phone:" , "0800 000 0000");
		System.out.println("--------------------------------------------------------------");
		System.out.println("");
		
	}
	
}
