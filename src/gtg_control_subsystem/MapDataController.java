package gtg_control_subsystem;

import java.util.List;
import java.awt.geom.Point2D;
import java.awt.Point;

import java.util.ArrayList;
import java.util.Hashtable;

import gtg_model_subsystem.Node;
import gtg_model_subsystem.Edge;
import gtg_model_subsystem.Map;

public class MapDataController {
	
	private MainController mainController;
		
	private ArrayList<String> listOfMapName = new ArrayList<String>();
	private ArrayList<String> listOfMapURL = new ArrayList<String>();
	
	private ArrayList<String> listOfMapNameForReturn = new ArrayList<String>();
	private ArrayList<String> listOfMapURLForReturn = new ArrayList<String>();
	
	private ArrayList<Node> nodeList = new ArrayList<Node>();
	private ArrayList<Edge> edgeList = new ArrayList<Edge>();
	private ArrayList<Point> tempPntList  = new ArrayList<Point>();
	private ArrayList<Point> tempEdgeList = new ArrayList<Point>();
	
	// Constructor
	public MapDataController(MainController controlInterface){
		mainController = controlInterface;
		LoadInMapNameList();	
		LoadInMapURL();

	}
	
	/* *******************************
	 * 
	 * 			Map Manipulation
	 * @Yixiao Added by Neha
	 * I made change in the case "BoyntonHall" added the campus map name as the last value in the arraylist.
	 * Also remember that while creating the list of building add the campus map name always in the first position.
	 * This is required to for view to differentiate between the dropdown list manupulation.
	 * ********************************/
	
	private boolean LoadInMapNameList(){
		boolean success=false;
		listOfMapName.clear();
		ArrayList<String> tempList = mainController.mapModel.getArrayOfMapNames();
		if(!tempList.isEmpty()){
			for (String mapName: tempList){
				if(listOfMapName.indexOf(mapName)<0){
					listOfMapName.add(mapName);
				}
			}
			success=true;
			return success;
		}
		System.out.println("Map list from model is empty!");
		return success;
	}
	
	private boolean LoadInMapURL(){
		boolean success=false;
		listOfMapURL.clear();
		ArrayList<String> tempList=mainController.mapModel.getImgURLS();
		if(!tempList.isEmpty()){
			for (String mapURL: tempList){
				if(listOfMapURL.indexOf(mapURL)<0){
					listOfMapURL.add(changeSeparator(mapURL));
				}
			}
			success=true;
			return success;
		}
		System.out.println("Map URL list from model is empty!");
		return success;
	}
	
	public ArrayList<String> getCurrentMapNameList(){
		return listOfMapName;
	}
	public ArrayList<String> getCurrentMapURLList(){
		return listOfMapURL;
	}
	
	private String changeSeparator(String url){
		String newUrl=new String();
		String osName=System.getProperty("os.name");
		if (osName.contains("Mac")||osName.contains("Linux")){
			newUrl=url.replace("\\", System.getProperty("file.separator"));
		} else {
			newUrl=url;
		}
		
		return newUrl;
	}
	
	public String changeBackSeparator(String url){
		String newUrl=new String();
		String osName=System.getProperty("os.name");
		if (osName.contains("Mac")||osName.contains("Linux")){
			newUrl=url.replace(System.getProperty("file.separator"), "\\");
		} else {
			newUrl=url;
		}
		return newUrl;
	}
	
	public boolean mapIsInTheOldList(String mapName){
		boolean mapInTheList = false;
		if(listOfMapName.indexOf(mapName)>0){
			mapInTheList = true;
		}
		return mapInTheList;
	}
	
	private void updateMapList(String mapRequestCommand){

		
		switch(mapRequestCommand){
		case "admin":
			listOfMapNameForReturn.clear();
			listOfMapURLForReturn.clear();
			mainController.mapModel.loadMapLists();
			LoadInMapNameList();
			LoadInMapURL();
			getAllMapNameAndURL();
			break;
		case "CampusMap":
			listOfMapNameForReturn.clear();
			listOfMapURLForReturn.clear();
			updateMapListWithDesiredMapName(mapRequestCommand);
			addAllBuildingIntoList();
			break;		
		// Get All the buildingMap
		default:
			if(validateBuildingName(mapRequestCommand)){
				listOfMapNameForReturn.clear();
				listOfMapURLForReturn.clear();
				updateMapListWithDesiredMapName(mapRequestCommand);
				updateMapListWithDesiredMapName("CampusMap");
			}
			break;
		}
	}
	
