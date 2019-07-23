package de.unisaar.faphack.model;

import de.unisaar.faphack.model.map.Room;
import de.unisaar.faphack.model.map.Tile;
import org.junit.jupiter.api.Test;

import static de.unisaar.faphack.model.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class CharacterTest {

  /**
   * Move a character which is placed on tile 1x1 in a square 4 x 4 room .
   * 1: Direction (0,1) to the right
   * 2: Direction (0,-1) to the left ( now we should be back where we started)
   * 3. Direction (-1, 1) down (this should have now effect as there is a wall )
   * 4. Direction (0,2) exception expected as only directions with values from -1 to 1 are allowed in MoveEffects
   */
  @Test
  void move() {
    Room room = TestUtils.createSimpleRoom(4,4,1);
    Tile[][] tiles = room.getTiles();
    Character testObject = createBaseCharacter("Foo", 2, 2);
    addCharacter(room, 1, 1, testObject);
    testObject.move(room.getNextTile(testObject.tile, new Direction(0,1 )));
    assertEquals(tiles[1][2], testObject.tile);
    testObject.move(room.getNextTile(testObject.tile, new Direction(0,-1 )));
    assertEquals(tiles[1][1], testObject.tile);
  }


  /**
   * Let a character which is placed on tile 1x1 in a square 4 x 4 room pickup some items.
   * 1. Item on tile 1x2 but character on tile 1x1, return false
   * 2. Character and item on the same tile (return true and item is in the characters inventory)
   * 3. Another item, this time the maxWeight limit is exceeded, return false
   */
  @Test
  void pickUp() {
    Room room = TestUtils.createSimpleRoom(8,8,1);
    Tile[][] tiles = room.getTiles();
    Character testObject = createBaseCharacter("Foo", 2, 2);
    addCharacter(room, 1, 1, testObject);
    Wearable item1 = createWearable(2, false);
    Wearable item2 = createWearable(2, false);
    placeItemsInRoom(room, 1,2,item1, item2);
    // the item is not on the field the character is
    assertFalse(testObject.pickUp(item1));
    // Wearable at [1][2]: picking this up should work
    placeCharacter(testObject, tiles[1][2]);
    assertTrue(testObject.pickUp(item1));
    assertTrue(testObject.items.contains(item1));
    // the item is too heavy for the character
    assertFalse(testObject.pickUp(item2));
  }


  /**
   * Apply an attack that decreases health by 10, magic by 15 and power by 1 to a character with
   *  100 health, 50 magic and 2 power.
   *
   * 1. Character wears no armor,
   *      the character's values after the attack health: 90, magic: 35 and power: 1
   * 2. Character wears an armor reducing health damage by 50% and blocking all other damage (It's a really good armor!),
   *      character's values after the attack health: 85, magic: 35, power: 1
   *
   */
  @Test
  void applyAttack() {
    // create a character without any armor
    Character testObject = TestUtils.createBaseCharacter("Foo", 2, 2);
    CharacterModifier characterModifier = TestUtils.createCharacterModifier(-10, -15, -1, 1);
    testObject.applyAttack(characterModifier);
    assertEquals(90, testObject.health);
    assertEquals( 35, testObject.magic);
    assertEquals(1, testObject.power);
    // the testObject wears a armor now
    Armor armor = TestUtils.createArmor(0.5, 0.0, 0.0);
    TestUtils.equipArmor(armor, testObject);
    characterModifier = TestUtils.createCharacterModifier(-10, -15, -1, 1);
    testObject.applyAttack(characterModifier);
    assertEquals(85, testObject.health);
    assertEquals( 35, testObject.magic);
    assertEquals(1, testObject.power);
  }

  /**
   * Test whether items, mor specific, the <code>CharacterModifier</code>s connected to them,
   * are correctly applied to our test subject (100 health, 50 magic and 2 power)
   */
  @Test
  void applyItem() {
    // create our test subject
    Character testObject = TestUtils.createBaseCharacter("Foo", 2, 2);
    // some poison
    CharacterModifier characterModifier = TestUtils.createCharacterModifier(-10, -15, -1, 1);
    testObject.applyItem(characterModifier);
    assertEquals(90, testObject.health);
    assertEquals( 35, testObject.magic);
    assertEquals(1, testObject.power);
    // some healing potion
    characterModifier = TestUtils.createCharacterModifier(10, 15, 1, 1);
    testObject.applyItem(characterModifier);
    assertEquals(100, testObject.health);
    assertEquals( 50, testObject.magic);
    assertEquals(2, testObject.power);
    // some food providing extra power
    characterModifier = TestUtils.createCharacterModifier(0, 0, 40, 1);
    testObject.applyItem(characterModifier);
    assertEquals(100, testObject.health);
    assertEquals( 50, testObject.magic);
    assertEquals(42, testObject.power);
  }
}
