package de.hpi.pictureSupport;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * base class for all elements which should be read
 * it provides</br> 
 * a facility to store unused attributes or elements
 * in a String which can be exported if necessary</br>
 * the basic mechanism to read in and write the JSON model for/from PICTURE
 *
 */
public class XMLConvertible implements Serializable{
	
	private static final long serialVersionUID = 173231207049052166L;	
	
	/**
	 * methods to parse a JSON object tree and prepare them for xmappr
	 * implicit interface for creating a JSON-Object (methods must start with "readJSON")	  
	 * @throws JSONException 
	 */
	public void readJSON(JSONObject modelElement){
		Iterator<?> jsonKeys = modelElement.keys();
		while (jsonKeys.hasNext()) {
			String key = (String) jsonKeys.next();
			String readMethodName = "readJSON" + key;
			if (key.length() > 0 && hasJSONMethod(readMethodName)) {
				try {
					if (keyNotEmpty(modelElement, key)) {
						getClass().getMethod(readMethodName, JSONObject.class).invoke(this, modelElement);
					}
				} catch (Exception e) {
					try {
						Logger.e(this.getClass()+"\t"+readMethodName+"\n"+modelElement,e);
					} catch (Exception e2){
						Logger.e(this.getClass()+"\t"+readMethodName,e);
					}
					e.printStackTrace();
				}
			} else {
				readJSONunknownkey(modelElement, key);
			}
		}
	}

	/**
	 * responsible for writing error messages 
	 * @param modelElement
	 * @param key
	 */
	public void readJSONunknownkey(JSONObject modelElement, String key) {
		Logger.e( "Unknown JSON-key: " + key + "\n" +
							"in JSON-Object: " + modelElement + "\n" +
							"while parsing in: " + getClass() + "\n");
	}
	
	
	protected boolean keyNotEmpty(JSONObject modelElement, String key) {
		try {
			JSONObject objectAtKey = modelElement.getJSONObject(key);
			//Value is a valid JSONObject and has members
			return objectAtKey.length() > 0;
		} catch(JSONException objectException) {
			try {
				JSONArray arrayAtKey = modelElement.getJSONArray(key);
				//Value is a valid JSONArray and has at least one element
				return arrayAtKey.length() > 0;
			} catch(JSONException arrayException) {
				return !modelElement.optString(key).equals(""); 
			}
		}
	}
	
	protected boolean hasJSONMethod(String methodName) {
		Method[] methods = getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(methodName) &
				hasMethodJSONParameter(methods[i])) {
				return true;
			}
		}
		return false;
	}

	protected boolean hasMethodJSONParameter(Method method) {
		@SuppressWarnings("rawtypes")
		Class[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length == 1) {
			return parameterTypes[0].equals(JSONObject.class);
		}
		return false;
	}
	
	public JSONArray getJSONArray(JSONObject json,String key) throws JSONException{
		if (json.optJSONArray(key) == null){
			json.put(key,new JSONArray());
		}
		return json.getJSONArray(key);
	}
	
	public JSONObject getJSONObject(JSONObject json,String key) throws JSONException{
		if (json.optJSONObject(key) == null){
			json.put(key,new JSONObject());
		}
		return json.getJSONObject(key);
	}
	
	/**
	 * parses the class and looks for methods starting with writeJSON
	 * these methods are executed in the current context with the json object as base 
	 * @throws JSONException 
	 */
	public void writeJSON(JSONObject modelElement) throws JSONException {
		Method[] methods = getClass().getMethods();
		ArrayList<Method> unusedHandler = new ArrayList<Method>();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			String methodName = method.getName();
			if (methodName.startsWith("writeJSON") && methodName.length() != 9) {
				if (methodName.contains("unused")){
					unusedHandler.add(method);
				} else {
					try {
						getClass().getMethod(methodName, JSONObject.class).invoke(this, modelElement);
					} catch (Exception e) {
						try {
							Logger.e(this.getClass()+"\t"+methodName+"\n"+modelElement,e);
						} catch (Exception e2){
							Logger.e(this.getClass()+"\t"+methodName,e);
						}
					}
				}
			}
		}
	}
	
	

	protected Object fromStorable(String stored) {
		BASE64Decoder base64dec = new BASE64Decoder();
		
		try {
			//Read Base64 String and decode them
			byte[] decodedBytes = base64dec.decodeBuffer(stored);
			ByteArrayInputStream byteStreamIn = new ByteArrayInputStream(decodedBytes);
			//Restore the object
			ObjectInputStream objectStreamIn = new ObjectInputStream(byteStreamIn);
			return objectStreamIn.readObject();
		} catch (Exception e) {
			Logger.e("could not recover encoded elements",e);
			e.printStackTrace();
		}
		return null;
	}
	
	protected String makeStorable(Object objectToStore) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try {
			//Serialize the Java object
			ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
			objectStream.writeObject(objectToStore);
		} catch (IOException e) {
			Logger.e("could not encoded elements "+objectToStore,e);
			e.printStackTrace();
		}
		
		BASE64Encoder base64enc = new BASE64Encoder();
		//Encode the byte stream with Base64 -> Readable characters for the JSONObject
		return base64enc.encode(byteStream.toByteArray());	
	}
	
	
	
	
}