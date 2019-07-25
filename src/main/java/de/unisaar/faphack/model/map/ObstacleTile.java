package de.unisaar.faphack.model.map;

import de.unisaar.faphack.model.MarshallingContext;

/**
 * An obstacle tile could have different traits, so we have to store it
 */
public class ObstacleTile extends WallTile {

  /** default trait: boulder */
  public ObstacleTile() {
    trait = BOULDER;
  }

  @Override
  public void marshal(MarshallingContext c) {
    super.marshal(c);
    c.write("trait", trait);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    super.unmarshal(c);
  }
}