	private void getAllMapNameAndURL(){
		for(String mapName:listOfMapName){
			listOfMapNameForReturn.add(mapName);
		}
		for(String mapURL:listOfMapURL){
			listOfMapURLForReturn.add(mapURL);
		}
	}
	
	// Waiting model create a method return me the mapList
	private void addAllBuildingIntoList(){
		for(String mapName:listOfMapName){
			String buildingName ="";
			int end = mapName.lastIndexOf("_");
			buildingName=mapName.substring(0, end);
			// Check if we have add the building into the list
			if(!buildingName.equals("CampusMap") && listOfMapNameForReturn.indexOf(buildingName)<0){
				listOfMapNameForReturn.add(buildingName);
				listOfMapURLForReturn.add("");
			}
		}
	}
	
	private void updateMapListWithDesiredMapName(String desiredMap){
		for(String mapName:listOfMapName){
			String Section_1 ="";
			int end = mapName.lastIndexOf("_");
			Section_1=mapName.substring(0, end);
			if(desiredMap.equals(Section_1)){
				listOfMapNameForReturn.add(mapName);
				int mapIndex = listOfMapName.indexOf(mapName);
				String mapURL = listOfMapURL.get(mapIndex);
				listOfMapURLForReturn.add(mapURL);
			}
		}
	}
	
	private boolean validateBuildingName(String buildingName){
		boolean buildingMapExist = false;
		for(String mapName:listOfMapName){
			String Section_1 ="";
			int end = mapName.lastIndexOf("_");
			Section_1=mapName.substring(0, end);
			if(buildingName.equals(Section_1)){
				buildingMapExist = true;
				return buildingMapExist;
			}
		}
		return buildingMapExist;		
	}

	public void addNewMapToList(String mapName){
		if(listOfMapName.indexOf(mapName)<0){
			listOfMapName.add(mapName);
		}
	}
	
	public void addNewMapURLToList(String mapURL){
		if(listOfMapURL.indexOf(mapURL)<0){
			listOfMapURL.add(mapURL);
		}
	}

	// Remove the map from MapList and MapURLList at the same time;
	public Boolean removeMapFromList(String mapName){
		Boolean mapRemoved = false;
		int index = listOfMapName.indexOf(mapName);
		if(index>0){
			listOfMapName.remove(index);
			listOfMapURL.remove(index);
			mapRemoved = true;
		}
		else{
			System.out.print("Can't find map in the MapList");
		}
		return mapRemoved;
	}
	
	/* For integration the method return me the hardcoded arraylist.
	 * Once a method from model is available the method should return me an arrayList of mapnames.
	 * The input parameter mapName will help us to use the same method in the map page
	 * i.e if mapName is admin means the list should conatins all the map names
	 * if mapName is campus then the list will contain all building names
	 * if mapName is building then the list will contain all the floor names of the building and the campus map also.
	 * You will have to implement the switch case.
	 */
	public ArrayList<String> getMapList(String mapName){
		updateMapList(mapName);
		return listOfMapNameForReturn;
	}
	
	/* Changed > to >= as index starts from 0 value*/
	public String getMapURL(String mapName){
		String mapurl = "";
		int index = listOfMapNameForReturn.indexOf(mapName);
		if(index >= 0){
			mapurl = listOfMapURLForReturn.get(index);
		}
		return mapurl;
	}
	
	// 2015-12-08
	public String getClickedBuildingMapName(Point inputPnt){
		String buildingMapName = null;
		Node mappingResult = searchingAPointInNodeList(inputPnt);
		if(mappingResult!=null){
			buildingMapName = mappingResult.getBuilding();
			// Be sure, this is a building instead of Campus;
			System.out.println(mappingResult.getDescription());
			if(!buildingMapName.equals("CampusMap")){
				return buildingMapName;
			}
		}
		return buildingMapName;
	}
	
	/* *******************************
	 * 
	 * 			Nodes and Edges
	 * 
	 * ********************************/
	public Boolean LoadingPntsAndEdges(String mapName){
		// Clear the temporary node/edge List before add new point into it;
		this.nodeList.clear();
		this.edgeList.clear();
		this.tempPntList.clear();
		this.tempEdgeList.clear();		
		if(mainController.mapModel.loadFiles(mapName)){
			LoadInNodeList(mapName);
			LoadInEdgeList(mapName);	 
			transferNodeToPnt2D(this.nodeList);
			transferEdgeToPnt2D(this.edgeList);

			return true;
		}
		else{
			System.out.println("Loading File failed");
			return false;
		}
	}
	
