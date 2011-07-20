package gcm.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import gcm.util.GlobalConstants;

/*
 * TODO-jstev:
 * 
 * put stuff in the listeners to update the grid's components and appearance
 * 		and also disallow moving outside of the grid and > 1 component per location and so on
 * 			create a new rectangle with the bounds and use the contains method
 * 		eg, for deletion, take the component and use a map to get the gridnode and then delete its component
 * 
 * make sure this won't break if there's an empty spot when loading from a file or otherwise
 * 		(ie, in createGrid)
 * 
 * you shouldn't be able to move cells in the schematic during analysis view
 * 		perhaps a toggle to know that it's analysis view? (paramsOnly is this toggle, i think)
 * 
 */


/**
 * Contains all of the methods relevant to a grid
 * This is an explict, formal grid which is principally used for tracking diffusible species
 * and the locations of compartments/components/cells in a formal population
 * 
 * @author jason (sarbruis@gmail.com)
 * 
 */
public class Grid {
	
	//---------------
	//CLASS VARIABLES
	//---------------
	
	//the actual grid -- a 2d arraylist of gridnodes
	private ArrayList<ArrayList<GridNode>> grid;
	
	//map of gridnodes and their corresponding rectangles
	private HashMap<Rectangle, GridNode> rectToNodeMap;
	
	//map of x,y points and the grid node they're located in
	private HashMap<Point, GridNode> pointToNodeMap;
	
	//components on the grid
	private HashMap<String, Properties> components;
	
	//map of row, col grid locations to the component at that location
	private HashMap<Point, Map.Entry<String, Properties>> locToComponentMap;
	
	private int numRows, numCols;
	private int verticalOffset;
	private int padding; //padding for the grid rectangles
	private int gridWidth;
	private int gridHeight;
	private boolean enabled;
	private boolean mouseClicked;
	
	//true means spatial grid; false means cell population grid
	//this is used when editing a grid, as only compartments can be added to a cell population grid
	private boolean gridSpatial;
	
	private Rectangle gridBounds;
	private Point mouseClickLocation;
	private Point mouseLocation;
	
	//-------------
	//CLASS METHODS
	//-------------
	
	//PUBLIC
	
	/**
	 * constructor to create a row x col grid of GridNodes
	 */
	public Grid(int rows, int cols, HashMap<String, Properties> components) {
				
		this();
		
		createGrid(rows, cols, components);
	}
	
	/**
	 * default constructor
	 */
	public Grid() {
		
		gridSpatial = false;
		enabled = false;
		verticalOffset = 0;
		numRows = 0;
		numCols = 0;
		padding = 30;
		gridWidth = GlobalConstants.DEFAULT_COMPONENT_WIDTH + padding;
		gridHeight = GlobalConstants.DEFAULT_COMPONENT_HEIGHT + padding;
		
		gridBounds = new Rectangle();
		mouseClickLocation = new Point();
		mouseLocation = new Point();
		grid = new ArrayList<ArrayList<GridNode>>();
		rectToNodeMap = new HashMap<Rectangle, GridNode>();
		pointToNodeMap = new HashMap<Point, GridNode>();
		locToComponentMap = new HashMap<Point, Map.Entry<String, Properties>>();
	}
	
	/**
	 * creates a 2d arraylist of GridNodes of rows x cols
	 * usually called from GridPanel
	 * 
	 * @param rows number of rows in the grid
	 * @param cols number of columns in the grid
	 * @param components the components that are located on the grid
	 */
	public void createGrid(int rows, int cols, HashMap<String, Properties> components) {
		
		//if the grid size is 0 by 0, don't make it
		if (rows == 0 && cols == 0) {
			
			enabled = false; //should be false already, but whatever
			return;
		}
		
		enabled = true;
		numRows = rows;
		numCols = cols;
		
		this.components = components;
		
		for(int i = 0; i < numRows; ++i) {
			
			grid.add(new ArrayList<GridNode>(numCols));
			
			for(int j = 0; j < numCols; ++j) {
				
				grid.get(i).add(new GridNode());
				grid.get(i).get(j).setRow(i);
				grid.get(i).get(j).setCol(j);
			}
		}
		
		updateGridRectangles();
		updateNodeMaps();
		updateLocToComponentMap();
		putComponentsOntoGrid();
	}
	
