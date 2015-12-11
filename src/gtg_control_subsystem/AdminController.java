package gtg_control_subsystem;


public class AdminController {
	
	private MainController mainController;
	
	public AdminController (MainController controlInterface) {
		mainController = controlInterface;
	}

	public Boolean adminQualification(String userName, String passWord){
		Boolean isAdmin = false;
		isAdmin = mainController.mapModel.isValidAdmin(userName, passWord);
		if(!isAdmin){
			System.out.println("Sorry You are not Admin!");
			mainController.mapModel.printAdmins();
		}
		return isAdmin;
	}
	
}
