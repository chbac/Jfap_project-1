package de.unisaar.faphack.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.lang.model.element.Element;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.sun.tools.javac.parser.ReferenceParser.ParseException;

import de.unisaar.faphack.model.map.Tile;

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
			JSONObject game; /** final object */
			JSONObject obj = new JSONObject(); /** highest json object */
			stack.addFirst(obj);
			write("Game", s);
			game = (JSONObject) obj.get("Game");
			f.write(game.toJSONString());
			f.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Storable read() {
		/**
		 * initialize variables
		 */
		JSONParser parser = new JSONParser();
		Storable game = null;

		/**
		 * try to parse
		 */
		try (Reader r = new FileReader(file)) {

			Object parsed = parser.parse(r);
			JSONObject jsonObject = (JSONObject) parsed;
			stack.push(jsonObject);

			/**
			 * get id
			 */
			String id = (String) jsonObject.get("id");
			int index_at = id.indexOf("@");
			String clazz = id.substring(0, index_at);

			/**
			 * load first object = game
			 */
			game = factory.newInstance(clazz);
			stack.addFirst(jsonObject);
			readcache.put(id, game);
			/**
			 * start unmarshalling
			 */
			game.unmarshal(this);
			/**
			 * remove when finsished
			 */
			stack.pop();

		} catch (FileNotFoundException fe) {
			fe.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return game;
		// Handle cache and object creation
		/**
		 * String curr_id = (String) jsonObject.get("id"); if (readcache.get(curr_id) ==
		 * null) { int index_at = curr_id.indexOf("@"); String clazz =
		 * curr_id.substring(0,index_at); output = factory.newInstance(clazz);
		 * readcache.put(curr_id, output); } else { output = readcache.get(curr_id); }
		 */

		// return the object
		// return output;
	}

	@Override
	public void write(String key, Storable object) {
		/**
		 * if the storable is already in the writecache, get its id and put everything
		 * into the outer json object
		 */
		if (writecache.keySet().contains(object)) {
			JSONObject parent = stack.getFirst();
			parent.put(object, writecache.get(object));
			return;
		} 

		/**
		 * account for empty objects
		 */
		else if (object == null) {
			JSONObject parent = stack.getFirst();
			parent.put(key, null);
			return;
		}
		
		JSONObject child = new JSONObject();
		String id = getObjectId(object);
		child.put("id", id);

		/**
		 * insert into writecache and add to stack
		 */
		writecache.put(object, id);
		stack.addFirst(child);

		/**
		 * initialize recursive marshalling
		 */
		object.marshal(this);

		/**
		 * in any case, put child object into the outer json object
		 */
		JSONObject temp = stack.pop();
		JSONObject parent = stack.getFirst();
		parent.put(key, temp);

	}

	/* help method to create the id */
	private String getObjectId(Storable object) {
		String clazz = factory.getClassName(object.getClass());
		String increment = Integer.toString(idGenerator++);
		return clazz + "@" + increment;
	}

	@Override
	public <T extends Storable> T read(String key) {
		JSONObject parent;
		Object raw = stack.getFirst().get(key);
		T obj; /** return object */

		/**
		 * check for nulls
		 */
		if (raw == null) {
			return null;
		}

		/**
		 * try to retrieve from readcache
		 */
		else if (readcache.keySet().contains(key)) {
			return (T) readcache.get(key);
		}

		/**
		 * encounter an existing id
		 */
		else if (raw instanceof String) {
			return (T) readcache.get(raw);
		}

		/**
		 * no other possibility left, so do the actual work
		 */
		else {
			parent = (JSONObject) raw;
			String existingId = (String) parent.get("id");

			/**
			 * retrieve id
			 */
			int index_at = existingId.indexOf("@");
			String clazz = existingId.substring(0, index_at);

			/**
			 * create new object
			 */
			obj = (T) factory.newInstance(clazz);

			/**
			 * insert into readcache
			 */
			readcache.put(key, obj);

			/**
			 * put parent on stack
			 */
			stack.addFirst(parent);

			/**
			 * unmarshal lower level object
			 */
			obj.unmarshal(this);

			/**
			 * remove the read item from the stack
			 */
			stack.pop();

			return obj;
		}
	}

	@Override
	public void write(String key, int object) {
		stack.getFirst().put(key, object);
	}

	@Override
	public int readInt(String key) {
		return Integer.parseInt(stack.getFirst().get(key).toString());
	}

	@Override
	public void write(String key, double object) {
		stack.getFirst().put(key, object);
	}

	@Override
	public double readDouble(String key) {
		return (double) stack.getFirst().get(key);
	}

	@Override
	public void write(String key, String object) {
		stack.getFirst().put(key, object);
	}

	@Override
	public String readString(String key) {
		return (String) stack.getFirst().get(key);
	}

	@Override
	public void write(String key, Collection<? extends Storable> coll) {
		JSONArray array = new JSONArray(); /** array for contained json objects */

		for (Storable s : coll) {
			stack.addFirst(new JSONObject());

			/**
			 * initiate marshalling of the collection's objects; it will also be added to
			 * writechache in the write method
			 */
			write("bla", s);

			/**
			 * return the new object (String if the object was referenced before or json
			 * object)
			 */
			Object element = stack.pop().get("bla");
			
			/**
			 * when done, return a String if there is such an object (i.e. there is an id)
			 * else return the new json object
			 */
			if (element instanceof String) {
				String existingId = (String) element;
				array.add(existingId);
			} else {
				JSONObject json = (JSONObject) element;
				array.add(json);
			}
		}
	}

	@Override
	public void readAll(String key, Collection<? extends Storable> coll) {
		Object raw = stack.getFirst().get(key);
		Collection<JSONObject> temp = (Collection<JSONObject>) raw;
		for (Object obj : temp) {
			coll.add(fromJson(obj));
		}
	}

	public <T extends Storable> T fromJson(Object obj) {
		JSONObject child;

		/**
		 * empty collection
		 */
		if (obj == null) {
			return null;
		}

		/**
		 * handle existing ids
		 */
		else if (obj instanceof String) {
			return (T) readcache.get(obj);
		}

		/**
		 * if it's not null or a string, the collection element has to be a json object
		 */
		else {
			JSONObject parent = new JSONObject();
			child = (JSONObject) obj;
			parent.put("bla", child);
			stack.addFirst(parent);
			T output = (T) read("bla");
			stack.pop();
			return output;
		}

	}

	@Override
	public void write(String key, Tile[][] coll) {
		JSONObject parent = stack.getFirst(); /** room */
		JSONArray jsonCols = new JSONArray(); /** cols */

		/** loop over all cols */
		for (Tile[] row : coll) {
			JSONArray jsonRow = new JSONArray();

			/** loop over all rows */
			for (Tile tile : row) {
				JSONObject jsonTile; /** tile to be written */

				/** create object on stack that will be written */
				stack.addFirst(new JSONObject());
				write("bla", tile);

				/** fetch tile after writing */
				jsonTile = stack.pop();
				jsonRow.add(jsonTile);
			}
			jsonCols.add(jsonRow);
		}
		parent.put(key, jsonCols);
	}

	@Override
	public Tile[][] readBoard(String key) {
		JSONObject parent = stack.getFirst();
		JSONArray array = (JSONArray) parent.get(key); /** the board, needs to be cast */

		/**
		 * create a new default tilelist. praise the json arrays!
		 */
		int height = array.size();
		int width = ((JSONArray) array.get(0)).size();
		Tile[][] tiles = new Tile[height][width];

		/**
		 * do the heavy work
		 */
		for (int h = 0; h < height; h++) { /** rows */
			JSONArray jsonRow = (JSONArray) array.get(h);

			/**
			 * single fields
			 */
			for (int w = 0; w < width; w++) {
				Object obj = jsonRow.get(w);

				/**
				 * break if field is null (necessary?)
				 */
				if (obj == null) {
					tiles[w][h] = null;
					break;
				}
				/**
				 * check for encountered fields, existing id found
				 */
				else if (obj instanceof String) {
					Tile t = (Tile) readcache.get((String) obj);
					tiles[h][w] = t;
				}

				/**
				 * now, only a new json object is left; put it on the stack and then read it.
				 */
				else {
					JSONObject temp = new JSONObject();
					temp.put("bla", (JSONObject) obj);
					stack.addFirst(temp);
					tiles[h][w] = read("bla");
					/**
					 * remove again
					 */
					stack.pop();
				}
			}
		}
		return tiles;
	}

}