	/**
	 * draws the grid
	 * usually called from Schematic
	 * 
	 * @param g Graphics object (from the Schematic JPanel)
	 */
	public void drawGrid(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
		
		boolean selectionOff = false;
		
		//if the user's mouse is within the grid bounds
		if (gridBounds.contains(mouseLocation)) {
			
			//draw a hover-rectangle over the grid location
			hoverGridLocation(g);
			
			//if the user has clicked, select/de-select that location
			if (mouseClicked) selectGridLocation(g);
		}
		//if the user clicks out of bounds
		//de-select all grid locations
		else {
			if (mouseClicked) selectionOff = true;
		}
		
		//draw the actual grid and selection boxes for selected nodes
		for (int row = 0; row < numRows; ++row) {
			for (int col = 0; col < numCols; ++col) {
				
				GridNode node = grid.get(row).get(col);
				Rectangle rect = node.getRectangle();
				
				g2.drawRect(rect.x, rect.y, rect.width, rect.height);
				
				if (selectionOff) node.setSelected(false);
				
				if (node.isSelected())
					drawGridSelectionBox(g, rect);
				
				//some debug stuff to draw onto the grid
				g2.drawString(Boolean.toString(node.isOccupied()), rect.x, rect.y);
				
				if (node.component == null) {
					g2.drawString("null", rect.x+40, rect.y);
				}
				else g2.drawString(node.getComponent().getKey(), rect.x+40, rect.y);
			}
		}
	}
	
	/**
	 * this draws a box around the grid location when the user hovers over it
	 * with the mouse or clicks it, which is used for grid location selection
	 * 
	 * @param g Graphics object from the Schematic JPanel
	 * @param row the row of the selected location
	 * @param col the column of the selected location
	 */
	public void drawGridSelectionBox(Graphics g, Rectangle rect) {
		
		g.setColor(Color.blue);
		
		//don't change the original rectangle; make a copy
		Rectangle localRect = new Rectangle(rect);
		
		//have to loop to get a thickness > 1
		for (int i = 0; i < 3; ++i) {
			
			localRect.x += 1; localRect.y += 1; localRect.width -= 2; localRect.height -= 2;
			g.drawRect(localRect.x, localRect.y, localRect.width, localRect.height);
		}
		
		g.setColor(Color.black);
	}	
	
	/**
	 * @return the location-to-component map
	 */
	public HashMap<Point, Map.Entry<String, Properties>> getLocToComponentMap() {
		
		updateLocToComponentMap();
		
		return locToComponentMap;
	}
	
	/**
	 * empties a node on the grid based on a component ID
	 * 
	 * @param compID
	 */
	public void clearNode(String compID) {
		
		GridNode node = getNodeFromCompID(compID);
		
		if (node.getComponent() != null)
			node.clear();
	}
	
