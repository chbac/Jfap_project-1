package de.unisaar.faphack.model;

import de.unisaar.faphack.model.map.FloorTile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WearableTest {

  @Test
  void pickUp() {
    Wearable item = TestUtils.createWearable(1,false);
    item.onTile = new FloorTile();
    Character character = TestUtils.createBaseCharacter("Foo", 1, 1);
    item.pickUp(character);
    assertEquals(character, item.character);
    assertNull(item.onTile);
  }

  @Test
  void drop() {
    Wearable item = TestUtils.createWearable(1,false);
    item.character =  TestUtils.createBaseCharacter("Foo", 1, 1);
    item.onTile =  null;
    FloorTile t = new FloorTile();
    item.drop(t);
    assertEquals(t, item.onTile);
    assertNull(item.character);
  }
}