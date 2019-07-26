package de.unisaar.faphack.model;

import de.unisaar.faphack.model.map.Tile;

/**
 * @author
 *
 */
public abstract class Item extends AbstractObservable<TraitedTileOccupier>
implements Storable, TraitedTileOccupier {
  /**
   * The Tile on which the item is placed. This is null if the Item is in the
   * inventory of a character.
   */
  protected Tile onTile;

  /**
   * The trait of this item.
   */
  protected String trait;

  /**
   * The Effect connected to the item.
   */
  private  CharacterModifier effect;

  public Item() {

  }

  public void marshal(MarshallingContext c) {
    c.write("onTile", onTile);
    c.write("trait", trait);
    c.write("effect", effect);
  }

  public void unmarshal(MarshallingContext c) {
    onTile = c.read("onTile");
    trait = c.readString("trait");
    effect = c.read("effect");
  }

  @Override
  public Tile getTile() { return onTile; }

  @Override
  public String getTrait() { return trait; }

  public CharacterModifier getCharacterModifier(){
    return effect;
  }
}