	/**
	 * tries to move a node given the component ID and the center x,y coords
	 * of where the user is trying to move the component
	 * 
	 * @param compID id of the component
	 * @param centerX center x coord of where the component is trying to be moved to
	 * @param centerY center y coord of where the component is trying to be moved to
	 */
	public boolean moveNode(String compID, double centerX, double centerY) {
		
		Point moveToPoint = new Point((int)centerX, (int)(centerY + verticalOffset));
		
		//if the user is trying to move the component to a place within the grid
		//then pay attention
		if (gridBounds.contains(moveToPoint)) {
			
			GridNode node = pointToNodeMap.get(moveToPoint);
			
			//this shouldn't be null because we're on the grid, but just in case . . .
			if (node != null) {
				
				//make sure there isn't a component in the location
				if (node.isOccupied() == false) {
					
					//clear the old component's spot
					clearNode(compID);
					
					//NOTE:
					//this is a horrible hack to get a Map.Entry<String, Property>
					//i didn't realize it was an interface and not instantiable
					//TODO-jstev: i'll probably make my own pair class for a single component
					HashMap<String, Properties> tempMap = new HashMap<String, Properties>();
					tempMap.put(compID, components.get(compID));
					
					Map.Entry<String, Properties> compo = tempMap.entrySet().iterator().next();
					
					//put the component in its new home
					node.setComponent(compo);
					node.setOccupied(true);
					
					//update the row/col properties of the component
					Properties props = compo.getValue();
					
					props.setProperty("row", Integer.toString(node.getRow()+1));
					props.setProperty("col", Integer.toString(node.getCol()+1));
					
					//update the location to component hash map
					//as the component and their locations have changed
					updateLocToComponentMap();
					
					return true;
				}
				else return false;
			}
			else return false;
		}
		else return false;
		
	}
	
	/**
	 * finds and returns the snap rectangle from the component id passed in
	 * @param compID component id
	 * @return the snap rectangle
	 */
	public Rectangle getSnapRectangleFromCompID(String compID) {
		
		return getNodeFromCompID(compID).getSnapRectangle();
	}
	
	/**
	 * returns where or not the user clicked on an occupied grid location
	 * @param mouseX where the user clicked
	 * @param mouseY where the user clicked
	 * @return true if the grid location is occupied, false if not
	 */
	public boolean getOccupancyFromPoint(Point point) {
		
		point.y += verticalOffset;		
		GridNode node = pointToNodeMap.get(point);
		
		if (node != null)
			return node.isOccupied();
		
		//return true because it's easier to consider outside the grid as occupied
		//so nothing can ever get placed there
		else return true;
	}
	
	/**
	 * returns the grid row corresponding to a point
	 * 
	 * @param point
	 * @return the grid row corresponding to the point
	 */
	public int getRowFromPoint(Point point) {
		
		point.y += verticalOffset;		
		return pointToNodeMap.get(point).getRow();
	}
	
	/**
	 * returns the grid column corresponding to a point
	 * 
	 * @param point
	 * @return the grid column corresponding to the point
	 */
	public int getColFromPoint(Point point) {
		
		point.y += verticalOffset;		
		return pointToNodeMap.get(point).getCol();
	}
	
	/**
	 * takes the gcm's components and resets the grid with those components
	 */
	public void refreshComponents(HashMap<String, Properties> gcmComponents) {
		
		components = gcmComponents;
		
		putComponentsOntoGrid();
		
		//update the location to component hash map
		//as the component and their locations have changed
		updateLocToComponentMap();
		
		updateNodeMaps();
	}
	
	
	//PRIVATE
	
	/**
	 * sets the rectangles/bounds for each node
	 * also sets the bounds for the entire grid
	 */
	private void updateGridRectangles() {
		
		int start = padding/2;
		int currX = start;
		int currY = start + verticalOffset;
		
		//create 2d arraylist of GridNode objects
		//give them a location and rectangle bounds
		for (int row = 0; row < numRows; ++row) {
			for (int col = 0; col < numCols; ++col) {
								
				Rectangle rect = new Rectangle(currX, currY, gridWidth, gridHeight);
				
				grid.get(row).get(col).setRectangle(rect);
				
				currX += gridWidth;
			}
			
			currY += gridHeight;
			currX = start;
		}
		
		//sets the total grid bounds
		int outerX = start + gridWidth * numCols;
		int outerY = start + gridHeight * numRows + verticalOffset;
		
		gridBounds.setBounds(start, start, outerX-start, outerY-start);
	}
	
