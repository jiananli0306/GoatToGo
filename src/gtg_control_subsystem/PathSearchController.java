package gtg_control_subsystem;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import gtg_model_subsystem.Node;
import gtg_model_subsystem.Path;
import gtg_view_subsystem.PathData;

public class PathSearchController {
	private MainController mainController;
	private MapDataController mapDataController;
	
	private LinkedHashMap<String, Path> MultilayerPathcalculationResult;
	private ArrayList<String> resultMapList;
	private Path currentPath;
	private Node startNode;
	private Node endNode;
	
	public PathSearchController(MainController controlInterface, MapDataController mapDataComponent) {
		mainController = controlInterface;
		mapDataController = mapDataComponent;
	}
	
	public Point setTaskPnt(Point taskPnt, String pntType, String mapName){
		Point targetPnt = new Point();		
		//System.out.println("Task Type: " + pntType);
		if(pntType.equals("FROM")){
			startNode = mainController.mapModel.validatePoint(mapName, (int)(taskPnt.getX()),(int)(taskPnt.getY()),"");
			if(startNode!=null){
				targetPnt.x = startNode.getX();
				targetPnt.y = startNode.getY();
			}
		}
		else if(pntType.equals("TO")){
			endNode = mainController.mapModel.validatePoint(mapName, (int)(taskPnt.getX()),(int)(taskPnt.getY()),"");
			if(endNode!=null){
				targetPnt.x = endNode.getX();
				targetPnt.y = endNode.getY();
			}
		}
		
		return targetPnt;
	}
	
	public PathData getDesiredPath(int Index){
		PathData path = new PathData();
		
		// Get requested path
		String requestedMapName = resultMapList.get(Index);
		currentPath = MultilayerPathcalculationResult.get(requestedMapName);

		// Set StartPnt
		Point TempStartPnt = new Point();
		Node TempNode =  currentPath.getStartPoint();
		TempStartPnt.x = TempNode.getX();
		TempStartPnt.y = TempNode.getY();		
		path.setStartPoint(TempStartPnt);
		
		// Set EndPnt
		TempNode = currentPath.getEndPoint();
		Point TempEndPnt = new Point();
		TempEndPnt.x = TempNode.getX();
		TempEndPnt.y = TempNode.getY();
		path.setEndPoint(TempEndPnt);		
		
		// Set wayPoint List
		ArrayList<Point> displayWayPnts = convertNodeListIntoPointList(currentPath);
		path.setWayPoints(displayWayPnts);

		// Set mapName List
		path.setArrayOfMapNames(resultMapList);	

		System.out.println("The requested mapName is: " + requestedMapName);
		// Set URL of current map
		int IndexOfMapURL = mapDataController.getCurrentMapNameList().indexOf(requestedMapName);
		String mapURL = mapDataController.getCurrentMapURLList().get(IndexOfMapURL);	

		System.out.println("The requested mapURL is: " + mapURL);
		path.setMapURL(mapURL);

		return path;
	}
	
	public String getStartEndNodeDescription(String pointType){
		String description = null;
		if(pointType.equals("FROM")){
			description = currentPath.getStartPoint().getDescription();
		}
		else if(pointType.equals("TO")){
			description = currentPath.getEndPoint().getDescription();
		}
		return description;
	}	
	
	private ArrayList<Point> convertNodeListIntoPointList(Path inputPath){
		ArrayList<Point> pntPath = new ArrayList<Point>();
		List<Node> currentNodePath = inputPath.getWayPoints();
		if(!currentNodePath.isEmpty()){
			for(Node nd:currentNodePath){
				Point pnt = new Point();
				pnt.x = nd.getX();
				pnt.y = nd.getY();
				pntPath.add(pnt);			
			}
		}		
		return pntPath;
	}	
	
	public boolean getPathData(){
		boolean pathCalculated = false;
		resultMapList=new ArrayList<String>();
		if(startNode!=null && endNode!=null){

			System.out.println("FROM: " + startNode.getBuilding() + " " + startNode.getFloorNum() + " " + startNode.getX() + " " + startNode.getY() + " " + startNode.getDescription());
			System.out.println("TO: " + endNode.getBuilding() + " " + endNode.getFloorNum() + " " + endNode.getX() + " " + endNode.getY() + " " + endNode.getDescription()  );
			pathCalculated =  mainController.mapModel.multiPathCalculate(startNode, endNode);
		}
		
		if(pathCalculated){	
			System.out.println("Hoooooya~~ Path Calculated!\n");
			MultilayerPathcalculationResult = mainController.mapModel.getMapPaths();
			Set<String> calculationResultMapName = MultilayerPathcalculationResult.keySet();
			Iterator<String> iterator = calculationResultMapName.iterator();
			while(iterator.hasNext()){
				String mapName = iterator.next();
				System.out.println(mapName);
				resultMapList.add(mapName);
			}
		}
		return pathCalculated;
	}	

}
