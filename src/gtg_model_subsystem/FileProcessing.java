package gtg_model_subsystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
/**
 */
public class FileProcessing {
	
	/**
	 * The method loadMapList load maps from master map list stored in masterMapList.txt
	 * @return master map list * @throws IOException */
	public ArrayList<Map> loadMapList() throws IOException
	{
		//Get the master map list text file reference
		String masterMapListURL = "ModelFiles"+System.getProperty("file.separator")+ "masterMapList.txt";
		//reading each map into line
		String line;
		//array of stored types for each map
		String[] lines;
		File file = null;
		BufferedReader buffer = null;
		Map tempMap = null;
		//the maps from the master map list text file reference
		ArrayList<Map> tempMapList = null;
		
		try{
			file = new File(masterMapListURL);
			buffer = new BufferedReader(new FileReader(file));
			tempMapList = new ArrayList<Map>();
			
			while((line = buffer.readLine()) != null && !(line.trim().equals(""))){
				line.trim();
				if(line.equals("")){break;}
				lines = line.split("[\\s+]");
				//create a Map using stored types(name,graph,img url,type) for each map
				tempMap=new Map(lines[0], null, lines[1],lines[2]);
				//add the created map into the map list
				tempMapList.add(tempMap);
			}
			buffer.close();
		}
		catch(IOException e){
			System.out.println(e.toString());
		}
		return tempMapList;
	}
	/**
	 * deleteMapFromMaster will take in a map name and delete the given line that is associated with the
	 * map name given.
	 * @param mapName the name of the map needs to be deleted from masterMapList.txt
	 * @return true if delete succeeded false otherwise * @throws IOException  */
	public boolean deleteMapFromMaster(String mapName) throws IOException{
			boolean deleteSuccess = true;
			boolean firstLine = true;
			//String references to master list and a temporary written master list file
			String masterMapListURL = "ModelFiles"+System.getProperty("file.separator")+ "masterMapList.txt";
			String masterMapListTemp = "ModelFiles"+System.getProperty("file.separator")+ "masterMapList_temp.txt";
			
			//variables for manipulation
			File originalFile = null;
			File tempFile = null;
			BufferedReader reader = null;
			BufferedWriter writer = null;
			//Current line being read from file
			String line;
			//Array of map values from master list text file
			String lines[];
			try{
				originalFile = new File(masterMapListURL);
				tempFile = new File(masterMapListTemp);
				reader = new BufferedReader(new FileReader(originalFile));
				writer = new BufferedWriter(new FileWriter(tempFile));
				//WHILE a line exists in master list text file
				while((line = reader.readLine()) != null && !(line.trim().equals(""))){
						//remove the end line
						line.trim();
						//split values and store into lines array
						lines = line.split("[\\s+]");
						//IF the line map name does not equal null and the line map name does not equal map name
						if(lines[0] != null && !lines[0].equals(mapName)){
								if(firstLine){
									writer.write(line);
									firstLine = false;
								}else{
									writer.write(System.getProperty("line.separator"));
									writer.write(line);
								}		
						}
				}
			}catch(IOException e){
				//CATCH failure and set the delete success to false
				System.out.println(e.toString());
				deleteSuccess = false;
			} finally{
				writer.close();
				reader.close();
				//DELETE original master list file
				originalFile.delete();
				//RE-NAME new temp list to master list
				deleteSuccess = tempFile.renameTo(originalFile);
			}
			return deleteSuccess;
	}
	/**
	 * saveMapToMaster method saves a newly added map from admin view to master map list.
	 * @param mapName the name of map to be saved.
	 * @param mapType the type of map that we are saving (IE possible campus map).	
	 * @param mapImgURL String
	 * @return true if write was successful false if write failed. * @throws IOException */
	public boolean saveMapToMaster(String mapName, String mapImgURL, String mapType) throws IOException{
			boolean writeSuccess = true;
			try{
				//Get the master map list text file reference
				String masterMapListURL = "ModelFiles"+System.getProperty("file.separator")+ "masterMapList.txt";
				System.out.println(masterMapListURL);
				//Create a file writer that will write to file, append value set to true to append to end of document
				FileWriter fstream = new FileWriter(masterMapListURL, true);
				//Create a buffered writer for the file stream index
			    BufferedWriter out = new BufferedWriter(fstream);
			    //Write newly created map to end of master map list
			    out.write(System.getProperty("line.separator") + mapName + " " + mapImgURL + " " + mapType);
			    out.close();
			}
			catch(IOException e){
				//Print out error if either map file does not exist
				System.out.println(e.toString());
				//return false for failure
				writeSuccess = false;
			}
			return writeSuccess;
	}
	/**
	 * read nodes and edges from the txt file according to the map name
	 * @param nodes the node list that contains the nodes loaded from the txt file
	 * @param edges the edges list that contains the edges loaded from the txt file
	 * @param mapName the mapName specifies which map is loaded
	 * @return true if creating file or readind nodes,edges successful
	 *         will not return false
	 * @throws IOException
	 */
	public boolean readGraphInformation(List<Node> nodes, List<Edge> edges, String mapName)throws IOException{
		String mapGraphInformationURL = "ModelFiles"+System.getProperty("file.separator")+mapName+"_EdgeNode.txt";
		boolean readSuccess = true;
		int nodeOrEdge = 0;
		File file = new File(mapGraphInformationURL);
		BufferedReader buffer = null;
		String line;
		String[] lines = null;
		//CHECK to see if there is a new file that needs to be created
		if(createFile(file)){
			return true;
		}else{
			try{
				//file exists,begin reading
				System.out.println("Reading instead");
				buffer = new BufferedReader(new FileReader(file));
				//read line from file
				while((line = buffer.readLine()) != null && !(line.trim().equals(""))){
					line = line.trim();
					//encounter keyword NODES
					if(line.equals("NODES")){
						continue;
					}
					//encounter keyword EDGES,set flag
					if(line.equals("EDGES")){
						nodeOrEdge = 1;
						continue;
					}
					//begin reading edges or nodes
					if(nodeOrEdge == 1){
						readSuccess = readEdges(nodes, edges, line, lines);
					}else{
						readSuccess = readNodes(nodes, line, lines);
					}
				}
			}catch(IOException E){
				System.out.println(E.toString());
			}finally{
				buffer.close();
			}
		}
		return readSuccess;
	}
	/**
	 * read a node into the nodes list by reading a line from the txt file
	 * @param nodes the nodes list that stores the nodes from file
	 * @param line the line that is read currently
	 * @param lines the array stores the node id,x,y
	 * @return true if reading is successful
	 */
	public boolean readNodes(List<Node> nodes, String line, String[] lines){
			boolean readSuccess = true;
			String finalDescription;
			lines = line.split("[\\s+]");
			
			//construct a node
			Node node = new Node(Integer.parseInt(lines[0]),//id 
								 Integer.parseInt(lines[1]),//x
								 Integer.parseInt(lines[2]),//y
								 Integer.parseInt(lines[3]),//entranceID
								 lines[4],//building
								 Integer.parseInt(lines[5]),//floorNum
								 lines[6],//type
								 "");
			if(lines[7] != ""){
				finalDescription = loadDescription(lines[7]);
				node.setDescription(finalDescription);
			}else{
				node.setDescription("");
			}
			nodes.add(node);
			return readSuccess;
	}
	/**
	 * read an edge into edges list by reading a line from the txt file
	 * @param nodes the nodes list that stores the nodes list from file
	 * @param edges the edges list that stored the edges list from file
	 * @param line the line that is read currently
	 * @param lines the array stores the edge id,x,y
	 * @return true if reading is successful
	 *         false if one of the nodes in the edge does not exist
	 */
	public boolean readEdges(List<Node> nodes, List<Edge> edges, String line, String[] lines){
			boolean readSuccess = true;
			lines = line.split("[\\s+]");
			
			//Subtract one for arraylist index value
			Node tempNode1 = getNode(nodes, Integer.parseInt(lines[1]));
			Node tempNode2 = getNode(nodes, Integer.parseInt(lines[2]));
			//one of the nodes in the edge does not exist
			if(tempNode1 == null){
				System.out.println("Node1  could not be read for edge");
				readSuccess = false;
			}
			if(tempNode2 == null){
				System.out.println("Node2  could not be read for edge");
				readSuccess = false;
			}
			//construct an edge
			Edge edge = new Edge(Integer.parseInt(lines[0]), 
								 tempNode1, 
								 tempNode2,
								 calculateDistance(tempNode1.getX(), tempNode2.getX(), tempNode1.getY(), tempNode2.getY())
								 );
			
			edges.add(edge);
			return readSuccess;
	}
	/**
	 * get a node by nodeID
	 * @param nodes the nodes list
	 * @param nodeID the ID of the node that we want to find 
	 * @return a node if it is found
	 *         null if it does not exist
	 */
	public Node getNode(List<Node> nodes, int nodeID){
			Node nodeFound = null;
			for(Node node: nodes){
				if(node.getID() == nodeID){
					nodeFound = node;
					break;
				}
			}
			return nodeFound;
	}
	/**
	 * Method readAdmin.
	 * @param admins List<Admin>
	 * @throws IOException
	 */
	public void readAdmin(List<Admin> admins) throws IOException{
		String adminURL = "ModelFiles"+System.getProperty("file.separator")+"adminFile.txt";
		try{
		File file = new File(adminURL);
		BufferedReader buffer = new BufferedReader(new FileReader(file));
		String line;
		String[] lines;
		while((line = buffer.readLine()) != null && !(line.trim().equals(""))){
				line = line.trim();
				if(line.equals("")){break;}
				lines = line.split("[\\s+]");
				
				Admin admin = new Admin(lines[0], lines[1]);
				admins.add(admin);
		}
		buffer.close();
		}catch(IOException e){
			System.out.println(e.toString());
		}
	}
	public void saveGraphInformation(List<Node> nodes, List<Edge> edges, String mapName) throws IOException{
		String mapGraphInformationURL = "ModelFiles"+System.getProperty("file.separator")+mapName+"_EdgeNode.txt";
		try{
		    FileWriter fstream = new FileWriter(mapGraphInformationURL, false);
		    BufferedWriter out = new BufferedWriter(fstream);
		    out.write("NODES" + System.getProperty("line.separator"));
		    for(Node node: nodes){
		    	out.write(node.getID() + " " + 
		    			  node.getX() + " " + 
		    			  node.getY() + " " +
		    			  node.getEntranceID() + " " +
		    			  node.getBuilding() + " " +
		    			  node.getFloorNum() + " " +
		    			  node.getType() + " " +
		    			  saveDescription(node.getDescription()) +
		    			  System.getProperty("line.separator"));
		    }
		    System.out.println("File Node Write Success!");

		    out.write("EDGES" + System.getProperty("line.separator"));
		    for(Edge edge: edges){
		    	out.write(edge.getEdgeID() + " " + 
		    	edge.getSource().getID() + " " + 
		    	edge.getDestination().getID() + 
		    	System.getProperty("line.separator"));
		    }
		    System.out.println("File Edge Write Success!");

		    out.close();

		}catch(IOException E){
			System.out.println(E.toString());
		}
	}
	
