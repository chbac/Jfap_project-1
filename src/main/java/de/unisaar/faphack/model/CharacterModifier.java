package de.unisaar.faphack.model;

public class CharacterModifier implements Storable {
  // what this modifier does to the various aspects of a character
  public int health;
  public int magic;
  public int power;

  private int howLong;

  public CharacterModifier(){}

  public CharacterModifier(int h, int m, int p, int hl) {
    health = h;
    magic = m;
    power = p;
    howLong = hl;
  }

  /**
   * Apply the changes of this modifier to c, but only if howLong is not zero
   */
  public boolean applyTo(Character c) {
	/* check beforehand if the effect is in active effects */
	if (c.activeEffects.contains(this)) {
		return false;
	} else {
		c.activeEffects.add(this);
		return true;
	}
  }

  public int howLong() {
    return howLong;
  }

  @Override
  public void marshal(MarshallingContext c) {
	  c.write("health", health);
	  c.write("magic", magic);
	  c.write("power", power);
	  c.write("howLong", howLong);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
	  health = c.readInt("health");
	  magic = c.readInt("magic");
	  power = c.readInt("power");
	  howLong = c.readInt("howLong");
  }
}
