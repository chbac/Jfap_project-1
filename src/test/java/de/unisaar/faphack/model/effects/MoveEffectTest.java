package de.unisaar.faphack.model.effects;

import de.unisaar.faphack.model.Character;
import de.unisaar.faphack.model.Direction;
import de.unisaar.faphack.model.TestUtils;
import de.unisaar.faphack.model.map.Room;
import de.unisaar.faphack.model.map.Tile;
import org.junit.jupiter.api.Test;

import static de.unisaar.faphack.model.TestUtils.addCharacter;
import static de.unisaar.faphack.model.TestUtils.createBaseCharacter;
import static org.junit.jupiter.api.Assertions.*;

class MoveEffectTest {

  /**
   * Move a character which is placed on tile 1x1 in a square 4 x 4 room .
   * 1: Direction (0,1) to the right
   * 2: Direction (0,-1) to the left ( now we should be back where we started)
   * 3. Direction (-1, 1) down (this should have now effect as there is a wall )
   * 4. Direction (0,2) exception expected as only directions with values from -1 to 1 are allowed in MoveEffects
   * 5. move actions are not allowed when the character's power is 0
   */
  @Test
  void apply() {
    Room room = TestUtils.createSimpleRoom(8,8,1);
    Tile[][] tiles = room.getTiles();
    Character testObject = createBaseCharacter("Foo", 2, 2);
    addCharacter(room, 1, 1, testObject);
    MoveEffect moveEffect = new MoveEffect(new Direction(0,1));
    moveEffect.apply(testObject);
    assertEquals(tiles[1][2], testObject.getTile());
    moveEffect = new MoveEffect(new Direction(0,-1));
    moveEffect.apply(testObject);
    assertEquals(tiles[1][1], testObject.getTile());
    moveEffect = new MoveEffect(new Direction(-1,0));
    moveEffect.apply(testObject);
    assertEquals(tiles[1][1], testObject.getTile());
    assertThrows(IllegalArgumentException.class, () ->  new MoveEffect(new Direction(0, 2)));
    // create a testObject with 0 power
    Character testObject2 = createBaseCharacter("Bar", 0, 2);
    addCharacter(room, 2,2, testObject2);
    moveEffect = new MoveEffect(new Direction(1,1));
    moveEffect.apply(testObject2);
    assertEquals(tiles[2][2], testObject2.getTile());
  }
}