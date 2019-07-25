package de.unisaar.faphack.model;

import de.unisaar.faphack.model.map.Tile;

/**
 * Wearables are Items that can be carried by a Character. These include armor,
 * weapons, food, potions, key and others.
 *
 * @author
 *
 */
public class Wearable extends Item {
  /**
   * The weight of the item.
   */
  protected int weight;

  /**
   *
   */
  protected boolean isWeapon;

  /**
   * The character who carries this item. This is null if the Item is placed on a
   * Tile.
   */
  protected Character character;

  public Wearable() {

  }

  @Override
  public void marshal(MarshallingContext c) {
	super.marshal(c);
	c.write("weight", weight);
	c.write("character", character);
	c.write("isWeapon", isWeapon ? 1 : 0);
//	("isWeapon", isWeapon ? 1 : 0);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
	super.unmarshal(c);
	weight = c.readInt("weight");
	character = c.read("character");
	isWeapon = (c.readInt("isWeapon") != 0);
  }

  public void pickUp(Character c) {
    c.pickUp(this);
  }

  public void drop(Tile t) {
    t.addItem(this);
  }
}