	/**
	 * Method createFile.
	 * @param file File
	 * @return boolean
	 */
	private boolean createFile(File file){
		boolean createFile = true;
		//IF file does not exist create new file
		if(!file.exists()){
			try{
				file.createNewFile();
			}catch(IOException e){
				System.out.println(e.toString());
				
			}
		}
		else{
			return false;
		}
		return createFile;
	}
	private String loadDescription(String readLineDescription){
			if(readLineDescription.equals("")){
				System.out.println("The word was blank");
			}
			String arrayOfWordsFromDescription[];
			String finalDescription;
			StringBuilder buildDescription = new StringBuilder();
			
			arrayOfWordsFromDescription = readLineDescription.split(";");
			//Add the each word from description into the node description
			for(String word : arrayOfWordsFromDescription){
				buildDescription.append(word + " ");
			}
			finalDescription = buildDescription.toString();
			finalDescription.trim();
			return finalDescription;
	}
	private String saveDescription(String nodeDescription){
		String finalDescription = "";
		String arrayOfWordsFromDescription[];
		StringBuilder buildDescription = new StringBuilder();
		
		if(nodeDescription.equals("")){
			return finalDescription;
		}
		arrayOfWordsFromDescription = nodeDescription.split(" ");
		//Add the each word from description into the node description
		for(String word : arrayOfWordsFromDescription){
			buildDescription.append(word + ";");
		}
		finalDescription = buildDescription.toString();
		finalDescription.trim();
		return finalDescription;
	}
	/**
	 * Method calculateDistance.
	 * @param x1 double
	 * @param x2 double
	 * @param y1 double
	 * @param y2 double
	 * @return double
	 */
	private double calculateDistance(double x1, double x2, double y1, double y2)
	{
		return Math.sqrt(Math.pow(x2-x1, 2)+ Math.pow(y2 - y1, 2));
	}
}
