import java.io.*;
import java.util.*;
import java.lang.*;

public class FRCPredict
{
	public static double evalV(String a, String b, String c, String[][] ranks)
	{
		double c1 = 0, c2 = 0, c3 = 0, r1 = 0, r2 = 0, r3 = 0, v1 = 0, v2 = 0, v3 = 0;
		for(int i = 0; i < ranks.length; i++)
		{
			if(a.equals(ranks[i][1]))
			{
				double wins = Double.parseDouble(ranks[i][7]);
				double losses = Double.parseDouble(ranks[i][8]);
				c1 = Double.parseDouble(ranks[i][11]);
				if(losses == 0)
				{
					r1 = wins;
				}
				else
				{
					r1 = wins / losses;
				}
				break;
			}
		}
		for(int i = 0; i < ranks.length; i++)
		{
			if(b.equals(ranks[i][1]))
			{
				double wins = Double.parseDouble(ranks[i][7]);
				double losses = Double.parseDouble(ranks[i][8]);
				c2 = Double.parseDouble(ranks[i][11]);
				if(losses == 0)
				{
					r2 = wins;
				}
				else
				{
					r2 = wins / losses;
				}
				break;
			}
		}
		for(int i = 0; i < ranks.length; i++)
		{
			if(c.equals(ranks[i][1]))
			{
				double wins = Double.parseDouble(ranks[i][7]);
				double losses = Double.parseDouble(ranks[i][8]);
				c3 = Double.parseDouble(ranks[i][11]);
				if(losses == 0)
				{
					r3 = wins;
				}
				else
				{
					r3 = wins / losses;
				}
				break;
			}
		}
		v1 = c1 + (35 * (Math.log(r1) / Math.log(10.0)));
		v2 = c2 + (35 * (Math.log(r2) / Math.log(10.0)));
		v3 = c3 + (35 * (Math.log(r3) / Math.log(10.0)));
		return Math.pow(1.01, v1 + v2 + v3);
	}
	public static void main(String[] args) throws IOException
	{
		try
		{
            BufferedReader reader = new BufferedReader(new FileReader("rankings.csv"));
			Console c = System.console();
			
			String[][] ranks = new String[39][12];
			String line = reader.readLine();
			for(int i = 0; i < ranks.length; i++)
			{
				line = reader.readLine();
				ranks[i] = line.split(",");
			}

			String bt1 = c.readLine("Enter the first blue team: ");
			String bt2 = c.readLine("Enter the second blue team: ");
			String bt3 = c.readLine("Enter the third blue team: ");
			String rt1 = c.readLine("Enter the first red team: ");
			String rt2 = c.readLine("Enter the second red team: ");
			String rt3 = c.readLine("Enter the third red team: ");
			
			double vBlue = evalV(bt1, bt2, bt3, ranks);
			double vRed = evalV(rt1, rt2, rt3, ranks);
			double result = 100 * (vBlue / (vBlue + vRed));
			System.out.println(result + "% chance the blue alliance will win.");
			reader.close();
        }
		catch(IOException e)
		{
            e.printStackTrace();
        }
	}
}