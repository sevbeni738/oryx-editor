package de.hpi.netgraph2xml.netgraph;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.bpmn2xpdl.XMLConvertible;
import de.hpi.netgraph2xml.XMLConvertibleUtils;

@RootElement
public class Network extends XMLConvertible{
    @Element
    String base_ip;
    @Element
    String subnet;
    @Attribute
    String id;
    @Attribute
    String dhcp;
    public String getBase_ip() {
        return base_ip;
    }
    public void setBase_ip(String base_ip) {
        this.base_ip = base_ip;
    }
    public String getSubnet() {
        return subnet;
    }
    public void setSubnet(String subnet) {
        this.subnet = subnet;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getDhcp() {
        return dhcp;
    }
    public void setDhcp(String dhcp) {
        this.dhcp = dhcp;
    }
    
    public void writeJSONstencil(JSONObject modelElement) throws JSONException {
	JSONObject stencil = new JSONObject();
	stencil.put("id", "switch");

	modelElement.put("stencil", stencil);
    }
    public void writeJSONid(JSONObject modelElement) throws JSONException {
	modelElement.put("resourceId", getId());
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("id", getId());
    }
    public void readJSONid(JSONObject modelElement) throws JSONException {
	setId(modelElement.optString("id"));
    }
    public void writeJSONbaseip(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("baseip", getBase_ip());
    }
    public void readJSONbaseip(JSONObject modelElement) throws JSONException {
	setBase_ip(modelElement.optString("baseip"));
    }
    public void writeJSONsubnet(JSONObject modelElement) throws JSONException {
	modelElement = XMLConvertibleUtils.switchToProperties(modelElement);
	modelElement.put("subnet", getSubnet());
    }
    public void readJSONsubnet(JSONObject modelElement) throws JSONException {
	setSubnet(modelElement.optString("subnet"));
    }
    public void readJSONproperties(JSONObject modelElement) throws JSONException {
	modelElement = modelElement.optJSONObject("properties");
	if(modelElement==null){
	    return;
	}
	parse(modelElement);
    }
    public void writeJSONresourceId(JSONObject modelElement) throws JSONException {
	modelElement.put("resourceId", XMLConvertibleUtils.generateResourceId());
    }
}
