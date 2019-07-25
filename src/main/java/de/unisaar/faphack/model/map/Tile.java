package de.unisaar.faphack.model.map;

import de.unisaar.faphack.model.Character;
import de.unisaar.faphack.model.*;

import java.util.Collections;
import java.util.List;

/**
 * @author
 *
 */
public abstract class Tile implements Storable, TraitOwner {

  protected int x;
  protected int y;

  /**
   * The room this tile is located in. This must not be null.
   */
  protected Room room;

  /**
   * The trait of this item.
   */
  protected String trait;

  public Tile() {

  }

  public Tile(int x, int y, Room room){
    this.room = room;
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public Room getRoom() {
    return room;
  }

  /**
   * Given the "vector" d, what's the tile you get in return? (Hint: ask the room)
   *
   * @return the next tile in direction d
   */
  public Tile getNextTile(Direction d) {
	return room.getNextTile(this, d);
  }

  /**
   * Can c proceed onto this tile?
   *
   * @return the current tile if you can move the Character c onto this tile, null
   *         otherwise
   */
  public abstract Tile willTake(Character c);
  
  /**
   *  Almost all tiles can not have items on them.
   */
  public List<Item> onTile() {
    return Collections.emptyList();
  }

  @Override
  public String getTrait() { return trait; }

  /**
   * Most tiles have no trap
   */
  public Trap hasTrap() {
    return null;
  }

  public void marshal(MarshallingContext c) {
    c.write("x", x);
    c.write("y", y);
    c.write("room", room);
    c.write("trait", trait);
  }

  public void unmarshal(MarshallingContext c) {
    // TODO please implement me!
  }

  public boolean removeItem(Wearable what) {
    return false;
  }

  public boolean addItem(Wearable what){
    return false;
  }

  /**
   *  Almost all tiles can not be occupied by a character.
   */
  public boolean isOccupied(Character c){
    return false;
  }
}
