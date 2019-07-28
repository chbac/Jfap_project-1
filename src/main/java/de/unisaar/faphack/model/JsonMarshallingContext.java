package de.unisaar.faphack.model;

import de.unisaar.faphack.model.map.Room;
import de.unisaar.faphack.model.map.Tile;
import org.json.simple.JSONArray;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;

public class JsonMarshallingContext implements MarshallingContext {

  private final File file;

  private static StorableFactory factory;

  private IdentityHashMap<Storable, String> writecache;

  private Deque<JSONObject> stack;

  private Map<String, Storable> readcache, readcache2;

  private int idGenerator = 1;

  public JsonMarshallingContext(File f, StorableFactory fact) {
    file = f;
    factory = fact;
    writecache = new IdentityHashMap<Storable, String>();
    readcache = new HashMap<String, Storable>();
    readcache2 = new HashMap<String, Storable>();
    stack = new ArrayDeque<JSONObject>();
  }

  @Override
  public void save(Storable s) {
	  try (FileWriter f = new FileWriter(file)) {
		  JSONObject write_this = toJson(s);
		  f.write(write_this.toJSONString());
		  f.flush();
	  } catch (IOException e) {
		  e.printStackTrace();
	  }
	  
  }

  public Storable read() {
	// Initialise variables
    JSONParser parser = new JSONParser();
    JSONObject jsonObject = null;
    Storable output = null;
    
    // Try reading and parsing the file
    try {
    	jsonObject = (JSONObject) parser.parse(new FileReader(file));
    	stack.push(jsonObject);
    } catch (Exception e) {
    	e.printStackTrace();
    }
    
    // Handle cache and object creation
    /**
    String curr_id = (String) jsonObject.get("id");
    if (readcache.get(curr_id) == null) {
    	int index_at = curr_id.indexOf("@");
    	String clazz = curr_id.substring(0,index_at);
    	output = factory.newInstance(clazz);
    	readcache.put(curr_id, output);
    } else {
    	output = readcache.get(curr_id);
    } */
    output = fromJson(jsonObject);
    stack.pop();
    // return the object
    return output;
  }
  
  @SuppressWarnings("unchecked")
  private JSONObject toJson(Storable s) {
	  JSONObject output = new JSONObject();
	  // If not in writecache
	  if (!writecache.containsKey(s)) {
		  // s wasn't in cache, add it and assign new ID
		  String new_id = s.getClass().getSimpleName()+"@"+String.valueOf(idGenerator++);
		  output.put("id", new_id);
		  writecache.put(s, new_id);
		  stack.push(output);
		  s.marshal(this);
		  output = stack.pop();
	  } else { // If in writecache
		  output.put("id", writecache.get(s));
		  
	  }
	  

	  
	  return output;
  }
  