	private void LoadInNodeList(String mapName){
		// Clear the temporary nodeList before add new point into it;
		ArrayList<Node> tempNode = (ArrayList<Node>)mainController.mapModel.getNodeList(mapName);		
		for(Node nd:tempNode){
			this.nodeList.add(nd);
		}
		//return copySuccess;		
	}
	
	private void LoadInEdgeList(String mapName){
		ArrayList<Edge> tempEdge = (ArrayList<Edge>)mainController.mapModel.getEdgeList(mapName);		
		for(Edge eg:tempEdge){
			this.edgeList.add(eg);
		}
	}

	private void transferNodeToPnt2D(List<Node> targetList){
		tempPntList.clear();
		for(Node nd:targetList){
			//Point2D pnt = new Point2D.Double(nd.getX(),nd.getY());
			Point pnt = new Point(nd.getX(),nd.getY());
			tempPntList.add(pnt);			
		}
	}
	
	private void transferEdgeToPnt2D(List<Edge> targetList){
		tempEdgeList.clear();
		for(Edge eg:targetList){
			//Point2D pnt_1 = new Point2D.Double(eg.getSource().getX(),eg.getSource().getY());
			//Point2D pnt_2 = new Point2D.Double(eg.getDestination().getX(),eg.getDestination().getY());
			
			Point pnt_1 = new Point(eg.getSource().getX(),eg.getSource().getY());
			Point pnt_2 = new Point(eg.getDestination().getX(),eg.getDestination().getY());
			tempEdgeList.add(pnt_1);
			tempEdgeList.add(pnt_2);
		}
		
	}

	private String newStr (String str) {
		String newStr = str;
		if (newStr.trim().isEmpty()) {
			newStr = "NULL";
		}
		return newStr;
	}

	public Boolean addPoint(Point inputPnt, int floorNum, int entranceID, String buildingName, String pointType, String pointDescription){
		Boolean success = false;
		int i=CheckPntExistence(inputPnt);
		if(i<0){
			
			int coord_X = (int)inputPnt.getX();
			int coord_Y = (int)inputPnt.getY();						
			for(Node nd:nodeList){
				if(Math.abs(nd.getX() - coord_X)<5){
					coord_X = nd.getX();					
				}
				if(Math.abs(nd.getY() - coord_Y)<5){
					coord_Y = nd.getY();					
				}
			}
			
/*			System.out.println(pointDescription);
			pointDescription = pointDescription.replace("\\s+", ";");
			System.out.println(pointDescription);*/
			
			nodeList.add(new Node(this.getMaxNodeID()+1, coord_X, coord_Y, entranceID, buildingName, floorNum, pointType, newStr(pointDescription)));
			transferNodeToPnt2D(nodeList);
			success = true;
			
		}
		else{
			Node n=findNodeInList(i);
			n.setBuilding(buildingName);
			n.setFloorNum(floorNum);
			n.setEntranceID(entranceID);
			n.setType(pointType);
			n.setDescription(newStr(pointDescription));
			success= true;
		}
		return success;
	}
	
	public boolean editExistNode(int nodeID, int entranceID, String pointType, String pointDescription){
		boolean success = false;
		Node nd = findNodeInList(nodeID);
		if(nd!=null){
			nd.setEntranceID(entranceID);
			nd.setType(pointType);
			nd.setDescription(newStr(pointDescription));
			success = true;
		}
		return success;
	}
	
	public Boolean createEdge(Point pnt1, Point pnt2){
		Boolean success = false;
		// Check Edge Redundancy
		int PointID_1 = CheckPntExistence(pnt1);
		int PointID_2 = CheckPntExistence(pnt2);
		// Check if edge already exist in the edge list
		if (PointID_1>0&&PointID_2>0&&PointID_1!=PointID_2) {
			for (Edge e:edgeList){
				if (((e.getSource().getID()==PointID_1)&&(e.getDestination().getID()==PointID_2))||((e.getSource().getID()==PointID_2)&&(e.getDestination().getID()==PointID_1))) {
					System.out.println("Edge exists!");
					return success;
				}
			}
			Node start=findNodeInList(PointID_1);
			Node end=findNodeInList(PointID_2);
			if ((start==null)||(end==null)) {
				return success;
			}
			
			edgeList.add(new Edge(this.getMaxEdgeID()+1, start, end ,Math.sqrt(Math.pow(start.getX()-end.getX(), 2)+Math.pow(start.getY()-end.getY(), 2))));
			transferEdgeToPnt2D(edgeList);
			
			success=true;
			return success;
		} 
		
		return success;
	}
	
