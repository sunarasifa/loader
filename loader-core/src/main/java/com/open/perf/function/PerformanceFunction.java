package com.open.perf.function;

import com.open.perf.core.FunctionContext;
import org.apache.log4j.Logger;

import java.util.LinkedHashMap;

abstract public class PerformanceFunction {
    protected static Logger logger = Logger.getLogger(PerformanceFunction.class);

    /**
     * Will Be called only once. Before the actual run starts.
     * See it as an initialization area
     * You could set any variables that would need when the execute function is called.
     * @param context
     * @throws Exception
     */
    abstract public void init(FunctionContext context) throws Exception;

    /**
     * Logic that user wants to execute as part of performance run
     * @param context
     * @throws Exception
     */
    abstract public void execute(FunctionContext context) throws Exception;

    /**
     * Will Be called only once. After Run is over.
     * See it as a clean up section to release resources
     * @param context
     * @throws Exception
     */
    abstract public void end(FunctionContext context) throws Exception;

    /**
     * Can be used by user to explicitly find out input parameters for a function
     * @return
     */
    public LinkedHashMap<String, FunctionParameter> inputParameters(){
        return new LinkedHashMap<String, FunctionParameter>();
    }

    /**
     * Can be used by user to explicitly find out output parameters for a function
     * @return
     */
    public LinkedHashMap<String, FunctionParameter> outputParameters(){
        return new LinkedHashMap<String, FunctionParameter>();
    }
}