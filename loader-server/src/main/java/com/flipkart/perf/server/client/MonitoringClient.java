package com.flipkart.perf.server.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flipkart.perf.server.domain.MetricPublisherRequest;
import com.flipkart.perf.server.domain.OnDemandCollectorRequest;
import com.flipkart.perf.server.util.ObjectMapperUtil;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

/**
 * Created with IntelliJ IDEA.
 * User: nitinka
 * Date: 7/2/13
 * Time: 11:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class MonitoringClient {
    private String host;
    private int port;
    private static Logger log = LoggerFactory.getLogger(MonitoringClient.class);

    private static final String RESOURCE_RESOURCES = "/monitoring-service/resources";
    private static final String RESOURCE_RESOURCE = "/monitoring-service/resources/{resources}";
    private static final String RESOURCE_ON_DEMAND_RESOURCES = "/monitoring-service/onDemandResources";
    private static final String RESOURCE_ON_DEMAND_RESOURCE = "/monitoring-service/onDemandResources/{resource}";
    private static final String RESOURCE_ON_DEMAND_RESOURCES_REQUESTS = "/monitoring-service/onDemandResources/requests";
    private static final String RESOURCE_ON_DEMAND_RESOURCES_REQUEST = "/monitoring-service/onDemandResources/requests/{requestId}";
    private static final String RESOURCE_PUBLISH_RESOURCE_REQUESTS = "/monitoring-service/publishResourcesRequests";
    private static final String RESOURCE_PUBLISH_RESOURCE_REQUEST = "/monitoring-service/publishResourcesRequests/{requestId}";
    private static ObjectMapper objectMapper = ObjectMapperUtil.instance();

    public MonitoringClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public MonitoringClient setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public MonitoringClient setPort(int port) {
        this.port = port;
        return this;
    }

    public List<String> getResources() throws ExecutionException, InterruptedException, IOException {
        List<String> resources = new ArrayList<String>();
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        try {
        	AsyncHttpClient.BoundRequestBuilder b = asyncHttpClient.
        			prepareGet("http://"+this.getHost()+":" +
        					this.getPort() +
        					RESOURCE_RESOURCES).
        					setHeader("Content-Type", MediaType.APPLICATION_JSON);

        	Future<Response> r = b.execute();
        	r.get();
        	if(r.get().getStatusCode() == 200) {
        		resources = objectMapper.readValue(r.get().getResponseBody(), List.class);
        	}
        	else {
        		log.error("Get on "+RESOURCE_RESOURCES);
        	}
        } finally {
        	asyncHttpClient.close();
        }

        return resources;
    }

    /**
     *
     * @param resourceList Comma Separated
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     */
    public Map getResources(String resourceList, int count) throws ExecutionException, InterruptedException, IOException {
    	Map resources = new HashMap();
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        try {
        	AsyncHttpClient.BoundRequestBuilder b = asyncHttpClient.
        			prepareGet("http://"+this.getHost()+":" +
        					this.getPort() +
        					RESOURCE_RESOURCE.
        					replace("{resources}",resourceList) +
        					"?count="+count).
        					setHeader("Content-Type", MediaType.APPLICATION_JSON);

        	Future<Response> r = b.execute();
        	r.get();
        	if(r.get().getStatusCode() == 200) {
        		resources = objectMapper.readValue(r.get().getResponseBody(), Map.class);
        	}
        	else {
        		log.error("Get on "+RESOURCE_RESOURCE.
        				replace("{resources}", resourceList));
        	}
        } finally {
        	asyncHttpClient.close();
        }

        return resources;
    }

    public List getOnDemandResources() throws IOException, ExecutionException, InterruptedException {
        List resources = new ArrayList();
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        try {
        	AsyncHttpClient.BoundRequestBuilder b = asyncHttpClient.
        			prepareGet("http://"+this.getHost()+":" +
        					this.getPort() +
        					RESOURCE_ON_DEMAND_RESOURCES).
        					setHeader("Content-Type", MediaType.APPLICATION_JSON);

        	Future<Response> r = b.execute();
        	r.get();
        	if(r.get().getStatusCode() == 200) {
        		resources = objectMapper.readValue(r.get().getResponseBody(), List.class);
        	}
        	else {
        		log.error("Get on "+RESOURCE_ON_DEMAND_RESOURCES);
        	}
        } finally {
        	asyncHttpClient.close();
        }

        return resources;

    }

    public Map getOnDemandResource(String resource) throws IOException, ExecutionException, InterruptedException {
        Map resourceInfo = new HashMap();
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        try {
        	AsyncHttpClient.BoundRequestBuilder b = asyncHttpClient.
        			prepareGet("http://"+this.getHost()+":" +
        					this.getPort() +
        					RESOURCE_ON_DEMAND_RESOURCE.
        					replace("{resource}", resource)).
        					setHeader("Content-Type", MediaType.APPLICATION_JSON);

        	Future<Response> r = b.execute();
        	r.get();
        	if(r.get().getStatusCode() == 200) {
        		resourceInfo = objectMapper.readValue(r.get().getResponseBody(), Map.class);
        	}
        	else {
        		log.error("Get on "+RESOURCE_ON_DEMAND_RESOURCE);
        	}
        } finally {
        	asyncHttpClient.close();
        }

        return resourceInfo;

    }

    public void raiseOnDemandResourceRequest(OnDemandCollectorRequest request) throws IOException, ExecutionException, InterruptedException {
        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        AsyncHttpClient.BoundRequestBuilder b = asyncHttpClient.
                preparePost("http://"+this.getHost()+":" +
                        this.getPort() +
                        RESOURCE_ON_DEMAND_RESOURCES_REQUESTS).
                setHeader("Content-Type", MediaType.APPLICATION_JSON).
                setBody(objectMapper.writeValueAsBytes(request));

        Future<Response> r = b.execute(new AsyncCompletionHandler<Response>() {

			@Override
			public Response onCompleted(Response r) throws Exception {
				if(r.getStatusCode() != 204) {
		            log.error("Post on "+RESOURCE_ON_DEMAND_RESOURCES_REQUESTS);
		        }
				asyncHttpClient.close();
				return r;
			}
		});
    }

    public void deleteOnDemandResourceRequest(final String requestId) throws IOException, ExecutionException, InterruptedException {
        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        AsyncHttpClient.BoundRequestBuilder b = asyncHttpClient.
                prepareDelete("http://" + this.getHost() + ":" +
                        this.getPort() +
                        RESOURCE_ON_DEMAND_RESOURCES_REQUEST.
                                replace("{requestId}", requestId)).
                setHeader("Content-Type", MediaType.APPLICATION_JSON);

        Future<Response> r = b.execute(new AsyncCompletionHandler<Response>() {

			@Override
			public Response onCompleted(Response r) throws Exception {
				if(r.getStatusCode() != 204) {
		            log.error("Post on "+RESOURCE_ON_DEMAND_RESOURCES_REQUEST.
		                    replace("{requestId}", requestId));
		        }
				asyncHttpClient.close();
				return r;
			}
		});
    }

    public void raiseMetricPublishRequest(MetricPublisherRequest request) throws IOException, ExecutionException, InterruptedException {
        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        AsyncHttpClient.BoundRequestBuilder b = asyncHttpClient.
                preparePost("http://"+this.getHost()+":" +
                        this.getPort() +
                        RESOURCE_PUBLISH_RESOURCE_REQUESTS).
                setHeader("Content-Type", MediaType.APPLICATION_JSON).
                setBody(objectMapper.writeValueAsBytes(request));

        Future<Response> r = b.execute(new AsyncCompletionHandler<Response>() {

			@Override
			public Response onCompleted(Response r) throws Exception {
				if(r.getStatusCode() != 204) {
		            log.error("Post on "+RESOURCE_PUBLISH_RESOURCE_REQUESTS);
		        }
		        asyncHttpClient.close();
				return r;
			}
		});        

    }

    public void deletePublishResourceRequest(final String requestId) throws IOException, ExecutionException, InterruptedException {
        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        AsyncHttpClient.BoundRequestBuilder b = asyncHttpClient.
                prepareDelete("http://" + this.getHost() + ":" +
                        this.getPort() +
                        RESOURCE_PUBLISH_RESOURCE_REQUEST.
                                replace("{requestId}", requestId)).
                setHeader("Content-Type", MediaType.APPLICATION_JSON);

        Future<Response> r = b.execute(new AsyncCompletionHandler<Response>() {

			@Override
			public Response onCompleted(Response r) throws Exception {
				if(r.getStatusCode() != 204) {
		            log.error("Post on "+RESOURCE_PUBLISH_RESOURCE_REQUEST.
		                    replace("{requestId}", requestId));
		        }
		        asyncHttpClient.close();
				return r;
			}
		});
        
    }
}
