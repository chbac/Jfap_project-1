package de.unisaar.faphack.model.map;

import de.unisaar.faphack.model.Character;
import de.unisaar.faphack.model.MarshallingContext;

/**
 * Walltiles are used to determine the arrangement of a room. They usually
 * define the outer borders of a room, but might also be used within a room to
 * separate areas.
 * __________________
 * |                |
 * |                |
 * |   _______      |
 * |  |_____  |     D
 * |________________|
 *
 * @author
 *
 */
public class WallTile extends Tile {
  /** 0 means infinitely strong, > 0 means: must apply at least this force */
  protected int destructible;

  public WallTile() {
    trait = WALL;
  }

  public WallTile(int x, int y, Room room){
    super(x, y, room);
    trait = WALL;
  }

  @Override
  public Tile willTake(Character c) {

    if(destructible != 0 && c.getPower() > destructible)
      return this;
    else
      return null;
  }

  @Override
  public void marshal(MarshallingContext c) {
    super.marshal(c);
    c.write("destructible", destructible);
    c.write("trait", trait);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    super.unmarshal(c);
  }
}
