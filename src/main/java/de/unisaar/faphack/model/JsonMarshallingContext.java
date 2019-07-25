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

  private Map<String, Storable> readcache;

  private int idGenerator = 1;

  public JsonMarshallingContext(File f, StorableFactory fact) {
    file = f;
    factory = fact;
    writecache = new IdentityHashMap<Storable, String>();
    readcache = new HashMap<String, Storable>();
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
    String curr_id = (String) jsonObject.get("id");
    if (readcache.get(curr_id) == null) {
    	int index_at = curr_id.indexOf("@");
    	String clazz = curr_id.substring(0,index_at);
    	output = factory.newInstance(clazz);
    	readcache.put(curr_id, output);
    } else {
    	output = readcache.get(curr_id);
    }
    // Fill fields of newly created object
    stack.push(jsonObject);
    output.unmarshal(this);
    stack.pop();
    
    // return the object
    return output;
  }
  
  @SuppressWarnings("unchecked")
  private JSONObject toJson(Storable s) {
	  JSONObject output = new JSONObject();
	  // If not in writecache
	  if (writecache.get(s) == null) {
		  // s wasn't in cache, add it and assign new ID
		  String new_id = s.getClass().getSimpleName()+"@"+String.valueOf(idGenerator++);
		  output.put("id", new_id);
		  writecache.put(s, new_id);
	  } else { // If in writecache
		  output.put("id", writecache.get(s));
	  }
	  
	  // add to stack so the marshal methods can interact with the JSONObject
	  stack.push(output);
	  // do all the needed writes to the jsonobject
	  s.marshal(this);
	  
	  return stack.pop();
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
	  // put storable to be read on stack
	  stack.push((JSONObject) stack.getFirst().get(key));
	  
	  // check readcache
	  String curr_id = (String) stack.getFirst().get("id");
	  if (readcache.get(curr_id) == null) {
		  int index_at = curr_id.indexOf("@");
		  String clazz = curr_id.substring(0,index_at);
		  output = factory.newInstance(clazz);
		  readcache.put(curr_id, output);
	  } else {
		  output = readcache.get(stack.getFirst().get("id"));
	  }
	stack.pop();
    return (T) output;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void write(String key, int object) {
    stack.getFirst().put(key, object);

  }

  @Override
  public int readInt(String key) {
    return (int)stack.getFirst().get(key);
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
    // TODO Auto-generated method stub

  }

  @SuppressWarnings("unchecked")
  @Override
  public void write(String key, Tile[][] coll) {
	  JSONObject room_json = new JSONObject();
	  stack.push(room_json);
	  int i = 0;
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
		  }
		  tlist_json = stack.pop();
		  stack.getFirst().put(i, tlist_json);
		  i++;
	  }
	  room_json = stack.pop();
	  room_json.put("array_length", i);
	  stack.getFirst().put(key, room_json);
  }

  @Override
  public Tile[][] readBoard(String key) {
	  stack.push((JSONObject) stack.getFirst().get(key));
	  int length = (int) stack.getFirst().get("array_length");
	  Tile[][] output = new Tile[length][]; 
	  for (Object index : stack.getFirst().keySet()) {
		  Tile[] tlist = new Tile[length];
		  for (int i = 0; i < tlist.length; i++) {
			  
			  tlist[i] = null;
		  }
	  }
	  stack.pop();
	  
    return output;
  }

}