	/**
	 * takes the component list and uses their row/col properties to
	 * place them onto the grid
	 * 
	 * i think this should only be run once on grid creation
	 */
	private void putComponentsOntoGrid() {

		Iterator<Map.Entry<String, Properties>> iter = components.entrySet().iterator();
		
		//iterate through the components to get the x/y coords of its top left vertex
		//using these coords, map it to the grid
		while(iter.hasNext()) {
			
			Map.Entry<String, Properties> entry = (Map.Entry<String, Properties>)iter.next();
			
			//find the row and col from the component's properties
			//use these to put the component into the correct grid location
			Properties props = entry.getValue();
			
			String gcm = props.getProperty("gcm");
			int row = Integer.valueOf(props.getProperty("row"));
			int col = Integer.valueOf(props.getProperty("col"));
			boolean compartment = Boolean.parseBoolean(props.getProperty("compartment"));
						
			//if adding blank components, don't set the component
			if (gcm.equals("none"))
				grid.get(row-1).get(col-1).setOccupied(false);
			else {
				grid.get(row-1).get(col-1).setComponent(entry);
				grid.get(row-1).get(col-1).setOccupied(true);
				
				//if something isn't a compartment, we must be on a spatial grid
				if (compartment == false)
					setGridSpatial(true);
			}
		}
		
	}

	/**
	 * creates a hash map of corresponding rectangles and nodes
	 * this allows easy access of nodes from rectangle coordinates
	 * 
	 * creates a hash map of the corresponding x,y points and nodes
	 * this allows easy access to nodes from the location coordinates
	 */
	private void updateNodeMaps() {
		
		rectToNodeMap.clear();
		pointToNodeMap.clear();
		
		//loop through the rows and columns
		//find the grid node and rectangle then make it a map entry
		for (int row = 0; row < numRows; ++row) {
			for (int col = 0; col < numCols; ++col) {
		
				rectToNodeMap.put(grid.get(row).get(col).getRectangle(), grid.get(row).get(col));				
			}
		}
		
		int startX = gridBounds.x;
		int startY = gridBounds.y;
		int endX = gridBounds.x + gridBounds.width;
		int endY = gridBounds.y + gridBounds.height;
		
		//loop through every point on the grid
		//find the rectangle then the grid node then make it a map entry
		for (int x = startX; x <= endX; ++x) {
			for (int y = startY; y <= endY; ++y) {
				
				boolean nextPoint = false;
				Point point = new Point(x, y);
				
				//loop through every row and column
				//check this location's rectangle against the x,y point
				//if it contains it, add that to the map
				for (int row = 0; row < numRows; ++row) {
					for (int col = 0; col < numCols; ++col) {
						
						Rectangle rect = grid.get(row).get(col).getRectangle();
						
						if (rect.contains(point)) {
							pointToNodeMap.put(point, rectToNodeMap.get(rect));
							nextPoint = true;
							continue;
						}
					}
					
					//if we've found the correct location, move on
					if (nextPoint == true) continue;
				}
			}
		}
	}
	
	/**
	 * based on the mouse location, draws a box around the grid location
	 * that the user is hovering over
	 * 
	 * @param location the mouse location x,y coordinates
	 */
	private void hoverGridLocation(Graphics g) {
		
		GridNode hoveredNode = pointToNodeMap.get(mouseLocation);	
		
		if (hoveredNode != null) {
			
			hoveredNode.setHover(true);
			drawGridSelectionBox(g, hoveredNode.getRectangle());
		}
	}
	
	/**
	 * based on the mouse-click location, selects the grid location
	 * also draws a selection box
	 * 
	 * @param location the mouse-click x,y coordinates
	 */
	private void selectGridLocation(Graphics g) {
		
		GridNode hoveredNode = pointToNodeMap.get(mouseClickLocation);
		
		//select or de-select the grid location
		if (hoveredNode != null)
			hoveredNode.setSelected(hoveredNode.isSelected() == true ? false : true);
		
		//this click's action has been performed, so set it to false until
		//the user clicks again
		mouseClicked = false;
	}
	
