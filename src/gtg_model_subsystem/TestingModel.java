package gtg_model_subsystem;
//import gtg_control_subsystem.TestController;
import java.awt.Point;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
/**
 * Separate Main process to test Model subsystem, will be changed later to hook into Controller and view API
 * To run and see what is there so far right click main model -> Run As -> Java Application
 * @author Joshua
 *
 * @version $Revision: 1.0 $
 */
public class TestingModel {
	/**
	 * Method main.
	 * @param args String[]
	 */
	public static void main(String args[])
	{
		MainModel test = new MainModel();
		//remind alyssa and neha that we need relative path and not absolute
		
		//TEST CASE 1
		test.loadFiles();
		//test.printNodes("BH_Basement");
		//test.setStartEndPathPoint(new Point(605,90), "TO", "BoyntonHall_1");
		//test.setStartEndPathPoint(new Point(422,615), "FROM", "BoyntonHall_3");
		
		//Node toNode = test.validatePoint("BoyntonHall_1",605, 90, " ");
		// BH3 TWO NODES TO TEST ON THI S FLOOR TO BH 1 (429 517) (422 615)
		//Node fromNode = test.validatePoint("BoyntonHall_3",429, 517, " ");
		Node toNode = test.validatePoint("CampusMap_0",744, 462, " ");
		// BH3 TWO NODES TO TEST ON THI S FLOOR TO BH 1 (429 517) (422 615)
		Node fromNode = test.validatePoint("CampusMap_0",294, 428, " ");
		test.multiPathCalculate(fromNode, toNode);
		LinkedHashMap<String,Path> localMapPaths = test.getMapPaths();
		ArrayList<Path> pathArrayList = new ArrayList<Path>();
		List<Node> wayPointsList = new ArrayList<Node>();
		System.out.println("Back to  main");
		for(String mapName: localMapPaths.keySet()){
			System.out.println(mapName);
			pathArrayList.add(localMapPaths.get(mapName));
		}
		for(Path p: pathArrayList){
			test.printNodes(p.getWayPoints());
			if(p.getWayPoints() == null){
				System.out.println("There are no waypoints");
			}
			
		}
		
		
		//test.printNodes(test.getMapPaths().get("BoyntonHall_1").getWayPoints());
		/**System.out.println(test.getPath().getStartPoint().getID());
		System.out.println(test.getPath().getEndPoint().getID());
		test.testDij("BH_Basement");
		Point point = test.validatePoint("BH_Basement", 125, 400);
		System.out.println("Point x: " + point.x + " Point Y: " + point.y);
		TestController testController = new TestController(test);*/
		
	}
}