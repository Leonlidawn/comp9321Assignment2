
package comp9321;
import java.util.HashMap;
import org.w3c.dom.Node;
/**
 * @author weidongli
 *
 */
public class Item implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String publtype;
	private String key;//this is unique for all entries in dblp
	private String mdate;
	private HashMap<String,String> info;
	//private XPathExpression xp;
	private String xp;
//	public Item(Node node, XPathExpression xp){
	public Item(Node node, String xp){
		this.xp = xp;
		publtype = node.getNodeName();
		info = new HashMap<String,String>();
		mdate = node.getAttributes().getNamedItem("mdate").getTextContent();
		key = node.getAttributes().getNamedItem("key").getTextContent();
 		int lengthN = node.getChildNodes().getLength();
 		for(int i = 1; i<lengthN;i=i+2){
 			String k = node.getChildNodes().item(i).getNodeName();
 			String v = node.getChildNodes().item(i).getTextContent();
 			
 			if(info.containsKey(k)){
 				v = info.get(k)+", "+v;
 			}
 			info.put(k, v);
 		}
 		
 		
	}
	
	/**
	 * try to get information of this item, but may not exist. 
	 * returns null if not found.
	 *
	 * @param infoName information kind. etc title, author.
	 */
	public HashMap<String,String> getInfo(){
		return this.info;
	}
	
	public String tryGet(String infoName){
		return  info.get(infoName);
	}
	
	//public XPathExpression getXpathExpression( ){
	public String getXpath(){
		return xp;
	}
	
	public String getPubltype() {
		return publtype;
	}
	public String getKey() {
		return key;
	}
	public String getMdate() {
		return mdate;
	}
	public boolean isSame(Item other){
		return other.getKey().equals(this.key);
	}
	public String getHtmlXpath(){
		String temp = this.xp;
		temp = temp.replaceAll("'", "&apos;");
		temp = temp.replaceAll("\"", "&quot;");
	//	temp =	temp.replaceAll("&", "&amp;"); caused so much trouble. replace invisible \ with &amp, which is wrong.
		temp =	temp.replaceAll("<", "&lt;");
		temp = temp.replaceAll(">", "&at;");
		return temp;
	}

}
