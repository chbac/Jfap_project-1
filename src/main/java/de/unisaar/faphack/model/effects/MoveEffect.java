package de.unisaar.faphack.model.effects;

import de.unisaar.faphack.model.Direction;
import de.unisaar.faphack.model.Character;
import de.unisaar.faphack.model.map.Tile;
import de.unisaar.faphack.model.map.WallTile;

public class MoveEffect implements Effect<Character, Boolean> {
  private Direction dir;

  public MoveEffect(Direction d) throws IllegalArgumentException {
	if (Math.abs(d.x) > 1 || Math.abs(d.y) > 1) {
		throw new IllegalArgumentException("bla");
	}
	dir = d;
  }

  /**
   * @param c the character to move
   * @return true if successful, false otherwise
   */
  public Boolean apply(Character c) {
	if (c.getTile().willTake(c) != null 
			&& !(c.getTile().getNextTile(dir) instanceof WallTile)) {
		System.out.print("here!");
		c.move(c.getTile().getNextTile(dir));
		return true;
	}
	return false;
	
    
  }

}
