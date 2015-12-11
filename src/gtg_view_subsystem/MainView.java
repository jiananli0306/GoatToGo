package gtg_view_subsystem;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JPanel;

import gtg_control_subsystem.MainController;

/**
 */
public class MainView {
	private Page page;
	private WelcomePage welcomePage = new WelcomePage(this);
	private MapPage mapPage = new MapPage(this);
	private ResultPage resultPage = new ResultPage(this);
	private LoginPage loginPage = new LoginPage(this);
	private JPanel currentPage = new JPanel();
	private AdminMapEditPage adminMapPage; 
	//= new AdminMapEditPage(this);
	private AddDeleteMapPage addDeleteMapPage = new AddDeleteMapPage(this);

	public MainController mainController;
	
	/**
	 * Constructor for MainView.
	 * @param mainController MainController
	 */
	public MainView(MainController mainController){
		this.mainController = mainController;
		page = new Page(this);
		this.showWelcomePage();
	}
	
	public void showMapPage(){
		page.showAdminButton();
		page.hideLogoutButton();
		page.removePage(currentPage);
		page.addPage(mapPage);
		mapPage.reset();
		currentPage = mapPage;
		this.getListOfBuildings();
	}
	
	public void showWelcomePage(){
		page.showAdminButton();
		page.hideLogoutButton();
		page.removePage(currentPage);
		page.addPage(welcomePage);
		currentPage = welcomePage;
	}
	
	public void showAdminLoginPage(){
		page.hideAdminButton();
		page.hideLogoutButton();
		page.removePage(currentPage);
		page.addPage(loginPage);
		loginPage.reset();
		currentPage = loginPage;
	}
	
	public void showAddDeleteMapPage(){
		page.hideAdminButton();
		page.hideLogoutButton();
		page.removePage(currentPage);
		page.addPage(addDeleteMapPage);
		currentPage = addDeleteMapPage;
	}

	public void showResultPage(){
		page.showAdminButton();
		page.hideLogoutButton();
		page.removePage(currentPage);
		page.addPage(resultPage);
		currentPage = resultPage;
	}
	


	/**
	 * Method checkAdminValid.
	 * @param userName String
	 * @param passWord String
	 */
	public void checkAdminValid(String userName, String passWord){
		Boolean userValid = this.mainController.adminQualification(userName, passWord);
		if(userValid == true){
			page.showLogoutButton();
			page.removePage(currentPage);
			page.addPage(addDeleteMapPage);
			currentPage = addDeleteMapPage;
			addDeleteMapPage.showMapList(this.mainController.getMapList("admin"));
		} else {
			loginPage.showInvalidUsernameDialog();
		}
	}
	/**
	 * Method showAdminMapEditPage.
	 * @param mapName String
	 */
	public void showAdminMapEditPage(String mapName) {
		System.out.println("(Mainview switchToAdminMapEditPage)" + mapName);
		page.removePage(currentPage);
		adminMapPage = new AdminMapEditPage(this,mapName);
		adminMapPage.setMapName(mapName);
		page.addPage(adminMapPage);
		currentPage = adminMapPage;
		
	}

	public void showAdddDeleteMapPage() {
		page.showLogoutButton();
		page.removePage(currentPage);
		page.addPage(addDeleteMapPage);
		currentPage = addDeleteMapPage;
		addDeleteMapPage.showMapList(this.mainController.getMapList("admin"));
		
	}

	// Yixiao's change
	/**
	 * Method sentPointToModel.
	 * @param startEndPoint Point
	 * @param selectedPointType String
	 * @param mapName String
	 * @return Point
	 */
	public Point sentPointToModel(Point startEndPoint, String selectedPointType, String mapName) {
		//send the point to controller
		Point pntToBeMapped = startEndPoint;
		System.out.println("Selected Point is" + startEndPoint);
		System.out.println("Selected Point type" + selectedPointType);
		System.out.println("Selected Map " + mapName);
		pntToBeMapped = this.mainController.setTaskPnt(startEndPoint, selectedPointType, mapName);
		return pntToBeMapped;
	}

	public void getPathResult() {
		boolean pathCalculated = mainController.getPathData();
		showResultPage();
		if(pathCalculated == true){
			PathData path = mainController.getDesiredPath(0);
			this.resultPage.displayPath(path, 0);
		} else {
			//this.resultPage.showErrorPopup();
		}
	}

	public void getNextPrevPath(int index){
		PathData path = mainController.getDesiredPath(index);
		this.resultPage.displayPath(path, index);
	}
	
