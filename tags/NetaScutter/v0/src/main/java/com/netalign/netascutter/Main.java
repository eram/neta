package com.netalign.netascutter;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.netalign.netascutter.utils.PtswReaderThread;

/**
 * TODO bug - nodes' cck data is not shown although exists in db
 * TODO return codes in restapi - do not return page not found when not founding requested object - return some http code
 * @author yoavram
 */
public class Main {

    /**
     * @param args the command line arguments - seed urls
     */
    public static void main(String[] args) throws Exception {   
    	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("META-INF/netalign/beans.xml");
    	Scutter scut = (Scutter)context.getBean("scutter");
    	 	
        //String[] urls = {"http://www.aifb.uni-karlsruhe.de/Personen/viewPersonFOAF/foaf_2107.rdf"};        
        //String[] urls = {"http://danbri.org/foaf.rdf"};
        //String[] urls = {"http://www.johnbreslin.com/blog/index.php?sioc_type=user&sioc_id=1", "http://www.johnbreslin.com/blog/index.php?sioc_type=post&sioc_id=1575"};
       	//String[] urls = {"http://lukav.com/wordpress/index.php?sioc_type=site"};
    	//String[] urls = {"http://www.mikeaxelrod.com/index.php?sioc_type=post&sioc_id=71"};
    	//scut.go(urls);
    	
    	PtswReaderThread ptsw = new PtswReaderThread(scut, "ptsw_all_ping.xml");    	
    	ptsw.defaultInit();
    	ptsw.start();
    	scut.start();
    }
}
