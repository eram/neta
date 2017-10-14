/*
 * This file is in the Public Domain
 */
package com.netalign.netascutter.utils;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.Selector;

/**
 * TODO -- description of ExtendedSelector
 * 
 * @author ldodds
 */
public class ExtendedSelector implements Selector
{
    private Property _property;
    private String _value;
    
    public ExtendedSelector(Property property, String value)
    {
        _property = property;
        _value = value;        
    }
    
    /** 
     * @see com.hp.hpl.jena.rdf.model.Selector#test(com.hp.hpl.jena.rdf.model.Statement)
     */
    public boolean test(Statement statement)
    {
        if (!_property.equals(statement.getPredicate()))
        {
            return false;
        }
        Object object = statement.getObject();
        if (! (object instanceof Resource))
        {
            return _value.equals(object.toString());
        }
        Resource node = (Resource)object;
        return _value.equals(node.getURI());
    }

    /** 
     * @see com.hp.hpl.jena.rdf.model.Selector#isSimple()
     */
    public boolean isSimple()
    {
        return false;
    }

    /** 
     * @see com.hp.hpl.jena.rdf.model.Selector#getSubject()
     */
    public Resource getSubject()
    {
        return null;
    }

    /** 
     * @see com.hp.hpl.jena.rdf.model.Selector#getPredicate()
     */
    public Property getPredicate()
    {
        return _property;
    }

    /** 
     * @see com.hp.hpl.jena.rdf.model.Selector#getObject()
     */
    public RDFNode getObject()
    {
        return null;
    }

}
