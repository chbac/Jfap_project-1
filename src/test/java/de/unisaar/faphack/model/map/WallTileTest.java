package de.unisaar.faphack.model.map;


import de.unisaar.faphack.model.Character;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static de.unisaar.faphack.model.TestUtils.createBaseCharacter;
import static de.unisaar.faphack.model.TestUtils.createWallTile;
import static org.junit.jupiter.api.Assertions.assertNull;

class WallTileTest {

  /**
   * A walltile can only be accessed by a character if it is destructible
   * and the character has enough power to destroy it
   *
   */
  @Test
  void willTake() {
    Character character = createBaseCharacter("Foo", 2, 2);
    Character strongCharacter = createBaseCharacter("Bar", 42, 2);
    // indestructible Wall
    WallTile wall = createWallTile(0);
    assertNull(wall.willTake(character));
    // destructible wall (3) & weak character (2)
    WallTile destructibleWall = createWallTile(3);
    assertNull(destructibleWall.willTake(character));
    // destructible wall (3) & strong character (42)
    Assertions.assertEquals(destructibleWall, destructibleWall.willTake(strongCharacter));
  }
}