	public boolean deletePoint(Point inputPnt){
		Boolean pointDeleted = false;
		Node nodeFound=null;
		if(nodeList.isEmpty()){
			System.out.println("Node List is empty, nothing to delete.");
			return pointDeleted;
		}
		int pntID = this.CheckPntExistence(inputPnt);
		if(pntID == 0){
			System.out.println("Can't find point in the List, Will not delete any points.");
			return pointDeleted;
		}
		nodeFound=findNodeInList(pntID);
		for (int edgeSeq=edgeList.size()-1; edgeSeq>=0; edgeSeq--){
			if ((edgeList.get(edgeSeq).getSource().getID()==pntID)||(edgeList.get(edgeSeq).getDestination().getID()==pntID)){
				edgeList.remove(edgeSeq);
				}
		}
		nodeList.remove(nodeFound);
		transferNodeToPnt2D(nodeList);
		transferEdgeToPnt2D(edgeList);
		pointDeleted=true;
		return pointDeleted;
	}
	
	public boolean deleteEdge(Point2D p){
		int edgeID = checkIfPointIsInEdge(p);
		if(edgeID>0){			
			System.out.println("Edge "+ edgeID+ " will be deleted");
			for (int edgeSeq=edgeList.size()-1; edgeSeq>=0; edgeSeq--){
				if(edgeList.get(edgeSeq).getEdgeID() == edgeID){
					edgeList.remove(edgeSeq);	
					System.out.println("An edge is deleted!");
					transferEdgeToPnt2D(edgeList);
					return true;		
				}
			}
		}
		return false;
	}
	
	public Point pointMapping(Point inputPnt){		
		Point searchingResult = new Point(0,0);
		
		for (Point temPnt : tempPntList){
			double d = Math.sqrt(Math.pow(inputPnt.getX()-temPnt.getX(), 2)+ Math.pow(inputPnt.getY() - temPnt.getY(), 2));
			if(d <= 10){
				System.out.println("Mapping To Point" + temPnt.getX() + "," + temPnt.getY());
				searchingResult = temPnt;
				return searchingResult;
			}
		}		
		System.out.println("Invalid Input!!");
		return searchingResult;
	}
	
	public String getNodeDescription(Point pnt){
		String description = "";
		Node nd = findNodeInList(pnt);
		if(nd!=null){
			System.out.println(description);
			description = nd.getDescription();
		}
		return description;
	}
	
	private Node findNodeInList (int nodeID) {
		for (Node n:nodeList) {
			if (n.getID()==nodeID) {				
				return n;
			}
		}
		return null;
	}
	
	private Node findNodeInList(Point Inputpnt){
		for(Node nd:nodeList){
			if(nd.getX() == Inputpnt.x&&nd.getY() == Inputpnt.y){
				return nd;
			}				
		}
		return null;
	}
	
	private Node searchingAPointInNodeList(Point inputPnt){
		Node result = null;
		int threshold = 20;
		for(Node nd:nodeList){
			if(getPointDistance((double)nd.getX(),(double)nd.getY(),inputPnt.getX(),inputPnt.getY())<threshold){
				result = nd;
				return result;
			}
		}		
		return result;		
	}
	
	private double getPointDistance(double x1,double y1,double x2, double y2){
		return Math.sqrt(Math.pow(x1-x2, 2)+ Math.pow(y1 - y2, 2));		
	}
	
	public int CheckPntExistence(Point pnt){
		int pntID = -1;
		int toleranceRadius = 20;	// 15 pixels
		for (Node tempN: nodeList){
			double d = Math.sqrt(Math.pow(pnt.getX() - tempN.getX(), 2) + 
							     Math.pow(pnt.getY() - tempN.getY(), 2));
			//System.out.println("the distance is : "+d);
			if(d <= toleranceRadius){
				pntID = tempN.getID();
				System.out.println("Point "+pntID+" is Found in the nodeList!");
				return pntID;
			}
		}
		return pntID;
	}	

	//See if selected point is a part of an existing edge
	private int checkIfPointIsInEdge(Point2D p){
		double ab, ap, pb;
		int r = 0;
		for(Edge e: edgeList){
			
			ab=e.getEdgeLength();			
			ap=Math.sqrt(Math.pow(e.getSource().getX()-p.getX(), 2)+Math.pow(e.getSource().getY()-p.getY(), 2));
			pb=Math.sqrt(Math.pow(e.getDestination().getX()-p.getX(), 2)+Math.pow(e.getDestination().getY()-p.getY(), 2));
			
			if(Math.abs(ab-(ap+pb))<=5){
				r=e.getEdgeID();				
				System.out.println("Point " + p + " belongs to Edge "+r);
				return r;
			}
		}
		System.out.println("This Point doesn't belongs to any Edge");
		return r;
	}

