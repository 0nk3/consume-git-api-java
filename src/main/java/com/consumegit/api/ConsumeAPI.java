package com.consumegit.api;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.http.HttpRequest;
import java.util.Scanner;

import static java.lang.System.*;

public class ConsumeAPI {
    /**
     * Using the git api to GET all pull requests filtered by date
     *  (closed, opened and merged)
     *  The date input is expected to take the ISO8601 Standard i.e YYYY-MM-DD
     * */
    static final Scanner kb = new Scanner(in);
    private static final Logger LOGGER = LogManager.getLogger(ConsumeAPI.class.getName());
    static  String username;
    static String repo;
    static String startDate;
    static String endDate;

    public static void main(String[] args) {
        //user input
        out.println("Please enter the user name : ");
        username = kb.nextLine();
        out.println("Please enter the name of the repo : ");
        repo = kb.nextLine();
        out.println("Enter the start date (YYYY-MM-DD) : ");
        startDate = kb.nextLine();
        out.println("Enter the end date (YYYY-MM-DD)");
        endDate = kb.nextLine();

        // build url based on input by calling the filter method
        final String url = filter(username, repo, startDate, endDate);

        try{
            StringBuilder output = new StringBuilder();

            Process process = Runtime.getRuntime().exec(url);
            process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            int counter = 0;
            while ((line=reader.readLine())!=null){
                output.append(line).append("\n");
                if(line.contains("repo")){
                    counter++;
                }
            }
            out.println("Counter : " + counter);
            if(process.waitFor() == 0){
                out.println(output);
            }else {
                err.println("Something went wrong . . . ");
            }
            process.destroy(); // close the processes
        }catch (IOException | InterruptedException e){
            LOGGER.info("Possible Issues : \nInternet Connection\nInvalid URL\nInterrupted Exception ");
        }
    }
    public static String filter(String username, String repo, String startDate, String endDate){
        // building the url
        // Given a repo, for an PRs to be updated or merged or closed, it has to be created first hence the use of "created" only
        // i to get the header to see status 200 OK if connection successfully established
        return "curl -i -X GET https://api.github.com/repos/" + username + "/" + repo +"/pulls?state=all" + ",created:" + startDate + ".." + endDate;
    }

}
