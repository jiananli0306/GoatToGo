package gtg_model_subsystem;

import java.awt.Point;
import java.awt.geom.Point2D;


import java.util.List;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.LinkedHashMap;
/**
 */
public class MainModel {
	private List<Node> nodes;
	private List<Edge> edges;
	private Path shortestPath = null;
	private CoordinateGraph graph;
	private List<Admin> admins;
	private FileProcessing fileProcessing;
	private Hashtable<String, Map> mapTable;
	private LinkedHashMap<String, Path> mapPaths;
	private HashMap<Integer,Node>campusMapNodeList;
	private Path tempPath;
	private Node startNode;
	private Node endNode;
	public MainModel(){
		admins = new ArrayList<Admin>();
		fileProcessing = new FileProcessing();
		mapTable = new Hashtable<String, Map>();
		campusMapNodeList = new HashMap<Integer,Node>();
		try {			
			loadMapLists();
			loadAdmin();			
			loadFiles();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
    
	/**
	 * Load maps from master map text file and store into table of maps
	
	 * @return true if load and store the map table successfully
	 * 		   false if maps does not exist */
	public boolean loadMapLists()
	{
		ArrayList<Map> masterMapList=null;
		try {
			//load maps from master text file
			masterMapList=fileProcessing.loadMapList();		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//IF the master map list failed to load THEN
		if(masterMapList==null){
			return false;
		} else{
			//access each map and store it into map table
			for(Map map:masterMapList){
				System.out.println(map.getMapName());
				mapTable.put(map.getMapName(), map);
			}
			
			return true;
		}
	}
	/**
	 * deleteMap will delete map from the map table and the master list text file
	 * @param mapName the name of the map the admin wishes to delete
	
	
	 * @return true if deletion was successful false otherwise * @throws IOException  */
	public boolean deleteMap(String mapName) throws IOException{
			boolean deleteSuccess = true;
			if(mapTable.get(mapName) == null){
				System.out.println("Map does not exist");
				deleteSuccess = false;
			}
			//IF map exists in map table THEN
			if(mapTable.get(mapName) != null){
				//will this delete from memory??
				//REMOVE map from map table
				mapTable.remove(mapName);
				//REMOVE map from master map list so it will not be re-loaded
				fileProcessing.deleteMapFromMaster(mapName);
				System.out.println("Delete Success");
			}
			return deleteSuccess;
	}
	
	/**
	 * Method to save newly created maps added by admin from admin view to the list of maps.
	 * @param mapName the name of new map to be saved
	 * @param mapType the type of map that we are saving
	 * @param mapImgURL String
	 * @return true if map was successfully created false otherwise * @throws IOException  */
	public boolean saveNewMap(String mapName, String mapImgURL, String mapType) throws IOException{
				boolean saveNewMap = true;
				//IF map does not exist in map table THEN
				if (mapTable.get(mapName) != null){
					System.out.println("Map Already exists");
					saveNewMap = false;
				}
				//STORE map in masterMapList 
				else{
					fileProcessing.saveMapToMaster(mapName, mapImgURL, mapType);
				}
				return saveNewMap;
	}
	
	public boolean loadFiles(){
		boolean mapCreated = false;
		try{
			for(String mapName: mapTable.keySet()){
				mapCreated = createMapGraph(mapName);
			}
		}catch(IOException e){
			System.out.println(e.toString());
		}		
		return mapCreated;		
	}
	
	/**
	 * Method loadFiles.
	 * @param mapName String
	 * @return boolean
	 */
	public boolean loadFiles(String mapName){
		boolean mapCreated = false;
		try{
			mapCreated = createMapGraph(mapName);
		}catch(IOException e){
			System.out.println(e.toString());
		}		
		return mapCreated;		
	}
	/**
	 * Method loadAdmin.
	 * @throws IOException
	 */
	public void loadAdmin() throws IOException{
		fileProcessing.readAdmin(admins);
	}
	
	/**
	 * Method createMapGraph.
	 * @param mapName String
	 * @return boolean
	 * @throws IOException
	 */
	public boolean createMapGraph(String mapName) throws IOException{
		
		nodes = new ArrayList<Node>();
		edges = new ArrayList<Edge>();
		boolean createMapGraphSuccess = true;
		if(!mapTable.containsKey(mapName)){
			createMapGraphSuccess = false;
			System.out.println("Map does not exist in table");
			return createMapGraphSuccess;
		}
		fileProcessing.readGraphInformation(nodes, edges, mapName);
		
		
		graph = new CoordinateGraph(nodes, edges);
		mapTable.get(mapName).setGraph(graph);

		return createMapGraphSuccess;
	}
	
	/**
	 * The saveMapGraph method will save the temporarily stored nodes and edges from the controller into their 
	 * appropriate text files based on mapName. First, it will generate the node list by seeing if there is already
	 * a unique node on maps node list. Secondly, it will generate the edge list based on unique node identifiers in the
	 * node list. Lastly, it will save the newly created nodes and edges to their appropriate map specified text files.
	 * @param mapName the name of the map that the nodes and edges belong to
	 * @param tempPntList the temporary points that need to be converted to nodes and saved.
	 * @param tempEdgeList the temporary list that has unique node IDs to generate edges.
	
	
	 * @return true of write was successful, false otherwise * @throws IOException */
	public boolean saveMapGraph(String mapName, List<Node> tempNodeList, List<Edge> tempEdgeList) throws IOException{	
			//Generate a new coordinate graph to be saved by admin
			graph = new CoordinateGraph(tempNodeList, tempEdgeList);
			//STORE it as the new graph
			mapTable.get(mapName).setGraph(graph);
			//IF the node list is not empty THEN

			try{
				//STORE the new nodes and edges
				fileProcessing.saveGraphInformation(mapTable.get(mapName).getGraph().getNodes(), 
													mapTable.get(mapName).getGraph().getEdges(), 
													mapName);
			}catch(IOException e){
				//Signal input output error to controller based on false
				System.out.println(e.toString());
				return false;
			}
			System.out.println("File saved successfully");	
		
			return true;
		}
	
	/**
	 * Method testDij.
	 * @param mapName String
	 */
	public void testDij(String mapName){
		
		//testing for loading of nodes/edges
		try {
			//runJDijkstra(mapName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		}//END CATCH loadNodes/Edges
		
	}
	/**
	 * calculate the multilayer path between different floors 
	 * @param mapNames different maps that are going to be calculated
	 * @param startNode the start Node
	 * @param endNode the end Node
	 * @return true if calculate multipath successfully
	 *         false if calculation fails 
	 */
	public boolean multiPathCalculate(Node start, Node end){
		boolean multiPathCalcSuccess = true;
		System.out.println("START INFORMATION FROM CONTR: " + start.getX() +" " +start.getY() + " " +start.getBuilding() + " " + start.getFloorNum());
		System.out.println("END NODE INFORMATION FROM CONTR: " + end.getX() +" " +end.getY() + " " +end.getBuilding() + " " + end.getFloorNum());
		if((start == null) || (end == null)){
			multiPathCalcSuccess = false;
			return multiPathCalcSuccess;
		}
		mapPaths = new LinkedHashMap<String, Path>();
		tempPath = new Path(null, null, null);
		int compareFloors;
		final String campusMap = "CampusMap";
		Node tempEndNode;
		Node tempStartNode;
		//int startNodeEntranceID= startNode.getEntranceID();
		//IF the two buildings for the nodes are not equal THEN
		if(!start.getBuilding().equals(end.getBuilding())){
			if(start.getBuilding().contains(campusMap) && !onCampusMap(end)){
				System.out.println("CampusMap is selected as start.");
				tempEndNode = getStartEndNodeForCampusMap(end);
				System.out.println("TEMPENDNODE B:" + tempEndNode.getBuilding() + " FLOOR: " + tempEndNode.getFloorNum() + "X: "+ tempEndNode.getX() + "Y: " + tempEndNode.getY());
				//Next compare the two floors for the given nodes
				multiPathCalcSuccess = pathCampusMapToCampusMap(start, tempEndNode);
				tempStartNode = findClosestNodeInBuilding(tempEndNode);
				compareFloors = compareFloorNum(tempStartNode, end);
				System.out.println("COMAPRED FLOORS" + compareFloors);
				System.out.println("TEMPSTARTNODE :" + tempStartNode.getBuilding() + " FLOOR: "  + tempStartNode.getFloorNum() + "X: "+ tempStartNode.getX() + "Y: " + tempStartNode.getY());
				tempPath = new Path(null, null, null);
	
				multiPathCalcSuccess = calculatePathForFloors(tempStartNode,end, compareFloors);
				printMapPaths();
			}else if(end.getBuilding().equals(campusMap) && !onCampusMap(start)){
				tempEndNode = getStartEndNodeForCampusMap(start);
				tempStartNode = findClosestNodeInBuilding(tempEndNode);
				compareFloors = compareFloorNum(start, tempEndNode);
				System.out.println("COMAPRED FLOORS" + compareFloors);
				System.out.println("TEMPENDNODE B:" + tempEndNode.getBuilding() + " FLOOR: " + tempEndNode.getFloorNum() + "X: "+ tempEndNode.getX() + "Y: " + tempEndNode.getY());
				multiPathCalcSuccess = calculatePathForFloors(start, tempStartNode, compareFloors);
				tempStartNode = getStartEndNodeForCampusMap(tempEndNode);
				System.out.println("TEMPSTARTNODE :" + tempStartNode.getBuilding() + " FLOOR: "  + tempStartNode.getFloorNum() + "X: "+ tempStartNode.getX() + "Y: " + tempStartNode.getY());

				tempPath = new Path(null, null, null);
				multiPathCalcSuccess = pathCampusMapToCampusMap(tempStartNode, end);
			
			}
			//Edge case where the start point is on campus map and an end point is selected on building from
			//campus map view
			else if(start.getBuilding().contains(campusMap) && onCampusMap(end)){
				multiPathCalcSuccess = pathCampusMapToCampusMap(start, end);
			}
			//Edge case where the end point is on campusMap and start point is a selected building on campus 
			//map view
			else if(end.getBuilding().contains(campusMap) && onCampusMap(start)){
				multiPathCalcSuccess = pathCampusMapToCampusMap(start, end);
			}
			//Edge case where two selected points are on buildings but in campus map view
			else if(!end.getBuilding().contains(campusMap) && !start.getBuilding().contains(campusMap) && onCampusMap(start) && onCampusMap(end)){
				multiPathCalcSuccess = pathCampusMapToCampusMap(start, end);
			}
			//Edge case where two points are not on campus map but in seperate buildings
		}
		if(start.getBuilding().equals(end.getBuilding())){
			System.out.println("The buildings are the same");
			Node exchangeNode = null;
			if(onCampusMap(start)  && start.getFloorNum() != 0){
				//Edge case in which the start point in on campus map but still for same building
				System.out.println("Start is on campus");
				multiPathCalcSuccess = pathCampusMapToCampusMap(start,start);
				exchangeNode = findClosestNodeInBuilding(start);
				multiPathCalcSuccess = sameBuildingCalculation(exchangeNode, end);
				printMapPaths();
			}
			else if(onCampusMap(end) && end.getFloorNum() != 0){
				//Edge case in which the end node is on campus map but still technically with same building
				multiPathCalcSuccess = pathCampusMapToCampusMap(end,end);
				exchangeNode = findClosestNodeInBuilding(end);
				multiPathCalcSuccess = sameBuildingCalculation(start, exchangeNode);
			}else{
				multiPathCalcSuccess = sameBuildingCalculation(start,end);
			}
			
			
		}
		return multiPathCalcSuccess;
	}
	private boolean sameBuildingCalculation(Node start, Node end){
		boolean sameBuildingCalc = true;
		int compareFloors;
		//Next compare the two floors for the given nodes
		compareFloors = compareFloorNum(start, end);
		//Start Floor is higher then end floor go down
		if(compareFloors == 1 || compareFloors == -1){
			sameBuildingCalc = calculatePathForFloors(start, end, compareFloors);
		}
		//Start floor equals end floor stay and do single path calculate
		else{
			sameBuildingCalc = calculatePathForFloors(start, end, 0);
		}
		return sameBuildingCalc;
	}
	private boolean pathCampusMapToCampusMap(Node start, Node end){
		boolean pathCampusMapToCampusMapCalculate = true;
		
		Node additionStartNodeforMap = null;
		Edge additionStartEdgeforMap = null;

		Node additionEndNodeforMap = null;
		Edge additionEndEdgeForMap = null;
		
		if(start.equals(end)){
			additionStartNodeforMap = createTemporaryCampusMapNode(start);
			additionStartEdgeforMap = createTemporaryCampusMapEdge(additionStartNodeforMap);
			mapTable.get("CampusMap_0").getGraph().getNodes().add(additionStartNodeforMap);
			mapTable.get("CampusMap_0").getGraph().getEdges().add(additionStartEdgeforMap);
			pathCampusMapToCampusMapCalculate = calculatePathForFloors(additionStartNodeforMap, end, 0);
			mapTable.get("CampusMap_0").getGraph().getNodes().remove(additionStartNodeforMap);
			mapTable.get("CampusMap_0").getGraph().getEdges().remove(additionStartEdgeforMap);

		}
		else if(!start.getBuilding().contains("CampusMap")){
			additionStartNodeforMap = createTemporaryCampusMapNode(start);
			additionStartEdgeforMap = createTemporaryCampusMapEdge(additionStartNodeforMap);
			mapTable.get("CampusMap_0").getGraph().getNodes().add(additionStartNodeforMap);
			mapTable.get("CampusMap_0").getGraph().getEdges().add(additionStartEdgeforMap);

			pathCampusMapToCampusMapCalculate = calculatePathForFloors(additionStartNodeforMap, end, 0);
			mapTable.get("CampusMap_0").getGraph().getNodes().remove(additionStartNodeforMap);
			mapTable.get("CampusMap_0").getGraph().getEdges().remove(additionStartEdgeforMap);

		}
		else if(!end.getBuilding().contains("CampusMap")){
			System.out.println("THE END DOES NOT CONATIN CMAP");
			additionEndNodeforMap = createTemporaryCampusMapNode(end);
			additionEndEdgeForMap = createTemporaryCampusMapEdge(additionEndNodeforMap);
			mapTable.get("CampusMap_0").getGraph().getNodes().add(additionEndNodeforMap);
			mapTable.get("CampusMap_0").getGraph().getEdges().add(additionEndEdgeForMap);

			pathCampusMapToCampusMapCalculate = calculatePathForFloors(start, additionEndNodeforMap, 0);
			mapTable.get("CampusMap_0").getGraph().getNodes().remove(additionEndNodeforMap);
			mapTable.get("CampusMap_0").getGraph().getEdges().remove(additionEndEdgeForMap);
		}
		else{
			additionStartNodeforMap = createTemporaryCampusMapNode(start);
			additionStartEdgeforMap = createTemporaryCampusMapEdge(additionStartNodeforMap);
			mapTable.get("CampusMap_0").getGraph().getNodes().add(additionStartNodeforMap);
			mapTable.get("CampusMap_0").getGraph().getEdges().add(additionStartEdgeforMap);
			additionEndNodeforMap = createTemporaryCampusMapNode(end);
			additionEndEdgeForMap = createTemporaryCampusMapEdge(additionEndNodeforMap);
			mapTable.get("CampusMap_0").getGraph().getNodes().add(additionEndNodeforMap);
			mapTable.get("CampusMap_0").getGraph().getEdges().add(additionEndEdgeForMap);
			pathCampusMapToCampusMapCalculate = calculatePathForFloors(additionStartNodeforMap, additionEndNodeforMap, 0);
			mapTable.get("CampusMap_0").getGraph().getNodes().remove(additionStartNodeforMap);
			mapTable.get("CampusMap_0").getGraph().getEdges().remove(additionStartEdgeforMap);
			mapTable.get("CampusMap_0").getGraph().getNodes().remove(additionEndNodeforMap);
			mapTable.get("CampusMap_0").getGraph().getEdges().remove(additionEndEdgeForMap);
		}
		tempPath = new Path(null, null, null);
		return pathCampusMapToCampusMapCalculate;
	}
	private Edge createTemporaryCampusMapEdge(Node campusMapEndNode){
		Edge temporaryCampusMapEdge = null;
		double currentNodeDifference;
		double lastNodeDifference = Integer.MAX_VALUE;
		Node closestNode = null;
		for(Node node: mapTable.get("CampusMap_0").getGraph().getNodes()){
			currentNodeDifference = calculateDistance(node.getX(),campusMapEndNode.getX(),node.getY(),campusMapEndNode.getY());
			if(currentNodeDifference < lastNodeDifference){
				lastNodeDifference = currentNodeDifference;
				closestNode = node;
			}
		}
		temporaryCampusMapEdge = new Edge(mapTable.get("CampusMap_0").getGraph().getEdges().size()+1,
										  campusMapEndNode,
										  closestNode,
										  lastNodeDifference);
		
		return temporaryCampusMapEdge;
	}
	private Node createTemporaryCampusMapNode(Node campusMapEndNode){
			Node temporaryCampusMapNode = null;
			for(Node node: mapTable.get("CampusMap_0").getGraph().getNodes()){
				if(node.equals(campusMapEndNode)){
					temporaryCampusMapNode = new Node(mapTable.get("CampusMap_0").getGraph().getNodes().size()+1,
							campusMapEndNode.getX(),
							campusMapEndNode.getY(),
							campusMapEndNode.getEntranceID(),
							"CampusMap",
							0,
							campusMapEndNode.getType(),
							campusMapEndNode.getDescription());
							break;
				}
			}
			System.out.println("Temporary path node was not able to be craeted");
			return temporaryCampusMapNode;
	}
	private boolean onCampusMap(Node end){
		boolean exists = false;
		for(Node node: mapTable.get("CampusMap_0").getGraph().getNodes()){
			if(node.equals(end)){
				exists = true;
				return exists;
			}
		}
		return exists;
	}
	private Node findClosestNodeInBuilding(Node startOrEnd){
			int highestFloor = 5;
			int currentFloorDifference = 0;
			int closestFloor = Integer.MAX_VALUE;
			Node closestNode = null;
			while(highestFloor != 0){
				if(mapTable.get(startOrEnd.getBuilding()+ "_" + highestFloor) == null){
					highestFloor--;
					continue;
				}else{
					for(Node node: mapTable.get(startOrEnd.getBuilding()+ "_" + highestFloor).getGraph().getNodes()){
						currentFloorDifference = Math.abs(highestFloor - startOrEnd.getFloorNum());
							if(currentFloorDifference < closestFloor){
								if(node.getEntranceID() == startOrEnd.getEntranceID()){
									closestNode = node;
									closestFloor = currentFloorDifference;
								}//
							}//
					}//END-FOR/
					highestFloor--;
				}//END-IF
			}//END-WHILE
			return closestNode;
	}
	
	private boolean calculatePathForFloors(Node start, Node end, int compareFloors){
			boolean floorPathCalculateSuccess = true;
			startNode = null;
			endNode =  null;
			int currentFloorNumber = start.getFloorNum();
			int nextFloorNumber = -1;
			//Start Floor is higher then end floor go down
			if(compareFloors == 1){
				while(currentFloorNumber > end.getFloorNum() ){
					System.out.println("Floor is lower\n");
					nextFloorNumber = currentFloorNumber;
					nextFloorNumber--;
					floorPathCalculateSuccess = calculateSingleFloorPath(start,end,currentFloorNumber,nextFloorNumber);
					currentFloorNumber--;
				}
					System.out.println("END NODE INFORMATION BEFORE FLOOR :" + end.getBuilding()+ " " +  end.getFloorNum() + " " + end.getX() + " " + end.getY());
					floorPathCalculateSuccess = calculateSingleFloorPath(start,end,currentFloorNumber,nextFloorNumber);
			}//END FIRST IF
			//Start floor is less then end floor go up
			else if(compareFloors == -1){
				while(currentFloorNumber < end.getFloorNum()){
					System.out.println("Floor is higher\n");
					nextFloorNumber = currentFloorNumber;
					nextFloorNumber++;
					floorPathCalculateSuccess = calculateSingleFloorPath(start,end,currentFloorNumber,nextFloorNumber);
					currentFloorNumber++;
				}
					floorPathCalculateSuccess = calculateSingleFloorPath(start,end,currentFloorNumber,nextFloorNumber);

			}
			else{
				System.out.println("Floors are same");
				tempPath.setStartPoint(start);
				tempPath.setEndPoint(end);
				floorPathCalculateSuccess = singlePathCalculate(start.getBuilding() + "_" + start.getFloorNum(), tempPath);
			}

			return floorPathCalculateSuccess;
		
	}
	private boolean calculateSingleFloorPath(Node start, Node end, int currentFloorNumber, int nextFloorNumber){
		boolean calculateSingleFloorPath = true;
		ArrayList<Integer> tempEntIdListStart = new ArrayList<Integer>();
		ArrayList<Integer>  tempEntIdListEnd = new ArrayList<Integer>();
		HashSet<Integer>	sameEntIdList = new HashSet<Integer>();
		tempEntIdListStart = getFloorPathIDs(start.getBuilding(),currentFloorNumber);
		
		if(currentFloorNumber == nextFloorNumber){
			startNode = getStartEndPathNode(mapTable.get(start.getBuilding() + "_"+ currentFloorNumber).getGraph().getNodes(),
					endNode.getEntranceID());
			tempPath.setStartPoint(startNode);
			tempPath.setEndPoint(end);
			System.out.println(tempPath.getEndPoint().getEntranceID());
			System.out.println("FLOOR NUMBER " + currentFloorNumber);
			System.out.println("NEXT FLOOR: " + start.getBuilding() + "_"+ currentFloorNumber);
			//System.out.println("New start point" + path.getStartPoint().getBuilding() + " " +path.getStartPoint().getFloorNum() + " " +path.getStartPoint().getFloorNum()
			//		 + " " +path.getStartPoint().getX() + " " +  path.getStartPoint().getY());
			calculateSingleFloorPath = singlePathCalculate(startNode.getBuilding() + "_" + currentFloorNumber, tempPath);
			tempPath = new Path(null, null, null);
			return calculateSingleFloorPath;
		}
		
		for(int i : tempEntIdListStart)
			System.out.println("ENTRANCE ID ON THIS FLOOR: " + start.getBuilding() + " " + currentFloorNumber + " " + i);
		System.out.println("its all set");
		
		System.out.println("NEXT FLOOR BELOW: " + nextFloorNumber);
		tempEntIdListEnd = getFloorPathIDs(end.getBuilding(), nextFloorNumber);
		if(tempEntIdListEnd == null){
			System.out.println("There are no entrances/exits on floor");
			calculateSingleFloorPath = false;
			return calculateSingleFloorPath;
		}
		System.out.println("printing next floor entrance nodes: " + nextFloorNumber);
		for(int i : tempEntIdListEnd)
			System.out.println(i);
		for(int i : tempEntIdListStart){
			for(int j: tempEntIdListEnd){
				if( i == j){
					System.out.println("Adding to same Entrance ID list " + i);
					sameEntIdList.add(i);
				}
			}
		}
		if(startNode == null){
			System.out.println("startNode is null");
			startNode = start;
		}else{
			System.out.println("attempting to set start node");
			System.out.println("START NODE Entrance ID set" + endNode.getEntranceID());
			startNode = getStartEndPathNode(mapTable.get(start.getBuilding() + "_"+ currentFloorNumber).getGraph().getNodes(),
					endNode.getEntranceID());
		}
		if(sameEntIdList.isEmpty()){
			System.out.println("There are no same entrances on the next floor");
			calculateSingleFloorPath = false;
			return calculateSingleFloorPath;
		}else{
			for(int entranceID: sameEntIdList){
				System.out.println("attempting to set endnode");
				System.out.println("END NODE Entrance ID set" + entranceID);
				System.out.println("End Node Floor Number" + currentFloorNumber);
				endNode = getStartEndPathNode(mapTable.get(startNode.getBuilding() + "_"+ currentFloorNumber).getGraph().getNodes(),
						  entranceID);
				
			}
		}
		tempPath.setStartPoint(startNode);
		tempPath.setEndPoint(endNode);
		if(tempPath.getStartPoint() == null || tempPath.getEndPoint() == null){
			System.out.println("one of these is null");
		}
		System.out.println("single path calculate");
		System.out.println("START NODE INFORMATION: " + startNode.getX() +" " +startNode.getY() + " " +startNode.getBuilding() + " " + startNode.getFloorNum());
		System.out.println("END NODE INFORMATION: " + endNode.getX() +" " +endNode.getY() + " " +endNode.getBuilding() + " " + endNode.getFloorNum());
		if(calculateSingleFloorPath = singlePathCalculate(startNode.getBuilding() + "_" + currentFloorNumber, tempPath)){
			System.out.println("Floor path was calculated correctly");
		}else{
			System.out.println("Floor path was not calcuated correctly");
			return calculateSingleFloorPath;
		}
		System.out.println("Setting new start point");

		
		tempPath = new Path(null, null, null);
		return calculateSingleFloorPath;
	}

	public Node getStartEndPathNode(List<Node> nodes, int entranceID){
		Node tempNode=null;
		for(Node node:nodes)
		{
			if(node.getEntranceID()==entranceID){
				tempNode= node;
			}
			  
		}
		return tempNode;
	}
	/**
	 * get start Node and end Node in a floor 
	 * @return the start Node and end Node in a floor
	 */
	public ArrayList<Integer> getFloorPathIDs(String startFloorName,int floorNumber)
	{
		ArrayList<Integer> tempEntIDList = new ArrayList<Integer>();
		System.out.println(startFloorName + "_" + floorNumber);
		if(mapTable.get(startFloorName + "_" + floorNumber).getGraph().getNodes() == null){
			System.out.println("Nodes do not exist");
		}
		for(Node node1: mapTable.get(startFloorName + "_" + floorNumber).getGraph().getNodes()){
			if(node1.getEntranceID() != 0){
				tempEntIDList.add(node1.getEntranceID());
			}
		}
		return tempEntIDList;
	}
	private Node getStartEndNodeForCampusMap(Node end){
		int currentFloorDifference;
		int closestFloor = Integer.MAX_VALUE;
		Node closestNode = null;
		campusMapNodeList = new HashMap<Integer,Node>();
		for(Node node:mapTable.get("CampusMap_0").getGraph().getNodes())
		{
			if(end.getBuilding().equals(node.getBuilding()) && node.getEntranceID() != 0){
				campusMapNodeList.put(node.getEntranceID(),node);
			}
			  
		}
		for(Integer i : campusMapNodeList.keySet()){
			if(end.getBuilding().equals(campusMapNodeList.get(i).getBuilding())){
				currentFloorDifference = Math.abs(campusMapNodeList.get(i).getFloorNum() - end.getFloorNum());
				if(currentFloorDifference < closestFloor){
						closestNode = campusMapNodeList.get(i);
						closestFloor = currentFloorDifference;
					}//
				}//
			}
		return closestNode;
	}
	
	/**
	 * compare the floor number of two different floors
	 * @param node1 the first node that is compared
	 * @param node2 the second node that is compared
	 * @return 1 if floor number of the first node is larger than the second node
	 *         0 if floor number of the first node is smaller than the second node
	 *         -1 if floor number of the first node id the same as the second node  
	 */
	public int compareFloorNum(Node startNode,Node endNode)
	{

		if(startNode.getFloorNum()>endNode.getFloorNum()){          //go down
			System.out.println("we need to go down");
			return 1;
		}else if(startNode.getFloorNum() == endNode.getFloorNum()){   //same floor
			return 0;
		}else{
			return -1;                                       		//go up
		}
			
	}
	/**
	 * Method testDij.
	 * @param mapName String
	 */
	public boolean singlePathCalculate(String mapName, Path tempPath){
		boolean calculateSuccess = true;
		//testing for loading of nodes/edges
		try {
			calculateSuccess = runJDijkstra(mapName, tempPath);
			
		} catch (Exception e) {
			calculateSuccess = false;
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		}//END CATCH loadNodes/Edges
		return calculateSuccess;
	}
	/** Dijkstra algorithim
	 * @param mapName String
	 */
	public boolean runJDijkstra(String mapName, Path tempPath){
		boolean dijkstraSuccess = true;
		//Create object instance with temporary dijkstra algorithim
		System.out.println("START POINT INFO: " +tempPath.getStartPoint().getBuilding() + " " +tempPath.getStartPoint().getFloorNum() + " "+ tempPath.getStartPoint().getY() + " "  + tempPath.getStartPoint().getFloorNum());
		//Edge case where the start and end point equal each other
		if(tempPath.getStartPoint() == tempPath.getEndPoint()){
			System.out.println("The points are the same");
			List<Node> wayPoints = new ArrayList<Node>();
			System.out.println("START POINT INFO: " +tempPath.getStartPoint().getBuilding() + " " + tempPath.getStartPoint().getY() + " "  + tempPath.getStartPoint().getFloorNum());
			Node tempNode = new Node(1,1,1,1,"1",1,"1", "1");
			tempNode = tempPath.getStartPoint();
			System.out.println("START POINT INFO: " +tempNode.getBuilding() + " " + tempNode.getY() + " "  + tempNode.getFloorNum());
			wayPoints.add(tempNode);
			tempPath.setPath(wayPoints);
			if(tempPath.getWayPoints() == null){
				System.out.println("We were un-able to add the waypoints");
			}
			System.out.println("MAP NAME: " + mapName);
			printNodes(tempPath.getWayPoints());
			mapPaths.put(mapName, tempPath);
			printNodes(mapPaths.get(mapName).getWayPoints());
			return dijkstraSuccess;
		}
		JDijkstra dijkstra = new JDijkstra(mapTable.get(mapName).getGraph());
		System.out.println("Graph is set");
		//Start the execution with the first starting node
		dijkstra.execute(tempPath.getStartPoint());
		System.out.println("temp path START POINT INFO: " + " " +tempPath.getStartPoint().getBuilding() + " " + tempPath.getStartPoint().getFloorNum() + " " + tempPath.getStartPoint().getX() + " " + tempPath.getStartPoint().getY());

		System.out.println("temp path END POINT INFO: " + " " +tempPath.getEndPoint().getBuilding() + " " + tempPath.getEndPoint().getFloorNum() + " " + tempPath.getEndPoint().getX() + " " + tempPath.getEndPoint().getY());
		//Store the wayPoints for the map's graph
		LinkedList<Node> wayPoints = dijkstra.getPath(tempPath.getEndPoint());
		
		//Create the new path with the start point, end point, and way points
		tempPath.setPath(wayPoints);
		if(tempPath.getWayPoints() == null){
			System.out.println("Something went wrong storing nodes");
		}
		else{
			System.out.println("PATH WAY POINTS: " + mapName);
			printNodes(tempPath.getWayPoints());
		}
		
		if(!mapPaths.containsKey(mapName)){
			mapPaths.put(mapName, tempPath);
			System.out.println("Path for map " + mapName + " does not exist");
		}else{
			dijkstraSuccess = false;
			System.out.println("Path was not successfully placed");
		}
		return dijkstraSuccess;
	}
	//private calculatePathDistance(LinkedList<Node> wayPoints){
		//for(Node wayPoint: )
	//}
	/**
	 * isValidAdmin method validates if the user that has choosen to login as admin is an admin.
	 * @param userName the string representation of the login username
	 * @param password the string representation of the login password
	
	 * @return true if user is admin; otherwise false. */
	public boolean isValidAdmin(String userName, String password){
		//Assume user is not an admin
		boolean isAdmin = false;
		//FOR EACH admin in admin list
		for(Admin admin: admins){
			//IF current admin user name EQUALS param username AND admin password EQUALS param password
			if((admin.getUsername().equals(userName)) && (admin.getPassword().equals(password)))
				//SET isAdmin as valid
				isAdmin =true;
		}
		return isAdmin;
	}
	/**
	 * Validates the point for the view. By returning a Node for the controller to extract a point.
	 * Take in x and y values for user chosen node points, then cycle through each node on current coordinate graph node list.
	 * Then keep track of current difference in x and y value. If the current difference is less then the previous difference;
	 * of both x and y points then set the new validated point as the new closest point.
	 * @param x the x value for the given point
	 * @param y the y value for the given point
	
	 * @param mapName String
	 * @return the more accurate validated point */
	public Node validatePoint(String mapName, int x, int y, String lastName){
		Node validatedNode = null;
		if(mapTable.isEmpty()){
			System.out.println("MapTable is empty, Validation Failed");
			return validatedNode;
		}
		double currentDiff = 0.0;
		double previousDiff = Double.POSITIVE_INFINITY;
		for(Node node: mapTable.get(mapName).getGraph().getNodes()){
			currentDiff = calculateDistance(node.getX(),x,node.getY(),y);
			if(currentDiff < previousDiff){
				previousDiff = currentDiff;
				validatedNode = node;
			}				
		}
		return validatedNode;
	}
	public Point validatePoint(String mapName, int x, int y){
		
		Node validatedNode = null;
		if(mapTable.isEmpty()){
			Point pnt = new Point();
			System.out.println("MapTable is empty, Validation Failed");
			return pnt;
		}
		double currentDiff = 0.0;
		double previousDiff = Double.POSITIVE_INFINITY;
		for(Node node: mapTable.get(mapName).getGraph().getNodes()){
			currentDiff = calculateDistance(node.getX(),x,node.getY(),y);
			if(currentDiff < previousDiff){
				previousDiff = currentDiff;
				validatedNode = node;
			}				
		}
		Point correctedPoint = new Point(validatedNode.getX(), validatedNode.getY());
		System.out.println(correctedPoint.getX() + " " + correctedPoint.getY());
		return correctedPoint;
	}
	
	/**
	 * Method printNodes.
	 * @param mapName String
	 */
	public void printNodes(String mapName){
		int count = 0;
		for(Node node: mapTable.get(mapName).getGraph().getNodes()){
			count++;
			System.out.print(node.getID() + " " + node.getX() + " " + node.getY() + " " + node.getFloorNum());
			System.out.println();
		}
		System.out.println(count);
		System.out.println("END PRINT NODES");
	}
	/**
	 * Method printNodes.
	 * @param nodes List<Node>
	 */
	public void printNodes(List<Node> nodes){
		int count = 0;
		System.out.println("PRINTING NODES");
		for(Node node: nodes){
			count++;
			System.out.print(node.getID() + " " + node.getX() + " " + node.getY());
			System.out.println();
		}
		System.out.println("END PRINT NODES");
	}
	public void printEdges(){
		for(Edge edge: edges){
			System.out.print(edge.getEdgeID() + " " + edge.getSource().getID() + " " + edge.getDestination().getID() + " " + edge.getEdgeLength());
			System.out.println();
		}
		System.out.println("END PRINT EDGES");
	}
	public void printAdmins(){
		for(Admin admin: admins){
			System.out.print(admin.getUsername()+ " " + admin.getPassword());
			System.out.println();
		}
		System.out.println("END OF PRINT ADMIN");
	}

	public void printMaps(){
		for(String value: mapTable.keySet()){
			System.out.println(value);
		}
	}

	
	/**
	 * Method convertWayPointsToPoints.
	 * @return ArrayList<Point>
	
	public ArrayList<Point> convertWayPointsToPoints(){
		ArrayList<Point> tempWayPoints = new ArrayList<Point>();
		for(Node node : path.getWayPoints()){
			tempWayPoints.add(new Point(node.getX(), node.getY()));
		}
		return tempWayPoints;
	}*/
	/**
	 * Method getArrayOfMapNames.
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getArrayOfMapNames(){
		ArrayList<String> tempArrayOfMapNames = new ArrayList<String>();
		Enumeration e = mapTable.keys();
		while(e.hasMoreElements()){
			String mapName = (String) e.nextElement();
			tempArrayOfMapNames.add(mapName);
		}
		return tempArrayOfMapNames;
	}
	/**
	 * Method getImgURLS returns a string arraylist of map URLS
	 * @return array list of string urls
	 */
	public ArrayList<String> getImgURLS(){
		ArrayList<String> mapImgURLS = new ArrayList<String>();
		for(String map: mapTable.keySet()){
			mapImgURLS.add(mapTable.get(map).getMapImgURL());
		}
		return mapImgURLS;
	}
	/**
	 * Method getMapTypes returns a string array list of map types
	 * @return arraylist of map types
	 */
	public ArrayList<String> getMapTypes(){
		ArrayList<String> mapTypes = new ArrayList<String>();
		for(String map: mapTable.keySet()){
			mapTypes.add(mapTable.get(map).getMapType());
		}
		return mapTypes;
	}
	public LinkedHashMap<String,Path> getMapPaths(){
		return this.mapPaths;
	}
	/**
	 * Method getNodeList.
	 * @param mapName String
	 * @return List<Node>
	 */
	public List<Node> getNodeList(String mapName){
		List<Node> currentNodeList = mapTable.get(mapName).getGraph().getNodes();	
		return currentNodeList;		
	}
	/**
	 * Method getEdgeList.
	 * @param mapName String
	 * @return List<Edge>
	 */
	public List<Edge> getEdgeList(String mapName){
		List<Edge> currentEdgeList = mapTable.get(mapName).getGraph().getEdges();	
		return currentEdgeList;
	}
	/**
	 * Method calculateDistance.
	 * @param x1 double
	 * @param x2 double
	 * @param y1 double
	 * @param y2 double
	 * @return double
	 */
	public double calculateDistance(double x1, double x2, double y1, double y2){
		return Math.sqrt(Math.pow(x2-x1, 2)+ Math.pow(y2 - y1, 2));
	}

    //find node Id for the start of the edge
    /**
     * Method findNodeId.
     * @param mapName String
     * @param point Point
     * @return int
     */
    public int findNodeId(String mapName,Point point){
    	for(Node node: mapTable.get(mapName).getGraph().getNodes())
    	{
			if((node.getX()==point.x)&&(node.getY()==point.y))
			{ 
				return node.getID();   
			}
    	}
		return 0;
    }
    public void printMapPaths(){
    	for(String s: mapPaths.keySet()){
    		System.out.println("PRINTING NODE INFO FOR MAP: " + s);
			printNodes(mapPaths.get(s).getWayPoints());
    	}
    }
    
}

    
