package de.hpi.yawl;

import java.util.*;


public class Model {
	private String uri; // The uri of the YAWL model
	private String description = "No description has been given.";
	//private String name = "";
	//private String documentation = "";
	private HashMap<String, Decomposition> decompositions = new HashMap<String, Decomposition>(); // All decompositions of the YAWL model

	/**
	 * Create a new YAWL mode, given its uri.
	 * @param uri The given uri.
	 */
	public Model(String uri) {
		//super("YAWL model");
		
		this.uri = uri.replaceAll(" ","."); // spaces are not allowed in uri's
	}

	/**
	 * Adds a given decomposition with a given name to the YAWL model.
	 * @param id The given name
	 * @param decomposition The given decomposition
	 */
	public void addDecomposition(String id, Decomposition decomposition) {
		decompositions.put(id, decomposition);
	}

	public Collection<Decomposition> getDecompositions() {
		return decompositions.values();
	}

	public Decomposition getDecomposition(String id) {
		return decompositions.get(id);
	}

	/**
	 * Return whether the given name corresponds to a non-empty decomposition
	 * @param name The given name
	 * @return Whether this name corresponds to a non-empty decomposition
	 */
	public boolean isComposite(String name) {
		Decomposition decomposition = decompositions.get(name);
		if (decomposition == null) {
			return false;
		}
		return!decomposition.getNodes().isEmpty();
	}

	/**
	 * Export to YAWL file.
	 * @param bw Writer
	 * @return String The string to export for this YAWLDecompositon.
	 */
	public String writeToYAWL() {

		String s = "";
		s += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		s += "<specificationSet ";
		s += "xmlns=\"http://www.yawlfoundation.org/yawlschema\" ";
		s += "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
		s += "version=\"2.0\" ";
		s +="xsi:schemaLocation=\"http://www.yawlfoundation.org/yawlschema http://www.yawlfoundation.org/yawlschema/YAWL_Schema2.0.xsd\" >\n";
		s += "\t<specification uri=\"" + uri + "\">\n";
		s += "\t\t<metaData>\n";
		s += "\t\t\t<description>" + description + "</description>\n";
		s += "\t\t</metaData>\n";
		s += "\t\t<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" />\n";
		
		for (Decomposition decomposition: decompositions.values()) {
			s += decomposition.writeToYAWL();
		}
		s += "\t</specification>\n";
		s += "</specificationSet>\n";

		return s;
	}
}