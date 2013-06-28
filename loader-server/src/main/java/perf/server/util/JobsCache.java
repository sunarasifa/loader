package perf.server.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import perf.server.config.JobFSConfig;
import perf.server.domain.Job;
import perf.server.domain.ResourceCollectionInstance;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created with IntelliJ IDEA.
 * User: nitinka
 * Date: 26/6/13
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class JobsCache {
    private static ObjectMapper objectMapper;
    private static LoadingCache<String, Job> jobs;

    static {
        objectMapper = new ObjectMapper();
        DateFormat dateFormat = new SimpleDateFormat("MMM dd hh:mm:ss z yyyy");
        objectMapper.setDateFormat(dateFormat);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    }

    public static void initiateCache(final JobFSConfig jobFSConfig) {
        jobs = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .build(
                        new CacheLoader<String, Job>() {
                            public Job load(String jobId) throws IOException {
                                return objectMapper.readValue(new File(jobFSConfig.getJobStatusFile(jobId)), Job.class);
                            }
                        });
    }

    public static Job getJob(String jobId) throws ExecutionException {
        return jobs.get(jobId);
    }

}