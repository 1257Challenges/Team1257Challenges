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
	
	//evaluates v given 3 team numbers a, b, and c
	public static double evalV(String a, String b, String c)
	{
		double c1 = 0, c2 = 0, c3 = 0, v1 = 0, v2 = 0, v3 = 0;
		
		String[] teams = {a, b, c};
		double[] winLossRatios = { 0.0, 0.0, 0.0 };
		
		c1 = stats.getJSONObject("ccwms").getDouble(a);//get CCWM values for each team from the stats JSONObject
		c2 = stats.getJSONObject("ccwms").getDouble(b);
		c3 = stats.getJSONObject("ccwms").getDouble(c);
		
		for(int j = 0; j < teams.length; j++)//for each team
		{
			for(int i = 0; i < rankings.length(); i++)//for each entry in the event rankings
			{
				String temp = rankings.getJSONArray(i).getString(1);
				if(temp.equals(teams[j]))//if this entry is the correct team
				{
					double wins = Double.parseDouble(rankings.getJSONArray(i).getString(7).split("-")[0]);//get the number of wins from the w-l-t string
					double losses = Double.parseDouble(rankings.getJSONArray(i).getString(7).split("-")[1]);//get the number of losses from the w-l-t string
					if(losses == 0.0)//prevents infinite scores
					{
						winLossRatios[j] = 1.0;
					}
					else//calculate win loss ratio
					{
						winLossRatios[j] = wins/losses;
					}
				}
			}
		}
		
		v1 = c1 + (35 * (Math.log(winLossRatios[0]) / Math.log(10.0)));//uses the team data to create a v value for the alliance
		v2 = c2 + (35 * (Math.log(winLossRatios[1]) / Math.log(10.0)));
		v3 = c3 + (35 * (Math.log(winLossRatios[2]) / Math.log(10.0)));
		return Math.pow(1.01, v1 + v2 + v3);//returns the rating for the alliance
	}
	public static void main(String[] args) throws IOException
	{
		try
		{
			while(true)
			{
				Scanner reader = new Scanner(System.in);
		    	System.out.print("Enter the event name (i.e. njfla, njbri): ");
		    	String event = reader.nextLine();
		    	
		    	//creates an HTTP request for stats from the event specified
		        URL url = new URL("http://thebluealliance.com/api/v2/event/2016" + event + "/stats?X-TBA-App-Id=frc1257:challengeapp:1c");
		        URLConnection hc = url.openConnection();
		        hc.setRequestProperty("User-Agent", "Team 1257 Prediction App");
		        InputStream ins = hc.getInputStream();
		        InputStreamReader isr = new InputStreamReader(ins);
		        BufferedReader in = new BufferedReader(isr);
		        stats = new JSONObject(in.readLine());
		        //creates an HTTP request for rankings from the event specified
		        url = new URL("http://thebluealliance.com/api/v2/event/2016" + event + "/rankings?X-TBA-App-Id=frc1257:challengeapp:1c");
		        hc = url.openConnection();
		        hc.setRequestProperty("User-Agent", "Team 1257 Prediction App");
		        ins = hc.getInputStream();
		        isr = new InputStreamReader(ins);
		        in = new BufferedReader(isr);
		        rankings = new JSONArray(in.readLine());
		        
		        for(;;)//loop until the person types "n" or they close the program
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
				        
				        System.out.print("Enter match number (finals match 1 is match number 1): ");
			        	int matchNum = reader.nextInt() - 1; //take into account zero-based indexing
			        	String[] blueTeams = new String[3];
			        	String[] redTeams = new String[3];
			        	//get the three blue teams from the event list and print them out
			        	for (int i = 0; i < blueTeams.length; i++)
			        	{
			        		blueTeams[i] = matchList.getJSONObject(matchNum).getJSONObject("alliances").getJSONObject("blue").getJSONArray("teams").getString(i).substring(3);
			        		System.out.println(blueTeams[i]);
			        	}
			        	//get the three red teams from the event list and print them out
			        	for (int i = 0; i < redTeams.length; i++)
			        	{
			        		redTeams[i] = matchList.getJSONObject(matchNum).getJSONObject("alliances").getJSONObject("red").getJSONArray("teams").getString(i).substring(3);
			        		System.out.println(redTeams[i]);
			        	}
			        	double vBlue = evalV(blueTeams[0], blueTeams[1], blueTeams[2]);//evaluate the v values for both alliances
						double vRed = evalV(redTeams[0], redTeams[1], redTeams[2]);
						double result = 100 * (vBlue / (vBlue + vRed));//use these v values to form a probability
						System.out.println(result + "% chance the blue alliance will win.");
			        }
			        
			        //procedure for a custom match
			        else if(checkByMatches.equalsIgnoreCase("c") || checkByMatches.equalsIgnoreCase("custom"))
			        {
			        	//take inputs for the numbers of the teams on each alliance
			        	System.out.print("Enter first team in the blue alliance: ");
			        	String bt1 = reader.nextLine();
			        	System.out.print("Enter second team in the blue alliance: ");
			        	String bt2 = reader.nextLine();
			        	System.out.print("Enter third team in the blue alliance: ");
						String bt3 = reader.nextLine();
						System.out.print("Enter first team in the red alliance: ");
						String rt1 = reader.nextLine();
						System.out.print("Enter second team in the red alliance: ");
						String rt2 = reader.nextLine();
						System.out.print("Enter third team in the red alliance: ");
						String rt3 = reader.nextLine();

						double vBlue = evalV(bt1, bt2, bt3);//evaluate the v values for both alliances
						double vRed = evalV(rt1, rt2, rt3);
						double result = 100 * (vBlue / (vBlue + vRed));//use these v values to form a probability
						System.out.println(result + "% chance the blue alliance will win.");
			        }
			        
			        //if the user didn't specify a mode, ask again
			        else
			        {
			        	continue;
			        }
			        
			        //ask if they want to keep looping
			        System.out.println("Would you like to continue predicting matches (y/n)? ");
					String doContinue = reader.nextLine();
					if(doContinue.equalsIgnoreCase("y") || doContinue.equalsIgnoreCase("yes"))
					{
						continue;
					}
					else
					{
						reader.close();
						in.close();
						break;
					}
		        }
			}
        }
		catch(IOException e)
		{
            e.printStackTrace();
        }
	}
}
