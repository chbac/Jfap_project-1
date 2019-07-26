package de.unisaar.faphack.model;

import de.unisaar.faphack.model.map.Tile;
import de.unisaar.faphack.model.map.WallTile;
import de.unisaar.faphack.model.map.World;

import java.util.List;

/**
 * @author
 *
 */
public class Game implements Storable {
  private World world;
  private Character protagonist;

  public Game() {

  }

  /**
  * return the protagonist of this game
  */
  public Character getProtagonist() {
    return protagonist;
  }

  /**
   * tries to move the character into the given direction.
   * @param whom
   * @param direction
   * @return boolean
   */
  public boolean move(Character whom, Direction direction) {
	/* only move if there exists a tile in the direction, and that tile is not a walltile */
	if (whom.getTile().getNextTile(direction) != null
		&& !(whom.getTile().getNextTile(direction) instanceof WallTile)) {
			whom.move(whom.getTile().getNextTile(direction));
			return true;
		}
	return false;
  }

  /**
   * The character rests, i.e. it moves with direction (0,0) and its power increases by 5
  */
  public boolean rest(Character whom){
	whom.rest();
    return true;
  }

  /**
   *
   * @param who
   * @param direction
   * @return List<Item>
   */
  public List<Item> listItems(Character who, Direction direction) {
    Tile current = who.getTile();
    Tile next = current.getNextTile(direction);
    return next.onTile();
  }

  /**
   * Let a character pickup the given item
   * @param who the character
   * @param item the item to be picked up
   * @return boolean <code>true</code> if the character managed to pickup the item, <code>false</code> otherwise
   */
  public boolean pickUp(Character who, Item what) {
	/* of course, the item has to be a wearable */
	if (what instanceof Wearable) {
		return who.pickUp((Wearable)what);
	}
	return false;
  }

  /**
   * Removes an item from the given characters inventory and places it on the tile
   * @param who the character performing the action
   * @param what the item to be removed
   * @return <code>true</code> if the action was successful, <code>false</code> otherwise
   */
  public boolean drop(Character who, Wearable what){
    return who.dropItem(what);
  }

  /**
   * Equips the given Wearable as active Weapon or armor depending
   *
   * @param who the character performing the action
   * @param what the item to be equipped
   * @return <code>true</code> the action was successful, <code>false</code> otherwise
   */
  public boolean equip(Character who, Wearable what){
	return who.equipItem(what);
  }

  @Override
  public void marshal(MarshallingContext c) {
    c.write("world", world);
    c.write("protagonist", protagonist);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    world = (World) c.read("world");
    protagonist = (Character) c.read("protagonist");
  }

  public World getWorld() {
    return world;
  }

  /** Add the game's protagonist to a random floor tile in the first room */
  public void setProtagonist(Character prot) {
    protagonist = prot;
  }

  /** get the game's protagonist */
  public Character getProtagonist(Character prot) {
    return protagonist;
  }
}
