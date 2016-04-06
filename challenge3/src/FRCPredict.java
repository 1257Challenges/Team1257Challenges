import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.URL;
import java.net.URLConnection;
import org.json.*;

public class FRCPredict
{	
	//This will the event stats from TBA to get ccwm's for each team
	public static JSONObject stats;
	//This will the event rankings from TBA to get the wins and losses for each team
	public static JSONArray rankings;
	
	//evaluates v given 3 team numbers a, b, and c
	public static double evalV(String a, String b, String c)
	{
		double c1 = 0, c2 = 0, c3 = 0, v1 = 0, v2 = 0, v3 = 0;
		
		String[] teams = {a, b, c};
		double[] winLossRatios = { 0.0, 0.0, 0.0 };
		
		c1 = stats.getJSONObject("ccwms").getDouble(a);
		c2 = stats.getJSONObject("ccwms").getDouble(b);
		c3 = stats.getJSONObject("ccwms").getDouble(c);
		
		for(int j = 0; j < teams.length; j++)
		{
			for(int i = 0; i < rankings.length(); i++)
			{
				String temp = rankings.getJSONArray(i).getString(1);
				if(temp.equals(teams[j]))
				{
					double wins = Double.parseDouble(rankings.getJSONArray(i).getString(7).split("-")[0]);
					double losses = Double.parseDouble(rankings.getJSONArray(i).getString(7).split("-")[1]);
					if(losses == 0.0){
						winLossRatios[j] = 1.0;
					}
					winLossRatios[j] = wins/losses;
				}
			}
		}
		
		v1 = c1 + (35 * (Math.log(winLossRatios[0]) / Math.log(10.0)));//uses the team data to create a v value for the alliance
		v2 = c2 + (35 * (Math.log(winLossRatios[1]) / Math.log(10.0)));
		v3 = c3 + (35 * (Math.log(winLossRatios[2]) / Math.log(10.0)));
		return Math.pow(1.01, v1 + v2 + v3);
	}
	public static void main(String[] args) throws IOException
	{
		try
		{
			Scanner reader = new Scanner(System.in);
	    	System.out.print("Enter the event name (i.e. njfla, njbri): ");
	    	String event = reader.nextLine();
	    	
	        URL url = new URL("http://thebluealliance.com/api/v2/event/2016" + event + "/stats?X-TBA-App-Id=frc1257:challengeapp:1c");
	        URLConnection hc = url.openConnection();
	        hc.setRequestProperty("User-Agent", "Team 1257");
	        InputStream ins = hc.getInputStream();
	        InputStreamReader isr = new InputStreamReader(ins);
	        BufferedReader in = new BufferedReader(isr);
	        stats = new JSONObject(in.readLine());
	        
	        url = new URL("http://thebluealliance.com/api/v2/event/2016" + event + "/rankings?X-TBA-App-Id=frc1257:challengeapp:1c");
	        hc = url.openConnection();
	        hc.setRequestProperty("User-Agent", "Team 1257");
	        ins = hc.getInputStream();
	        isr = new InputStreamReader(ins);
	        in = new BufferedReader(isr);
	        rankings = new JSONArray(in.readLine());
	        
	        System.out.print("Enter first team in the blue alliance: ");
			String bt1 = reader.nextLine();//take inputs for the numbers of the teams on each alliance
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
			reader.close();
        }
		catch(IOException e)
		{
            e.printStackTrace();
        }
	}
}
