package com.flipkart.perf.sample;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.flipkart.perf.sample.config.SampleServiceConfiguration;
import com.flipkart.perf.sample.resource.MemoryHoggerResource;
import com.flipkart.perf.sample.resource.SearchNameResource;
import com.flipkart.perf.sample.search.Search;

public class SampleService  extends Service<SampleServiceConfiguration> {
    public SampleService() {
    }

    @Override
    public void initialize(Bootstrap<SampleServiceConfiguration> bootstrap) {
        bootstrap.setName("sample-service");
    }

    @Override
    public void run(SampleServiceConfiguration configuration, Environment environment) throws Exception {
        Search.loadNames(configuration.getFileContainingNames());
        environment.addResource(new SearchNameResource(configuration.getSearchPoolSize()));
        environment.addResource(new MemoryHoggerResource(configuration.getSearchPoolSize()));
    }

    public static void main(String[] args) throws Exception {
        args = new String[]{"server",args[0]};
        new SampleService().run(args);
    }
}