  /** Create object from Jsonobject*/
  @SuppressWarnings("unchecked")
  private <T extends Storable> T fromJson(JSONObject json) {
	  String id = (String) json.get("id");
	  if (id.equals("DoorTile@12")) System.out.println("DoorTile@12: "+stack.getFirst().get("hallway"));
	  //if (id.equals("DoorTile@10")) System.out.println("DoorTile@10: "+((JSONObject) stack.getFirst().get("hallway")).get("id"));
	  Storable t = null;
	  System.out.print("X");
	  if (id.equals("Hallway@11")) System.out.println(stack.getFirst());
	  if (readcache.containsKey(id)) {
		  // if only id
		  t = readcache.get(id);
		  if (json.keySet().size() != 1) {
			  if (!(json.keySet().size() == 2 && json.containsKey("index"))) {
				readcache2.put(id, t);
				t.unmarshal(this);  
			  }
		  }
	  } else {
		  if (id.equals("DoorTile@12")) System.out.println("Define door12 here");
		  String clazz = id.substring(0, id.indexOf("@"));
		  t = factory.newInstance(clazz);
		  readcache.put(id, t);
		  if (json.keySet().size() != 1) {
			  if (!(json.keySet().size() == 2 && json.containsKey("index"))) {
				    if (id.equals("DoorTile@12")) System.out.println("Define door12 here");
					readcache2.put(id, t);
				    t.unmarshal(this);  
				  }
		  }
		 
	  }
	  
	  return (T) t;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void write(String key, Storable object) {
	  if (object == null) {
		  stack.getFirst().put(key, null);
		  return;
	  }
	  if (writecache.containsKey(object)) {
		  stack.getFirst().put(key, writecache.get(object));
	  } else {
		  stack.getFirst().put(key, toJson(object));
	  }

  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Storable> T read(String key) { 
	  Storable output = null;
	  //CHECK READCACHE STRING NOT JSON IF NOT NEW
	  if (stack.getFirst().get(key) instanceof String) {
		  return (T) readcache2.get(key);
	  }
	  if (stack.getFirst().get(key) == null) return null;
	  JSONObject json =(JSONObject) stack.getFirst().get(key);
	  stack.push(json);
	  output = fromJson(json);
	  stack.pop();
	  
	  return (T) output;
  }
  
//  private <T extends Storable> T fromJson(String key) {
//	  return null;
//  }

  @SuppressWarnings("unchecked")
  @Override
  public void write(String key, int object) {
    stack.getFirst().put(key, object);

  }

  @Override
  public int readInt(String key) {
    return Integer.parseInt(stack.getFirst().get(key).toString());
  }

  @SuppressWarnings("unchecked")
  @Override
  public void write(String key, double object) {
    stack.getFirst().put(key, object);
  }

  @Override
  public double readDouble(String key) {
    return (double) stack.getFirst().get(key);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void write(String key, String object) {
    stack.getFirst().put(key, object);
  }

  @Override
  public String readString(String key) {
    return (String) stack.getFirst().get(key);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void write(String key, Collection<? extends Storable> coll) {
	  JSONObject coll_json = new JSONObject();
	  stack.push(coll_json);
	  for (Storable s : coll) {
		  JSONObject converted_storable = toJson(s);
		  stack.getFirst().put(writecache.get(s), converted_storable);
	  }
	  coll_json = stack.pop();
	  stack.getFirst().put(key, coll_json);
  }

  @Override
  public void readAll(String key, Collection<? extends Storable> coll) {
    JSONObject coll_json = (JSONObject) stack.getFirst().get(key);
    // handle empty coll
    if (coll_json == null) return;
    // handle non-empty coll
    for (Object id : coll_json.keySet()) {
    	JSONObject coll_element_json = (JSONObject) coll_json.get(id);
    	
    	stack.push(coll_element_json);
    	coll.add(fromJson(coll_element_json));
    	stack.pop();
    }

  }

  @SuppressWarnings("unchecked")
  @Override
  public void write(String key, Tile[][] coll) {
	  JSONObject room_json = new JSONObject();
	  stack.push(room_json);
	  int i = 0;
	  int j = 0;
	  for (Tile[] tlist:coll) {
	  //for (int x = 0; x < coll.length; x++) {
	  //  Tile[] tlist = coll[x];
		  JSONObject tlist_json = new JSONObject();
		  
		  stack.push(tlist_json);
		  //for (Tile t:tlist) {
		  for (int x = 0; x < tlist.length; x++) {
			  Tile t = tlist[x];
			  JSONObject tile_json = toJson(t);
			  tile_json.put("index", x);
			  stack.getFirst().put(writecache.get(t), tile_json);
			  if (x > j) j = x;
		  }
		  tlist_json = stack.pop();
		  stack.getFirst().put(i, tlist_json);
		  i++;
	  }
	  room_json = stack.pop();
	  room_json.put("array_length", i);
	  room_json.put("array_width", j+1);
	  stack.getFirst().put(key, room_json);
  }

  @Override
  public Tile[][] readBoard(String key) {
	  stack.push((JSONObject) stack.getFirst().get(key));
//	  if (readcache.containsKey(stack.getFirst().get("id"))) {
//	  }
	  int length = Integer.parseInt(stack.getFirst().get("array_length").toString());
	  int width = Integer.parseInt(stack.getFirst().get("array_width").toString());
	  Tile[][] output = new Tile[length][width]; 
	  Tile t = null;
	  
	  for (Object index : stack.getFirst().keySet()) {
		  if (index.equals("array_length")) break;
		  if (index.equals("array_width")) break;
		  stack.push((JSONObject) stack.getFirst().get(index));
		  Tile[] tlist = new Tile[width];
		  for (Object tile_id : stack.getFirst().keySet()) {
			  JSONObject tile_json = (JSONObject) stack.getFirst().get(tile_id);
			  stack.push(tile_json);
			  t = fromJson(tile_json);
			  tlist[Integer.parseInt(stack.getFirst().get("index").toString())] = (Tile) t;
			  stack.pop();
		  }
		  output[Integer.valueOf((String) index)] = tlist;
		  stack.pop();
	  }
	  stack.pop();
	  
    return output;
  }

}
