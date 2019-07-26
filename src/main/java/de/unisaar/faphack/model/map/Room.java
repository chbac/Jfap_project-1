package de.unisaar.faphack.model.map;

import java.util.List;

import de.unisaar.faphack.model.Character;
import de.unisaar.faphack.model.Direction;
import de.unisaar.faphack.model.MarshallingContext;
import de.unisaar.faphack.model.Storable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 *
 */
public class Room implements Storable {

  /** The world this room belongs to */
  World w;

  /** The Characters that currently are in this room */
  private List<Character> inhabitants = new ArrayList<>();

  /**
   * A 2-dimensional Array defining the layout of the tiles in the room.
   */
  private Tile[][] tiles;

  public Room(){}

  /**
   /**
   * This method returns a tile determined by the specified tile <code> t </ code> and the <code> direction </ code> d.
   * If the path between the specified tile and the derived tile is blocked by a wall,
   * the wall tile is returned.
   *
   * HINT: use the computeDDA to compute the path
   *
   * @param t the start tile
   * @param d the direction to follow
   * @return
   */
  public Tile getNextTile(Tile t, Direction d) {
	int xNew;
	int yNew;
	
	// x
	if (t.x + d.x < 0) {
		xNew = 1;
	} else if (t.x + d.x >= tiles.length) {
		xNew = tiles.length - 1;
	} else {
		xNew = t.x + d.x;
	}
	
	// y
	if (t.y + d.y < 0) {
		yNew = 1;
	} else if (t.y + d.y >= tiles[xNew].length) {
		yNew = tiles[xNew].length - 1;
	} else {
		yNew = t.y + d.y;
	}

	return tiles[xNew][yNew];
	
	//	if (tiles.length >= xNew && xNew >= 0) {
	//		if (tiles[xNew].length >= yNew && yNew >= 0) {
	//			return tiles[xNew][yNew];
	//		}
	//	}
	//    return t;
  }


  private List<Tile> computeDDA(Tile t, Direction d){
    List<Tile> path = new ArrayList<>();

    // Calculate number of steps
    int steps = Math.abs(d.x) > Math.abs(d.y) ? Math.abs(d.x) : Math.abs(d.y);


    // Calculate increments
    double xIncrement = d.x / (float) steps;
    double yIncrement = d.y / (float) steps;

    // Compute points
    double x = t.x;
    double y = t.y;
    path.add(tiles[(int) x][((int) y)]);
    for (int i = 0; i < steps; i++) {
      x += xIncrement;
      y += yIncrement;
      if (x >= tiles.length || y >= tiles[0].length)
        break;
      Tile tile = tiles[(int) Math.round(x)][(int) Math.round(y)];
      path.add(tile);
    }
    return path;
  }

  public Tile[][] getTiles() {
    return tiles;
  }

  public List<Character> getInhabitants() {
    return inhabitants;
  }

  @Override
  public void marshal(MarshallingContext c) {
    c.write("world", w);
    c.write("inhabitants", inhabitants);
    c.write("tiles", tiles);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    w = c.read("world");
    c.readAll("inhabitants", inhabitants);
    tiles = c.readBoard("tiles");
  }
  
  private boolean containsTile(Tile t) {
	  for (Tile[] tl : tiles) {
		  for (Tile tile : tl) {
			  if (tile.equals(t)) return true;
		  }
	  }
	  return false;
  }
}
