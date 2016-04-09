import java.io.*;
import java.util.*;
import java.net.URL;
import java.net.URLConnection;
import org.json.*;

public class FRCPredict
{	
	//This will the event stats from TBA to get ccwm's for each team
	public static JSONObject stats;
	//This will the event rankings from TBA to get the wins and losses for each team
	public static JSONArray rankings;
	//This contains all the matches for an even if selecting a prescheduled match
	public static JSONArray matchList;
	//This contains a list of all FRC matches for 2016
	public static JSONArray eventList;
	
	
	//Go through the stats JSONObject and ensure that the team is at the event
	public static boolean isValidTeam(String teamNumber)
	{
		if(stats.getJSONObject("oprs").has(teamNumber))
			return true;
		return false;
	}
	//Go through the eventList JSONArray and ensure that the match is within a valid range
	public static boolean isValidMatch(int matchNumber)
	{
		if(matchNumber < 0 || matchNumber > (matchList.length() - 1))
			return false;
		return true;
	}
	
	//Go through the eventList JSONArray and ensure that the event listed is valid
	public static boolean isValidEvent(String event)
	{
		//Iterate through all of the JSONObjects until you find a match
		for(int i = 0; i < eventList.length(); i++)
		{
			if(event.equals(eventList.getJSONObject(i).getString("event_code")))
			{
				return true;
			}
		}
		//If no match is found, then the event is invalid (false)
		return false;
	}
	
	
	//evaluates v given 3 team numbers a, b, and c
	public static double evalV(String a, String b, String c)
	{
		double c1 = 0, c2 = 0, c3 = 0, v1 = 0, v2 = 0, v3 = 0;
		
		String[] teams = new String[3];
		double[] winLossRatios = new double[3];
		
		c1 = stats.getJSONObject("ccwms").getDouble(a);//get CCWM values for each team from the stats JSONObject
		c2 = stats.getJSONObject("ccwms").getDouble(b);
		c3 = stats.getJSONObject("ccwms").getDouble(c);
		System.out.println(c1 + "\t" + c2  + "\t" + c3);
		
		for(int j = 0; j < teams.length; j++)//for each team
		{
			for(int i = 0; i < rankings.length(); i++)//for each entry in the event rankings
			{
				String temp = rankings.getJSONArray(i).getString(1);
				if(temp.equals(teams[j]))//if this entry is the correct team
				{
					double wins = Double.parseDouble(rankings.getJSONArray(i).getString(7).split("-")[0]);//get the number of wins from the w-l-t string
					double losses = Double.parseDouble(rankings.getJSONArray(i).getString(7).split("-")[1]);//get the number of losses from the w-l-t string
					if(losses == 0.0)//prevents the program from dividing by 0
					{
						winLossRatios[j] = 1.0;
						System.out.println("Here");
					}
					else//calculate win loss ratio
					{
						winLossRatios[j] = wins/losses;
						System.out.println(wins/losses);
					}
				}
			}
		}
		
		//uses the team data to create a v value for the alliance
		v1 = c1 + (35 * (Math.log(winLossRatios[0]) / Math.log(10.0)));
		v2 = c2 + (35 * (Math.log(winLossRatios[1]) / Math.log(10.0)));
		v3 = c3 + (35 * (Math.log(winLossRatios[2]) / Math.log(10.0)));
		//returns the rating for the alliance
		return Math.pow(1.01, v1 + v2 + v3);
	}
	public static void main(String[] args) throws IOException
	{
		try
		{
				System.out.println("=================================================");
				System.out.println("Welcome to Team 1257's FRC match prediction tool!");
				System.out.println("=================================================\n");
				String event;
				Scanner reader = new Scanner(System.in);
				
				//creates an HTTP request for all of the FRC 2016 events
				URL url = new URL("http://thebluealliance.com/api/v2/events/2016?X-TBA-App-Id=frc1257:challengeapp:1c");
		        URLConnection hc = url.openConnection();
		        hc.setRequestProperty("User-Agent", "Team 1257 Prediction App");
		        InputStream ins = hc.getInputStream();
		        InputStreamReader isr = new InputStreamReader(ins);
		        BufferedReader in = new BufferedReader(isr);
		        eventList = new JSONArray(in.readLine());
				
				while(true)
				{
					System.out.print("Enter the event name (i.e. njfla, njbri): ");
		    		event = reader.nextLine();
		    		if(isValidEvent(event))
		    		{
		    			break;
		    		}
		    		System.out.println("Please enter a valid match name.");
				}
		    	//creates an HTTP request for stats from the event specified
		    	url = new URL("http://thebluealliance.com/api/v2/event/2016" + event + "/stats?X-TBA-App-Id=frc1257:challengeapp:1c");
		        hc = url.openConnection();
		        hc.setRequestProperty("User-Agent", "Team 1257 Prediction App");
		        ins = hc.getInputStream();
		        isr = new InputStreamReader(ins);
		        in = new BufferedReader(isr);
		        stats = new JSONObject(in.readLine());
		    	
		        //creates an HTTP request for rankings from the event specified
		        url = new URL("http://thebluealliance.com/api/v2/event/2016" + event + "/rankings?X-TBA-App-Id=frc1257:challengeapp:1c");
		        hc = url.openConnection();
		        hc.setRequestProperty("User-Agent", "FRC Prediction App");
		        ins = hc.getInputStream();
		        isr = new InputStreamReader(ins);
		        in = new BufferedReader(isr);
		        rankings = new JSONArray(in.readLine());
		        
		        //Continue performing the prediction loop until the user indicates to stop or closes the program
		        while(true)
		        {
		        	System.out.print("Would you like to predict a custom or prescheduled match (c/p)? ");
			        String checkByMatches = reader.nextLine();
			        
			        //procedure for a prescheduled match
			        if(checkByMatches.equalsIgnoreCase("p") || checkByMatches.equalsIgnoreCase("prescheduled") || checkByMatches.equalsIgnoreCase("prescheduled match"))
			        {
			        	//create HTTP request for the list of matches at that event
			        	url = new URL("http://thebluealliance.com/api/v2/event/2016" + event + "/matches?X-TBA-App-Id=frc1257:challengeapp:1c");
				        hc = url.openConnection();
				        hc.setRequestProperty("User-Agent", "Team 1257 Prediction App");
				        ins = hc.getInputStream();
				        isr = new InputStreamReader(ins);
				        in = new BufferedReader(isr);
				        matchList = new JSONArray(in.readLine());
				        int matchNum;
				        while(true)
				        {
				        	System.out.printf("Enter match number (1-%d, where 1 is finals match 1): ", matchList.length());
			        		String tempInput = reader.nextLine(); //take into account zero-based indexing
			        		//Ensures that the input is a valid integer
			        		if(!tempInput.matches("\\d+"))
			        		{
			        			System.out.println("Please enter a valid match number.");
			        			continue;
			        		}
			        		//Set the matchNum to tempInput - 1 to account for 0 based indexing 
			        		matchNum = Integer.parseInt(tempInput) - 1;
			        		if(!isValidMatch(matchNum))
			        		{
			        			System.out.println("Please enter a valid match number.");
			        			continue;
			        		}
			        		break;
				        }
				        
			        	String[] blueTeams = new String[3];
			        	String[] redTeams = new String[3];
			        	
			        	//get the three blue teams from the event list and print them out
			        	for (int i = 0; i < blueTeams.length; i++)
			        	{
			        		blueTeams[i] = matchList.getJSONObject(matchNum).getJSONObject("alliances").getJSONObject("blue").getJSONArray("teams").getString(i).substring(3);
			        		System.out.println("Blue team " + (i+1) + ": " + blueTeams[i]);
			        	}
			        	
			        	//get the three red teams from the event list and print them out
			        	for (int i = 0; i < redTeams.length; i++)
			        	{
			        		redTeams[i] = matchList.getJSONObject(matchNum).getJSONObject("alliances").getJSONObject("red").getJSONArray("teams").getString(i).substring(3);
			        		System.out.println("Red team " + (i+1) + ": " + redTeams[i]);
			        	}
			        	
			        	//evalV for both teams and print the results
			        	double vBlue = evalV(blueTeams[0], blueTeams[1], blueTeams[2]);//evaluate the v values for both alliances
						double vRed = evalV(redTeams[0], redTeams[1], redTeams[2]);
						float result = (float)(100 * (vBlue / (vBlue + vRed)));//use these v values to form a probability
						System.out.printf("There is a %.2f%%  chance that the blue alliance will win.\n", result);
			        }
			        
			        //procedure for a custom match
			        else if(checkByMatches.equalsIgnoreCase("c") || checkByMatches.equalsIgnoreCase("custom"))
			        {
			        	//take inputs for the numbers of the teams on each alliance
			        	String[] blueTeams = new String[3];
			        	String[] redTeams = new String[3];
			        	
			        	//Get the team number for each blue alliance team and ensure the input is valid
			        	for(int i = 0; i < blueTeams.length; i++) {
			        		System.out.printf("Enter team %d in the blue alliance: ", i+1);
			        		blueTeams[i] = reader.nextLine();
			        		if(!isValidTeam(blueTeams[i]))
			        		{
			        			blueTeams[i] = "";
			        			--i;
			        			System.out.printf("Please enter an FRC who attended %s.\n", event);
			        		}
			        	}
			        	//Get the team number for each red alliance team and ensure the input is valid
			        	for(int i = 0; i < redTeams.length; i++) {
			        		System.out.printf("Enter team %d in the red alliance: ", i+1);
			        		redTeams[i] = reader.nextLine();
			        		if(!isValidTeam(redTeams[i]))
			        		{
			        			redTeams[i] = "";
			        			--i;
			        			System.out.printf("Please enter an FRC who attended %s.\n", event);
			        		}
			        	}
			        	
			        	//evalV for both teams and print the results
			        	double vBlue = evalV(blueTeams[0], blueTeams[1], blueTeams[2]);//evaluate the v values for both alliances
						double vRed = evalV(redTeams[0], redTeams[1], redTeams[2]);
						float result = (float)(100 * (vBlue / (vBlue + vRed)));//use these v values to form a probability
						System.out.println(result);
						System.out.println("There is a " + String.format("%4f", result) + "% chance that the blue alliance will win.");
			        }
			        
			        //if the user didn't specify a  valid mode, ask again
			        else
			        {
			        	System.out.println("Please input a valid option.");
			        	continue;
			        }
			        
			        //ask if they want to keep using the prediction tool
			        System.out.print("Would you like to continue predicting matches (y/n)? ");
			        String doContinue = reader.nextLine();
					if(doContinue.equalsIgnoreCase("y") || doContinue.equalsIgnoreCase("yes"))
					{
						continue;
					}
					
					//Assume all other responses to be no
					else
					{
						//Close all readers and end the loop
						reader.close();
						in.close();
						break;
					}
		       }
        }
		catch(IOException e)
		{
            e.printStackTrace();
        }
	}
}
