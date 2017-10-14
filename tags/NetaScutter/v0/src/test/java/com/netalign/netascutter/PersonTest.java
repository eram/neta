package com.netalign.netascutter;

import com.netalign.sioc.IFoafPerson;

import junit.framework.TestCase;
import java.util.*;

/**
 * 
 * @author ldodds
 */
public class PersonTest extends TestCase
{

    /**
     * Constructor for PersonTest.
     * 
     * @param arg0
     */
    public PersonTest(String arg0)
    {
        super(arg0);
    }

    public static void main(String[] args)
    {
    }

    public void testCompareTo()
    {
        IFoafPerson one = new Person();
        one.setMbox("abc@test.com");

        IFoafPerson two = new Person();
        two.setMbox("def@test.com");

        IFoafPerson three = new Person();

        IFoafPerson four = new Person();
        four.setMbox("abc@test.ca");

        List<IFoafPerson> people = new ArrayList<IFoafPerson>();
        people.add(one);
        people.add(two);
        people.add(three);
        people.add(four);       

    }

}