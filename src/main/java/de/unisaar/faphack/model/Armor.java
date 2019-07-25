package de.unisaar.faphack.model;

import de.unisaar.faphack.model.effects.ModifyingEffect;

public class Armor extends Wearable {

  private ModifyingEffect modifyingEffect;



  public Armor(){

  }

  /**
   * The Effect connected to the item is always NULL.
   * The armor has a ModifyingEffect instead.
   */
   @Override
  public CharacterModifier getCharacterModifier(){
    return null;
  }

  public  ModifyingEffect getModifyingEffect(){
    return modifyingEffect;
  }

  @Override
  public void marshal(MarshallingContext c) {
    super.marshal(c);
    c.write("modifyingEffect", modifyingEffect);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    super.unmarshal(c);
    modifyingEffect = c.read("modifyingEffect");
  }
}