	/**
	 * creates a hash map for easy access of the component at a specified grid location
	 * with the component you can get its name and properties
	 * used for diffusion reaction printing in printDiffusion
	 */
	private void updateLocToComponentMap() {
		
		locToComponentMap.clear();
				
		Iterator<Map.Entry<String, Properties>> iter = components.entrySet().iterator();
		
		//iterate through the components to get the number of rows and cols
		while(iter.hasNext()) {
			
			Map.Entry<String, Properties> compo = (Map.Entry<String, Properties>)iter.next();
			
			//find the row and col from the component's properties
			Properties props = compo.getValue();
						
			int row = Integer.parseInt(props.getProperty("row"));
			int col = Integer.parseInt(props.getProperty("col"));
			
			locToComponentMap.put(new Point(row, col), compo);
		}
	}
	
	/**
	 * finds and returns the grid node with the component id passed in
	 * @param compID
	 * @return the grid node with the component id
	 */
	private GridNode getNodeFromCompID(String compID) {
		
		//loop through all of the grid nodes
		//find the grid with that compartment ID
		for (int row = 0; row < numRows; ++row) {
			for (int col = 0; col < numCols; ++col) {
				
				GridNode node = grid.get(row).get(col);
				
				if (node.getComponent() != null && node.getComponent().getKey().equals(compID))
					return node;
			}
		}
		
		//if it's not found, send back a null pointer
		return null;
	}
	
	
	//BORING GET/SET METHODS
	
	/**
	 * @param enabled whether or not the grid is enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return whether or not the grid is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * 
	 * @return the components on the grid
	 */
	public HashMap<String, Properties> getComponents() {
		return components;
	}

	/**
	 * @param components the components on the grid
	 */
	public void setComponents(HashMap<String, Properties> components) {
		this.components = components;	
	}

	/**
	 * used for when there's a toolbar
	 * @return the number of pixels from the top of the jpanel
	 */
	public int getVerticalOffset() {
		return verticalOffset;
	}

	/**
	 * used for when there's a toolbar
	 * @param verticalOffset pixels from the top of the jpanel
	 */
	public void setVerticalOffset(int verticalOffset) {
		
		this.verticalOffset = verticalOffset;
		
		//change the grid's rectangle locations based on this offset
		updateGridRectangles();
		updateNodeMaps();
	}

	/**
	 * @return the numRows
	 */
	public int getNumRows() {
		return numRows;
	}

	/**
	 * @return the numCols
	 */
	public int getNumCols() {
		return numCols;
	}
	
	/**
	 * @param mouseClickLocation the mouseClickLocation to set
	 */
	public void setMouseClickLocation(Point mouseClickLocation) {
		
		this.mouseClickLocation = new Point(mouseClickLocation.x, mouseClickLocation.y + verticalOffset);
		mouseClicked = true;
	}

	/**
	 * @return the mouseClickLocation
	 */
	public Point getMouseClickLocation() {
		return mouseClickLocation;
	}

	/**
	 * @param mouseLocation the mouseLocation to set
	 */
	public void setMouseLocation(Point mouseLocation) {
		
		//the mouse location is changed because of the vertical offset
		this.mouseLocation = new Point(mouseLocation.x, mouseLocation.y + verticalOffset);
	}

	/**
	 * @return the mouseLocation
	 */
	public Point getMouseLocation() {
		return mouseLocation;
	}
	
	/**
	 * set where it's a spatial or cell population grid
	 * 
	 * @param gridSpatial true means spatial; false means population
	 */
	public void setGridSpatial(boolean gridSpatial) {
		this.gridSpatial = gridSpatial;
	}
	
	/**
	 * @return whether it's a spatial or cell population grid
	 */
	public boolean getGridSpatial() {
		return this.gridSpatial;
	}
	
	/**
	 * @return padding pixels between grid components
	 */
	public int getPadding() {
		return padding;
	}
	
	
	//--------------
	//GRIDNODE CLASS
	//--------------


	/**
	 * contains all of the information for a grid node
	 * each location on the grid is a grid node
	 */
	private class GridNode {
		
		//---------------
		//CLASS VARIABLES
		//---------------
		