	/**
	 * Method deleteSelectedPoint.
	 * @param selectedPointType String
	 */
	public void deleteSelectedPoint(String selectedPointType) {
		System.out.println("Point is deleted" + selectedPointType);
		this.mapPage.deletePoint(selectedPointType);
		
	}

	/**
	 * Method saveFromAdmin.
	 * @param mapName String
	 */
	public void saveFromAdmin(String mapName) {
		this.mainController.createCoordinateGraph(mapName);
	}

	/*
	 * This method is called from the admin addDeleteMapPage to fetch the map url.
	 */
	/**
	 * Method fetchMapURLAdmin.
	 * @param mapName String
	 */
	public void fetchMapURLAdmin(String mapName) {
		addDeleteMapPage.showMapImage(this.mainController.getMapURL(mapName));
	}

	/*
	 * This method is called from the admin addDeleteMapPage to add new map data into the .txt file.
	 */
	/**
	 * Method sendAddMapData.
	 * @param mapName String
	 * @param mapImageURL String
	 * @param mapType String
	 */
	public void sendAddMapData(String mapName, String mapImageURL, String mapType) {
		boolean result = this.mainController.addNewMap(mapName, mapImageURL, mapType);
		if(result == true){
			addDeleteMapPage.showMapList(this.mainController.getMapList("admin"));
		} else {
			addDeleteMapPage.showAddMapError();
		}
	}


	/*
	 * This method is called from the admin addDeleteMapPage to delete the map from the .txt file.
	 */
	/**
	 * Method deleteMap.
	 * @param mapName String
	 */
	public void deleteMap(String mapName) {
		System.out.println("(Mainview deleteMap)" + mapName);
		boolean result = this.mainController.deleteMap(mapName);
		if(result == true){
			addDeleteMapPage.showMapList(this.mainController.getMapList("admin"));
		} else {
			addDeleteMapPage.showDeleteMapError();
		}
	}
	
	public String getMouseSelectedBuilding(Point mouseClickedPnt){
		String correspondMapName = null;
		correspondMapName = this.mainController.getMouseSelectedBuilding(mouseClickedPnt);
		return correspondMapName;
	}
	
	/**
	 * Method getListOfFloors.
	 * @param mapName String
	 * This methods fetches the list of floors depending upon the building name.
	 * Called from the MapPage
	 */
	public void getListOfFloors(String mapName){
		ArrayList<String> mapList = this.mainController.getMapList(mapName);
		// 2015-11-27 Yixiao
		if(!mapList.isEmpty()){
			mapPage.displayDropDownList(mapList);
			this.getMapURL(mapList.get(0));
		}
		// Please create a Inform Box here to inform the user that the map is not exist yet
	}
	
	/**
	 * Method getMapURL.
	 * @param String mapName
	 * This methods fetches the map url for the mapName value.
	 * Called from the MapPage
	 */
	public void getMapURL(String mapName){
		boolean tempBoolean = this.mainController.LoadingPntsAndEdges(mapName);
		String mapURL = this.mainController.getMapURL(mapName);
		
		ArrayList<Point> graphPoints = new ArrayList<Point>();
		if(tempBoolean == true){
			graphPoints = this.mainController.getDisplayPnt();
		}
		mapPage.changeMapImage(mapURL);
		mapPage.addGraphPoints(graphPoints);
	}

	/**
	 * Method getListOfBuildings.
	 * @param none
	 * This methods fetches the list of building for a campus map.
	 * Called from the MapPage
	 */
	public void getListOfBuildings() {
		ArrayList<String> mapList = this.mainController.getMapList("CampusMap");
		mapPage.displayDropDownList(mapList);
		
		this.getMapURL(mapList.get(0));
	}
	
	public String tempMapURL(String mapName){
		String mapURL = this.mainController.getMapURL(mapName);
		return mapURL;
	}
	
	// Yixiao 2015-12-10
	public String getPointDescription(Point pnt){
		return this.mainController.getPointDescription(pnt);
	}
	public String getStartEndNodeDescription(String type){
		return this.mainController.getStartEndNodeDescription(type);
	}

	/**
	 * Method getFilteredList.
	 * @param pointType String
	 * This methods fetches the list of points depending on the filter value.
	 * Called from the MapPage
	 */
	public void getFilteredList(String pointType) {
		ArrayList<Point> filteredPoints = this.mainController.getFilteredList(pointType);
		this.mapPage.populateFilteredList(filteredPoints, pointType);
	}
}
