package com.flipkart.perf.core;

import com.flipkart.perf.domain.Group;
import com.flipkart.perf.domain.GroupFunction;
import com.flipkart.perf.domain.Load;
import com.flipkart.perf.operation.MathAddFunction;

/**
 * Created with IntelliJ IDEA.
 * User: nitinka
 * Date: 26/6/13
 * Time: 10:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class VerySmallTPSTest {
    public static void main(String[] args) throws Exception {
        Load load = new Load().
                addGroup(new Group().
                        setName("G1").
                        setRepeats(5).
                        setThroughput(0.05f).
                        setThreads(1).
                        addFunction(new GroupFunction().
                                setFunctionClass(MathAddFunction.class.getCanonicalName()).
                                setFunctionalityName("List Add").
                                addParam(MathAddFunction.IP_NUM_JSON_LIST, "[1,2,3,4,5,6,7,8,9,10]")));
        load.start(""+System.currentTimeMillis());
    }
}