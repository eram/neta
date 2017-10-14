/**
 * NetaScutter v0.1
 */
package com.netalign.netascutter;


import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.log4j.Logger;

import com.netalign.netascutter.utils.PtswReaderThread;
import com.netalign.netascutter.utils.TextImporter;
import com.netalign.netascutter.utils.VBulletinImporter;


/**
 * FIXED bug - nodes' cck data is not shown although exists in db
 * TODO return codes in restapi - do not return page not found when not founding requested object - return some http code
 * 
 * @author yoavram
 */
public class Main {
	private static Logger logger = Logger.getLogger(Main.class);
    /**
     * @param args the command line arguments - seed urls
     */
    public static void main(String[] args) throws Exception {  
       	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("META-INF/netalign/beans.xml");
    	Scutter scut = (Scutter)context.getBean("scutter");
    	List<String> urls = new ArrayList<String>();
    	// boards.us
    	/*String[] urls. {"http://www.boards.us/forums/sioc.php?sioc_type=forum&sioc_id=26",
    			"http://www.boards.us/forums/sioc.php?sioc_type=forum&sioc_id=32",
    			"http://www.boards.us/forums/sioc.php?sioc_type=forum&sioc_id=69",
    			"http://www.boards.us/forums/sioc.php?sioc_type=forum&sioc_id=15"
    			}; // finish with seen 1632 URLs, fetched 1744 URLs and parsed 4049 elements    			*/    
    	//urls.add("http://boards.us/forums/sioc.php?sioc_type=post&sioc_id=8937"); // a post 
    	//urls.add("http://boards.us/forums/sioc.php?sioc_type=post&sioc_id=8970"); // comment of the previous post 
    	//urls.add("http://boards.us/forums/sioc.php?sioc_type=forum&sioc_id=91");
    	// a 4 post thread
//    	urls.add("http://boards.us/forums/sioc.php?sioc_type=post&sioc_id=8262"); // parent
//    	urls.add("http://boards.us/forums/sioc.php?sioc_type=post&sioc_id=8441");
//    	urls.add("http://boards.us/forums/sioc.php?sioc_type=post&sioc_id=13990");
//    	urls.add("http://boards.us/forums/sioc.php?sioc_type=post&sioc_id=13996"); // child   	    	
//    	// boards.jp   
//    	urls.add("http://boards.jp/forums/sioc.php?sioc_type=thread&sioc_id=17429"); // a fat thread
//    	urls.add("http://boards.jp/forums/sioc.php?sioc_type=post&sioc_id=215981"); // a deep comment on prev thread
//    	
    	
    	//String[] urls = {"http://www.boards.jp/forums/sioc.php?sioc_type=thread&sioc_id=8535"};    	
    	//String[] urls = {"http://www.boards.jp/forums/sioc.php?sioc_type=forum&sioc_id=15"};
    	//String[] urls = {"http://www.boards.jp/forums/sioc.php?sioc_type=thread&sioc_id=20997"};
    	// former tests
    	//String[] urls = {"http://www.adamtibi.net/sioc.axd?sioc_type=post&sioc_id=91b459a6-2759-447c-93b3-65d05a742358"};
        //String[] urls = {"http://www.aifb.uni-karlsruhe.de/Personen/viewPersonFOAF/foaf_2107.rdf"};        
        //String[] urls = {"http://danbri.org/foaf.rdf"};
        //String[] urls = {"http://www.johnbreslin.com/blog/index.php?sioc_type=user&sioc_id=1", "http://www.johnbreslin.com/blog/index.php?sioc_type=post&sioc_id=1575"};
       	//String[] urls = {"http://lukav.com/wordpress/index.php?sioc_type=site"};
    	//String[] urls = {"http://www.mikeaxelrod.com/index.php?sioc_type=post&sioc_id=71"};
    	//String[] urls = {"http://www.lespacedunmatin.info/blog/sioc.php?type=post&post_id=893"};
    	
    	//urls.add("http://boards.us/forums/sioc.php?sioc_type=post&sioc_id=3787");
    	
    	for (String url : urls) 
    		scut.addURL(url);
    	
    	TextImporter imp = new TextImporter("C:\\Documents and Settings\\user\\Desktop\\empty_nodes_boards_us.csv", scut);
    	if (imp.init())
    		imp.start();
    	
    	/*PtswReaderThread ptsw = new PtswReaderThread(scut, "C:\\neta\\src\\java\\EclipseWorkspace\\NetaScutter\\src\\main\\resources\\ptsw_all_pings.xml");    	
    	ptsw.defaultInit();
    	ptsw.start();*/
    	
    	/*VBulletinImporter vb = new VBulletinImporter(scut);    	
    	vb.start();
    	Thread.sleep(60*1000); // 60 secs  
*/    	    	
    	int elements = -1;
    	while(scut.getNumOfElements() > elements) {
    		elements = scut.getNumOfElements();
    		Thread.sleep(60*1000); // 60 secs    		
    	}   
    	logger.info("Previous num of elements: " + elements + ", now: " + scut.getNumOfElements());
    	//scut.shutdown();
    }
}
