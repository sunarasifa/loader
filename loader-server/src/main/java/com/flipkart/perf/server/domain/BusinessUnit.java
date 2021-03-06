package com.flipkart.perf.server.domain;

import com.flipkart.perf.common.util.FileHelper;
import com.flipkart.perf.server.config.LoaderServerConfiguration;
import com.flipkart.perf.server.util.ObjectMapperUtil;
import com.flipkart.perf.server.util.ResponseBuilder;
import javax.ws.rs.WebApplicationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Represent Business Unit of a company and teams within
 */
public class BusinessUnit {
    private String name;
    private Map<String, Team> teams;

    public String getName() {
        return name;
    }

    public BusinessUnit setName(String name) {
        this.name = name;
        return this;
    }

    public Map<String, Team> getTeams() {
        return teams;
    }

    public Team getTeam(String teamName) {
        return teams.get(teamName);
    }

    public BusinessUnit setTeams(Map<String,Team> teams) {
        this.teams = teams;
        return this;
    }

    /**
     * Check it business unit directory exists
     * @return
     */
    public boolean exists() {
        return new File(LoaderServerConfiguration.instance().getJobFSConfig().getBusinessUnitFile(this.getName())).exists();
    }

    public void delete() {
        FileHelper.remove(LoaderServerConfiguration.instance().getJobFSConfig().getBusinessUnitFile(name));
    }

    public boolean teamExist(String team) {
        return this.teams.containsKey(team);
    }

    /**
     * Create Teams is as good as adding new Teams
     * @param newTeams
     * @return
     * @throws IOException
     */
    public BusinessUnit addTeams(Map<String, Team> newTeams) throws IOException {
        this.teams.putAll(newTeams);
        this.persist();
        return this;
    }

    /**
     * Create team is as good as adding new Teams
     * @param team
     * @return
     * @throws IOException
     */
    public BusinessUnit addTeam(Team team) throws IOException {
        this.teams.put(team.getName(), team);
        this.persist();
        return this;
    }

    public BusinessUnit addRuns(String teamName, List<String> runs) throws IOException {
        for(String run : runs) {
            if(!this.teams.get(teamName).getRuns().contains(run)) {
                this.teams.get(teamName).getRuns().add(run);
                this.persist();
            }
        }
        return this;
    }

    public BusinessUnit deleteTeam(String teamName) throws IOException {
        this.teams.remove(teamName);
        this.persist();
        return this;
    }

    public static Map<String, BusinessUnit> all() throws IOException {
        File businessUnitsPath = new File(LoaderServerConfiguration.instance().getJobFSConfig().getBusinessUnitsPath());
        Map<String, BusinessUnit> bus = new HashMap<String, BusinessUnit>();
        if(businessUnitsPath.exists()) {
            for(String businessUnitName : businessUnitsPath.list()) {
                bus.put(businessUnitName, build(businessUnitName));
            }
        }
        return bus;
    }

    /**
     * Create Business Unit and Team Directories
     */
    public BusinessUnit persist() throws IOException {
        ObjectMapperUtil.instance().
                writerWithDefaultPrettyPrinter().
                writeValue(new File(LoaderServerConfiguration.instance().getJobFSConfig().getBusinessUnitFile(this.getName())), this);
        return this;
    }

    public static BusinessUnit build(String businessUnitName) throws IOException {
        File businessUnitFile = new File(LoaderServerConfiguration.instance().getJobFSConfig().getBusinessUnitFile(businessUnitName));
        if(businessUnitFile.exists())
            return ObjectMapperUtil.instance().
                readValue(businessUnitFile, BusinessUnit.class);
        return null;
    }

    public static BusinessUnit businessUnitExistsOrException(String businessUnit) throws IOException {
        if(!new File(LoaderServerConfiguration.instance().getJobFSConfig().getBusinessUnitFile(businessUnit)).exists()) {
            throw new WebApplicationException(ResponseBuilder.resourceNotFound("BusinessUnit", businessUnit));
        }
        return BusinessUnit.build(businessUnit);
    }

    public Team teamExistOrException(String teamName) {
        if(!this.teamExist(teamName))
            throw new WebApplicationException(ResponseBuilder.resourceNotFound("Team", teamName));
        return this.getTeam(teamName);
    }
}
