package de.unisaar.faphack.model.effects;

import de.unisaar.faphack.model.CharacterModifier;
import de.unisaar.faphack.model.MarshallingContext;
import de.unisaar.faphack.model.Storable;

/**
 * Effects are connected to character values and items. They are used to modify
 * a character or other effects.
 *
 * @author
 *
 */
public abstract class ModifyingEffect
implements Effect<CharacterModifier, CharacterModifier>, Storable {
  /**
   * The amount by which the health of the affected character is increased/dec
   * reased.
   */
  protected double health;

  /**
   * The amount by which the magic of the affected character is increased/decr
   * eased.
   */
  protected double magic;

  /**
   * The amount by which the magic of the affected character is increased/decr
   * eased.
   */
  protected double power;

  /**
   * Applies this Effect to the T argument and returns a value
   *
   * @param applyTo what this effect is applied to
   * @return possibly a value of type R
   */
  public abstract CharacterModifier apply(CharacterModifier applyTo);

  @Override
  public void marshal(MarshallingContext c) {
    c.write("health", health);
    c.write("magic", magic);
    c.write("power", power);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    health = c.readDouble("health");
    magic = c.readDouble("magic");
    power = c.readDouble("power");
  }
}
