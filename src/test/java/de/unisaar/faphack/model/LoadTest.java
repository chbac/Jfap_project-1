package de.unisaar.faphack.model;

import static de.unisaar.faphack.model.TestUtils.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import de.unisaar.faphack.dirtyhacks.StorableRegistrator;

class LoadTest {

  private Map<String, String> idMap = new HashMap<>();
  private Set<String> o2ids = new HashSet<>();

  private void migrateRec(JSONObject o1, JSONObject o2,
      Map<String, String> idMap, Set<String> o2ids)
          throws IllegalArgumentException {
    String id1 = (String) o1.get("id");
    String id2 = (String) o2.get("id");
    if (idMap.containsKey(id1)) {
      if (! idMap.get(id1).equals(id2)) throw new IllegalArgumentException();
    } else {
      if (o2ids.contains(id2)) throw new IllegalArgumentException();
      idMap.put(id1, id2);
      o2ids.add(id2);
      o2.put("id", id1);
    }
    for (Object o : o1.keySet()) {
      String s = (String)o;
      if (! s.equals("id")) {
        Object c = o1.get(s);
        if (c instanceof JSONObject) {
          migrateRec((JSONObject)c, (JSONObject)o2.get(s), idMap, o2ids);
        }
      }
    }
  }

  /** We will try to carry over the "id" fields by registering the mapping
   *  when encountered for the first time, and checking in subsequent cases.
   *  @return false if there is no mapping possible, true otherwise
   */
  private boolean migrateIds(JSONObject o1, JSONObject o2) {
    idMap.clear();
    o2ids.clear();
    try {
      migrateRec(o1, o2, idMap, o2ids);
    } catch (Exception ex) {
      return false;
    }
    return true;
  }

  /**
   * Creates an instance of the TestUtils default Game which is then saved to a .json file.
   * Finally, a game is loaded from the json file and checked for correctness of all components.
   */
  @Test
  void saveGame() {
    Game game = createGame();
    File f = getTestResourceFile("", "game.json");
    StorableFactory fact = new StorableFactory();
    StorableRegistrator.registerStorables(fact);
    MarshallingContext mc = new JsonMarshallingContext(f, fact);
    mc.save(game);
    assertTrue(f.canRead());
    mc = new JsonMarshallingContext(f, fact);
    Game game2 = (Game)mc.read();
    testDefaultWordData(game2.getWorld());
  }


  /**
   * Tests the load and save features by loading an item instance (sword)
   * from a .json file and writing it back to .json. 
   * The output .json is compared to the input .json.
   * @throws IOException
   * @throws ParseException
   */
  @Test
  void loadSword() throws IOException, ParseException {
    File f = getTestResourceFile("", "sword.json");
    StorableFactory fact = new StorableFactory();
    StorableRegistrator.registerStorables(fact);
    MarshallingContext mc = new JsonMarshallingContext(f, fact);
    Item sword = (Item)mc.read();
    assertNotNull(sword);
    File f2 = getTestResourceFile("", "sword_out.json");
    mc = new JsonMarshallingContext(f2, fact);
    mc.save(sword);
    JSONParser parser = new JSONParser();
    JSONObject orig, saved;
    Reader reader = new FileReader(f);
    orig = (JSONObject) parser.parse(reader);
    reader.close();
    reader = new FileReader(f2);
    saved = (JSONObject) parser.parse(reader);
    migrateIds(orig, saved);
    reader.close();
    assertEquals(orig, saved);
  }
}
