package de.unisaar.faphack.model.effects;

import de.unisaar.faphack.model.Direction;
import de.unisaar.faphack.model.Character;
import de.unisaar.faphack.model.map.Tile;
import de.unisaar.faphack.model.map.WallTile;

public class MoveEffect implements Effect<Character, Boolean> {
  private Direction dir;

  public MoveEffect(Direction d) {
    dir = d;
  }

  /**
   * tries to move the character into the given direction.
   *
   * @param c the character to move
   * @param d the dirction to move the character (max one tile)
   * @return true if successful, false otherwise
   */
  public Boolean apply(Character c) {
	if (c.getTile().getNextTile(dir) != null 
			&& c.getTile().willTake(c) != null 
			&& !(c.getTile().getNextTile(dir) instanceof WallTile)) {
		c.move(c.getTile().getNextTile(dir));
		return true;
	}
	return false;
	
    
  }

}