	public ArrayList<Point> getDisplayPnt(){		
		return tempPntList;
	}
	
	public ArrayList<Point> getDisplayEdge(){
		return tempEdgeList;
	}
	
	public ArrayList<Node> getNodeList(){
		return nodeList;
	}
	
	public ArrayList<Edge> getEdgeList(){
		return edgeList;
	}	
	
	public ArrayList<Point> getFilteredList(String pointType){
		ArrayList<Point> filteredList = new ArrayList<Point>();
		for (Node n:nodeList){
			if (n.getType().equals(pointType)){
				filteredList.add(new Point(n.getX(),n.getY()));
			}
		}
		return filteredList;
	}
	
	private int getMaxNodeID(){
		int maxNodeID=0;
		for (Node n:nodeList) {
			if (n.getID()>maxNodeID){
				maxNodeID=n.getID();
			}
		}
		return maxNodeID;
	}
	
	private int getMaxEdgeID() {
		int maxEdgeID=0;
		for (Edge e:edgeList) {
			if (e.getEdgeID()>maxEdgeID){
				maxEdgeID=e.getEdgeID();
			}
		}
		return maxEdgeID;
	}	

	public Point getLastPntInPntList()
	{
		Point pnt = new Point(0,0);
		if(tempPntList.size()!=0){
			pnt = tempPntList.get(tempPntList.size()-1);
			System.out.println("The last Point is: "+pnt.getX()+ "" + pnt.getY());
			return pnt;
		}	
		System.out.println("There is no Pnt in the list!");
		return pnt;		
	}
	
	// Get Node properties
	public int getNodeID(Point inputPnt){
		int NodeID = -1;
		if(nodeList.isEmpty()){
			System.out.println("NodeList is empty, can't find point in NodeList");
			return NodeID;
		}
		else{
			for(Node nd:nodeList){
				if(inputPnt.getX() == nd.getX()&&
				   inputPnt.getY() == nd.getY()){
					NodeID = nd.getID();
					return NodeID;
				}
			}
			
			System.out.println("Can't find point in NodeList");
		}		
		return NodeID;
	}
	
	public String getDescriptionOfNode (int nodeID){
		String description=new String();
		if (this.findNodeInList(nodeID)!=null){
			description=this.findNodeInList(nodeID).getDescription();
			//waiting for extension of node class;
			return description;
		}
		return description;
	}
	
	public String getBuildingNameofNode(int nodeID){
		String buildingName = new String();
		if (this.findNodeInList(nodeID)!=null){
			buildingName = this.findNodeInList(nodeID).getBuilding();
			//waiting for extension of node class;
			}		
		return buildingName;
	}	
	
	public int getFloorNumofNode(int nodeID){
		int floorNum = 0;
		if (this.findNodeInList(nodeID)!=null){
			floorNum = this.findNodeInList(nodeID).getFloorNum();
			//waiting for extension of node class;
			}		
		return floorNum;
	}
	
	public int getEntranceIDOfNode (int nodeID){
		int entranceID=-1;
		if (this.findNodeInList(nodeID)!=null){
			entranceID=this.findNodeInList(nodeID).getEntranceID();
			//waiting for extension of node class;
			return entranceID;
		}
		return entranceID;
	}
	
	public String getTypeOfNode (int nodeID){
		String nodeType=new String();
		if (this.findNodeInList(nodeID)!=null){
			nodeType=this.findNodeInList(nodeID).getType();
			//waiting for extension of node class;
			return nodeType;
		}
		return nodeType;
	}
	
	// Set nodes properties
	public boolean setDescriptionOfNode (int nodeID){
		boolean success=false;
		if (this.findNodeInList(nodeID)!=null){
			//this.findNodeInList(nodeID).setDescription();
			//waiting for extension of node class;
			success=true;
			return success;
		}
		return success;
	}
	
	public boolean setEntranceIDOfNode (int nodeID){
		boolean success=false;
		if (this.findNodeInList(nodeID)!=null){
			//this.findNodeInList(nodeID).setEntranceID();
			//waiting for extension of node class;
			success=true;
			return success;
		}
		return success;
	}
	
	public boolean setTypeOfNode (int nodeID){
		boolean success=false;
		if (this.findNodeInList(nodeID)!=null){
			//this.findNodeInList(nodeID).setType();
			//waiting for extension of node class;
			success=true;
			return success;
		}
		return success;
	}
	
	// Clear Data
	public void clearAllTempData(){
		tempPntList.clear();
		tempEdgeList.clear();
		nodeList.clear();
		edgeList.clear();
	}
}
