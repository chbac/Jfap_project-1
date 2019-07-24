package de.unisaar.faphack.model;

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
    JSONParser parser = new JSONParser();
    try {
    	JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(file));
    	stack.push(jsonObject);
    } catch (Exception e) {
    	e.printStackTrace();
    }
    
    return null;
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

  @Override
  public <T extends Storable> T read(String key) {
    // TODO Auto-generated method stub
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void write(String key, int object) {
    stack.getFirst().put(key, object);

  }

  @Override
  public int readInt(String key) {
    // TODO Auto-generated method stub
    return 0;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void write(String key, double object) {
    stack.getFirst().put(key, object);
  }

  @Override
  public double readDouble(String key) {
    // TODO Auto-generated method stub
    return 0;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void write(String key, String object) {
    stack.getFirst().put(key, object);
  }

  @Override
  public String readString(String key) {
    // TODO Auto-generated method stub
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void write(String key, Collection<? extends Storable> coll) {
	  for (Storable s : coll) {
		  if (s instanceof Room) {
			 
		  }
	  }
	  stack.getFirst().put(key, coll);
  }

  @Override
  public void readAll(String key, Collection<? extends Storable> coll) {
    // TODO Auto-generated method stub

  }

  @Override
  public void write(String key, Tile[][] coll) {
    // TODO Auto-generated method stub
	  for (Tile[] tlist:coll) {
		  for (Tile t:tlist) {
			  
		  }
	  }

  }

  @Override
  public Tile[][] readBoard(String key) {
    // TODO Auto-generated method stub
    return null;
  }

}
