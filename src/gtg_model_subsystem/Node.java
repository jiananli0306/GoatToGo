package gtg_model_subsystem;

/**
 */
public class Node
	{
		/** the node's id
		 */
		private int id;
	    /** the node's x coordinate in the graph
	     */
		private int x;
		
		/** the node's y coordinate in the graph
		 */
		private int  y;
		private int entranceID;
		private String building;
		private int floorNum;
		private String type;
		private String description;
		
		/** the information about the node
		 *such as its name and location
		 * @param id int
		 * @param x int
		 * @param y int
		 */

		public Node(int id, int x, int y, int entranceID, String building, int floorNum, String type, String description){
			this.id = id;
			this.x = x;
			this.y = y;
			this.entranceID = entranceID;
			this.building = building;
			this.floorNum = floorNum;
			this.type = type;
			this.description = description;
		}


		public int getID() {
			return id;
		}


		public void setID(int id) {
			this.id = id;
		}


		public int getX() {
			return x;
		}


		public void setX(int x) {
			this.x = x;
		}


		public int getY() {
			return y;
		}


		public void setY(int y) {
			this.y = y;
		}


		public int getEntranceID() {
			return entranceID;
		}


		public void setEntranceID(int entranceID) {
			this.entranceID = entranceID;
		}


		public String getBuilding() {
			return building;
		}


		public void setBuilding(String building) {
			this.building = building;
		}


		public int getFloorNum() {
			return floorNum;
		}


		public void setFloorNum(int floorNum) {
			this.floorNum = floorNum;
		}


		public String getType() {
			return type;
		}


		public void setType(String type) {
			this.type = type;
		}
		
		public String getDescription(){
			return this.description;
		}
		public void setDescription(String description){
			this.description = description;
		}
		
		
	}