		private boolean occupied; //has a component or not
		private Map.Entry<String, Properties> component; //component in node
		private int row, col; //location on the grid (not x,y coords)
		private Rectangle snapRectangle; //x,y coordinate of the top-left of the component (not grid) rectangle
		private boolean isCompartment; //component is a compartment or not
		private boolean selected; //is the grid location selected or not
		private boolean hover; //is the grid location being hovered over or not
		private Rectangle gridRectangle; //contains the grid coordinates and size
		
		//-------------
		//CLASS METHODS
		//-------------
		
		/**
		 * default constructor that initializes variables
		 */
		public GridNode() {
			
			occupied = false;
			isCompartment = false;
			component = null;
			selected = false;
			hover = false;
			snapRectangle = new Rectangle(0, 0, 0, 0);
			gridRectangle = new Rectangle(0, 0, 0, 0);
		}
		

		//PUBLIC
		
		/**
		 * deletes the node's component's gcm association, freeing it to be re-set to another
		 */
		public void clear() {
			
			occupied = false;
			component = null;
			isCompartment = false;
		}
		
		
		//BORING GET/SET METHODS
		
		/**
		 * 
		 * @return the occupancy status
		 */
		public boolean isOccupied() {
			return occupied;
		}
		
		/**
		 * 
		 * @param occupancy whether gridNode is occupied or not
		 */
		public void setOccupied(boolean occupancy) {
			occupied = occupancy;
		}

		/**
		 * @param col the col to set
		 */
		public void setCol(int col) {
			this.col = col;
		}

		/**
		 * @return the col
		 */
		public int getCol() {
			return col;
		}

		/**
		 * @param row the row to set
		 */
		public void setRow(int row) {
			this.row = row;
		}

		/**
		 * @return the row
		 */
		public int getRow() {
			return row;
		}

		/**
		 * also sets boolean about whether the component is a compartment
		 * @param component the component to set
		 */
		public void setComponent(Map.Entry<String, Properties> component) {
			
			this.component = null;
			this.component = component;
			isCompartment = Boolean.getBoolean(component.getValue().getProperty("compartment"));
		}
		
		/**
		 * @return the component
		 */
		public Map.Entry<String, Properties> getComponent() {
			return component;
		}
	
		/**
		 * @param rectangle the rectangle for the node
		 */
		public void setRectangle(Rectangle rectangle) {
			this.gridRectangle = rectangle;
			
			//set the snap rectangle based on this rectangle
			setSnapRectangle(new Rectangle(
					(int)(rectangle.x + padding/2), 
					(int)(rectangle.y + padding/2),
					gridWidth - padding,
					gridHeight - padding));
		}
		
		/**
		 * 
		 * @return the grid rectangle for the node
		 */
		public Rectangle getRectangle() {
			return this.gridRectangle;
		}
	
		/**
		 * pair of points specifying which location the component should "snap" to
		 * ie, a component is only allowed to have its top-left point at this snap point
		 * 
		 * used by the schematic to move cells around the graph
		 * 
		 * @param x
		 * @param y
		 */
		public void setSnapRectangle(Rectangle r) {
			
			//because this is passed back to the Schematic
			//change the y coordinate to un-offset from the toolbar
			r.y -= verticalOffset;
			snapRectangle = r;
		}
		
		/**
		 * used by the schematic to move cells around the graph
		 * 
		 * @return the snap rectangle
		 */
		public Rectangle getSnapRectangle() {
			
			return snapRectangle;
		}
		
		/**
		 * @return the isCompartment
		 */
		public boolean isCompartment() {
			return isCompartment;
		}

		/**
		 * @param isCompartment the isCompartment to set
		 */
		public void setCompartment(boolean isCompartment) {
			this.isCompartment = isCompartment;
		}

		/**
		 * @param selected the selected status
		 */
		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		/**
		 * @return the selected status
		 */
		public boolean isSelected() {
			return selected;
		}
		
		/**
		 * @param selected the hover status
		 */
		public void setHover(boolean hover) {
			this.hover = hover;
		}

		/**
		 * @return the hover status
		 */
		public boolean isHover() {
			return hover;
		}
	}
	
}
