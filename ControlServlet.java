

package comp9321;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.*;

/**
 * Servlet implementation class 
 *
 * @author weidongli
*
*/
@WebServlet(name="ControlServlet",urlPatterns="/search")
public class ControlServlet extends HttpServlet {
	private static int counter = 0;
	private static final long serialVersionUID = 1L;
	private static Document doc = null;
	private static NodeList nodeList = null;
	private static String docPath = "/Users/weidongli/Desktop/EclipseWorkSpace/Assign1/xmlFiles/dblp"+'.'+"xml";
	//had to separate it into 3 strings, or the file loading will just fail. totally absurd!
	
    /**
     * @see HttpServlet#HttpServlet()
    public ControlServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest rq, HttpServletResponse rs) throws ServletException, IOException {
		doPost(rq,rs);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String nextPage = "search.jsp";
		String buttonName = request.getParameter("buttonName");
		Vector<Item> itemList = new Vector<Item>();
		XPathFactory xPathFactory = XPathFactory.newInstance();
			
		if (doc == null ){//initialize
			
			try {
				DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = builderFactory.newDocumentBuilder();
		        File f = new File(docPath);
				doc = builder.parse(f);
				nodeList = doc.getDocumentElement().getChildNodes();
			} catch (Exception e) {	}
			//initialize the cart cookies
		}
		
		if(buttonName!=null){//input form 
			if(buttonName.equals("Add to Cart")){
				String xpString = convertHtmlXpath(request.getParameter("htmlXpath")) ;
				//log("xpString is : "+xpString+" and   "+ request.getParameter("htmlXpath"));
				boolean added = false;
				for(Cookie c : request.getCookies()){
					//out.print("<br><br>"+c.getValue()+"<br>"+item.getXpath());
					if(xpString.equals(c.getValue())){//ignore case is wrong, but it is fine in this case.
						added = true;
					}
				}
				try {
						XPath xpath = xPathFactory.newXPath();
						XPathExpression expr = xpath.compile(xpString);  
						Node exprResult = (Node) expr.evaluate(doc, XPathConstants.NODE);
						Item item = new Item(exprResult,xpString);
						request.setAttribute("item",item);//might be better to direct from search.jsp to itemPage.jsp.
				} catch (Exception e) {e.printStackTrace();}
				if(!added){
					//unique name for each cartItemHtmlXpath?? cookie
					Cookie c = new Cookie("cartItemHtmlXpath"+Integer.toString(ControlServlet.counter),request.getParameter("htmlXpath"));
					counter++;	
					c.setMaxAge(12000);
					response.addCookie(c);
				}
				request.setAttribute("needReload", true);
				nextPage = "itemPage.jsp";
			}else if(buttonName.equals("         add         ")){
				itemList = (Vector<Item>) request.getSession().getAttribute("randomList");
				String xpString = convertHtmlXpath(request.getParameter("htmlXpath")) ;
				//log("xpString is : "+xpString+" and   "+ request.getParameter("htmlXpath"));
				boolean added = false;
				for(Cookie c : request.getCookies()){
					if(xpString.equals(c.getValue())){//ignore case is wrong, but it is fine in this case.
						added = true;
					}
				}
				if(!added){
					//unique name for each cartItemHtmlXpath?? cookie
					Cookie c = new Cookie("cartItemHtmlXpath"+Integer.toString(ControlServlet.counter),request.getParameter("htmlXpath"));
					counter++;	
					c.setMaxAge(12000);
					response.addCookie(c);
				}
				
			}
			else if(buttonName.equals("        add        ") ){
				nextPage = "result.jsp";
				itemList = (Vector<Item>) request.getSession().getAttribute("resultList");
				String xpString = convertHtmlXpath(request.getParameter("htmlXpath")) ;
				boolean added = false;
				for(Cookie c : request.getCookies()){
					if(xpString.equals(c.getValue())){//ignore case is wrong, but it is fine in this case.
						added = true;
					}
				}
				if(!added){
					//unique name for each cartItemHtmlXpath?? cookie
					Cookie c = new Cookie("cartItemHtmlXpath"+Integer.toString(ControlServlet.counter),request.getParameter("htmlXpath"));
					counter++;	
					c.setMaxAge(12000);
					response.addCookie(c);
				}
				
			}else if(buttonName.equals("    Previous   " )||buttonName.equals("      Next      ")){
				nextPage = "result.jsp";
				itemList = (Vector<Item>) request.getSession().getAttribute("resultList");
				int cur = Integer.parseInt(request.getParameter("currentPage"));
			//	log("\n\nthe page is : "+request.getParameter("currentPage"));
				if(buttonName.equals("    Previous   " )){
					request.setAttribute("currentPage",cur-1);
				}else{
					request.setAttribute("currentPage",cur+1);
				}
			}
			else if(buttonName.equals("")){
				itemList = (Vector<Item>) request.getSession().getAttribute("randomList");
				String xpString = convertHtmlXpath(request.getParameter("htmlXpath")) ;
				log("xpString is : "+xpString+" and   "+ request.getParameter("htmlXpath"));
				boolean added = false;
				for(Cookie c : request.getCookies()){
					if(xpString.equals(c.getValue())){//ignore case is wrong, but it is fine in this case.
						added = true;
					}
				}
				if(!added){
					//unique name for each cartItemHtmlXpath?? cookie
					Cookie c = new Cookie("cartItemHtmlXpath"+Integer.toString(ControlServlet.counter),request.getParameter("htmlXpath"));
					counter++;	
					c.setMaxAge(12000);
					response.addCookie(c);
				}
				
			}
			else if(buttonName.equals("Search")){ // will forward to the result page within this if block.
				nextPage = "result.jsp";
				Enumeration<String> paramNames = request.getParameterNames();
				HashMap<String,String> conditions = new HashMap<String,String>();
				HashMap<String,String> conditionsCopy = new HashMap<String,String>();

				while(paramNames.hasMoreElements()){
					String paramName =  paramNames.nextElement();
					log("\n\nThis is:"+paramName+"   v: "+request.getParameter(paramName));
					conditions.put(paramName,request.getParameter(paramName));
					conditionsCopy.put(paramName,request.getParameter(paramName));
				}
				request.setAttribute("conditions", conditions);

				LinkedList<String> cons = new LinkedList<String>();
				
				conditions.remove("buttonName");
				
				request.setAttribute("conditions", conditionsCopy);
				
				String searchTitle = "";
				if(conditions.containsKey("searchTitle") && !conditions.get("searchTitle").isEmpty()){
					searchTitle = "title[contains(text(),\'"+conditions.get("searchTitle")+"\')]";
					log("\nfuckme:"+searchTitle);
					cons.add(searchTitle);
				}
				conditions.remove("searchTitle");
				
				
				String searchAuthor = "";
				if(conditions.containsKey("searchAuthor") && !conditions.get("searchAuthor").isEmpty()){
					searchAuthor = "author[contains(text(),'"+conditions.get("searchAuthor")+"')]";
					cons.add(searchAuthor);

				}
				conditions.remove("searchAuthor");
				
				String searchISBN = "";
				if(conditions.containsKey("searchISBN") && !conditions.get("searchISBN").isEmpty()){
					searchISBN = "isbn[contains(text(),'"+conditions.get("searchISBN")+"')]";
					cons.add(searchISBN);
				}
				conditions.remove("searchISBN");
				
				String searchPublisher = "";
				if(conditions.containsKey("searchPublisher") && !conditions.get("searchPublisher").isEmpty()){
					searchPublisher = "publisher[contains(text(),'"+conditions.get("searchPublisher")+"')]";
					cons.add(searchPublisher);
				}
				conditions.remove("searchPublisher");
				
				
				String suffix = "";
				if(cons.size()>0){
					Iterator<String> iterC= cons.iterator();
					suffix += iterC.next();
					while(iterC.hasNext()){
						String newS = iterC.next();
						if(!newS.equals("")){
							suffix += " and "+newS;
						}
					}
						suffix="["+suffix+"]";
				}
				
				//==========
				String searchVenue = "";
				if(conditions.containsKey("searchVenue") && !conditions.get("searchVenue").isEmpty()){
					searchVenue = conditions.get("searchVenue");
				}
				conditions.remove("searchVenue");
				
				String searchYear1 = "";
				if(conditions.containsKey("searchYear1") && !conditions.get("searchYear1").isEmpty()){
					searchYear1 = conditions.get("searchYear1");
				}
				conditions.remove("searchYear1");
				
				String searchYear2 = "";
				if(conditions.containsKey("searchYear2") && !conditions.get("searchYear2").isEmpty()){
					searchYear2 = conditions.get("searchYear2");
				}
				conditions.remove("searchYear2");
				//------------
				String searchType= "";
				if(!conditions.isEmpty()){
					searchType = "[";
					Iterator<String> keyI = conditions.keySet().iterator();
					String lastType = keyI.next();
					while(keyI.hasNext()){   //pattern matching would not work somehow.
						searchType+="self::"+keyI.next()+" or ";
					}
					searchType+="self::"+lastType+"]";
				}
				String xpString = "/dblp/*"+searchType+suffix;
				
				//String xpString = "/dblp/*[author[text()'Sanjeev Saxena']]";
				//String xpString = "/dblp/*[self::article or self::book or self::incollection][author[contains(text(),'Karel')]  and  title[contains(text(),'HDTOL')] ]";
				log("\n\nString is : "+xpString);
				XPath xpath = xPathFactory.newXPath();
				XPathExpression expr;
				try {
					expr = xpath.compile(xpString);
					NodeList exprResult = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
					log("\n\nthere are many results found: "+exprResult.getLength());
					
					boolean year1 = (!searchYear1.equals(""));
					boolean year2 = (!searchYear2.equals(""));
					
					for(int counter = 0; counter < exprResult.getLength(); counter++){
						Node n = exprResult.item(counter);
						String expString = "/dblp/"+n.getNodeName()+"[@key=\""+n.getAttributes().getNamedItem("key").getTextContent()+"\"]";
						Item item = new Item(n,expString);
						
						String year = item.tryGet("year");
						try{
							if(year1){
								if(year==null || (Integer.parseInt(year.trim()) < Integer.parseInt(searchYear1))){
									continue;
								}
							}
							if(year2){
								if(year==null || (Integer.parseInt(year.trim()) > Integer.parseInt(searchYear2))){
									continue;
								}
							}
							if(!searchVenue.equals("")){
								String t = item.getPubltype();
								if(t.equals("incollection")||t.equals("proceedings")||t.equals("inproceedings")){
									if(!item.tryGet("booktitle").contains(searchVenue)){
										continue;
									}
								}else if(t.equals("article")){
									if(!item.tryGet("journal").contains(searchVenue)){
										continue;
									}
								}else if(t.equals("mastersthesis")||t.equals("phdthesis")){
									if(!item.tryGet("school").contains(searchVenue)){
										continue;
									}
								}else if(t.equals("book")){
									if(!item.tryGet("series").contains(searchVenue)){
										continue;
									}
								}else{
									log("\n\nunknown type or www: "+t);
									continue;
								}
							}
						}catch(Exception e){
							continue;
						}
						itemList.add(new Item(n,expString));
					}
					
				} catch (XPathExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
			}else if(buttonName.equals("Cart")){
				nextPage = "cart.jsp";
				
				for(Cookie c : request.getCookies()){
					
					if( c.getName().matches("^cartItemHtmlXpath[0-9]+$")){
						//	log(c.getValue());
							String xpString = convertHtmlXpath(c.getValue()) ;
						//	log(xpString+"\n\n");
							try {
								XPath xpath = xPathFactory.newXPath();
								XPathExpression expr = xpath.compile(xpString);  
								Node exprResult = (Node) expr.evaluate(doc, XPathConstants.NODE);
								itemList.add(new Item(exprResult,xpString));
							} catch (Exception e) {e.printStackTrace();}
					}
				}
			}else if(buttonName.equals("Home")){
				for(Node n : getRamdomElements(10,nodeList)){
					String expString = "/dblp/"+n.getNodeName()+"[@key=\""+n.getAttributes().getNamedItem("key").getTextContent()+"\"]";
					itemList.add(new Item(n,expString));
				//	log("\n\n"+expString+"\n\n");
				}
			}else if(buttonName.equals("Remove from cart")){
				nextPage = "cart.jsp";
				HashSet<String> removeHtmlXpaths = new HashSet<String>() ;
				Enumeration<String> paramNames = request.getParameterNames();
				while(paramNames.hasMoreElements()){
					String paramName =  paramNames.nextElement();
					if(paramName.startsWith("cartHtmlXpath") ){
						removeHtmlXpaths.add(request.getParameter(paramName));
						log("\n\nThis has been parsed to the hash:"+request.getParameter(paramName));
					}
				}
				
				for(Cookie c : request.getCookies()){
					if( c.getName().startsWith("cartItemHtmlXpath")){
						String xpString = convertHtmlXpath(c.getValue()) ;
						if(removeHtmlXpaths.contains(c.getValue())){//removed
							//log("\n\nThis html cookie has been removed from the hash:"+c.getValue());
								//remove the cookie
								c.setValue("");
								c.setMaxAge(0);
								response.addCookie(c);
						}else{
							
							try {
								XPath xpath = xPathFactory.newXPath();
								XPathExpression expr = xpath.compile(xpString);  
								Node exprResult = (Node) expr.evaluate(doc, XPathConstants.NODE);
								itemList.add(new Item(exprResult,xpString));
							} catch (Exception e) {e.printStackTrace();}
						}
					}
				}				
				
			}
		}else if(request.getParameter("xpath") != null){//clicked on a textlink which leads to itemPage
				String xpString = convertHtmlXpath(request.getParameter("xpath")) ;
//				log("xp is :"+xpString);
				try {
					XPath xpath = xPathFactory.newXPath();
					XPathExpression expr = xpath.compile(xpString);  
					Node exprResult = (Node) expr.evaluate(doc, XPathConstants.NODE);
					Item item = new Item(exprResult,xpString);
					request.setAttribute("item",item);//might be better to direct from search.jsp to itemPage.jsp.
					nextPage = "itemPage.jsp";
				} catch (Exception e) {e.printStackTrace();}
				
		}else{//search/home or initpage
			for(Node n : getRamdomElements(10,nodeList)){
				String expString = "/dblp/"+n.getNodeName()+"[@key=\""+n.getAttributes().getNamedItem("key").getTextContent()+"\"]";
				itemList.add(new Item(n,expString));
			}
		}
		
		 request.setAttribute("itemList",itemList);
		 RequestDispatcher rd = request.getRequestDispatcher("/"+nextPage);
		 rd.forward(request, response);	
	}
	
	
	
	
	
	
	
	
	private LinkedList<Node> getRamdomElements(int num, NodeList nodeList){
		Random r = new Random(); 
		Integer maxExclusive = nodeList.getLength();
		HashSet<Integer> indexesH = new HashSet<Integer> ();

		while(indexesH.size()<num){
			indexesH.add(r.nextInt(maxExclusive));
		}
		ArrayList<Integer> indexesL = (new ArrayList<Integer>(indexesH));
		Collections.sort(indexesL);
		
		LinkedList<Node> random10Nodes = new LinkedList<Node>();
		for(Integer i : indexesL){
			
			random10Nodes.add(nodeList.item(i));
		}
		
		return random10Nodes;
	}
	
	/**
	 * search base on the result of last search
	 * @param ori
	 * @return
	 */
	private Vector<Item> refinedSearch(Vector<Item> ori){
		return null;
	}
	
	/**
	 * primary search
	 * get input from form from search page, ant perform search. 
	 * @return
	 */
	private  Vector<Item> Vector(){
		
		return null;
	}
	
	private String convertHtmlXpath(String xp){
		String temp = xp;
		temp =temp.replaceAll("&","");
		temp = temp.replaceAll("apos;","'");
		temp = temp.replaceAll("quot;","\"");
		temp =	temp.replaceAll("amp;","&");
		temp =	temp.replaceAll("lt;","<");
		temp = temp.replaceAll("at;",">");		

		return temp;
		
	}
	
}
