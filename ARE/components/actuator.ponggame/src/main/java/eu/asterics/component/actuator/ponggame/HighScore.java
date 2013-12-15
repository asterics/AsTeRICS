package eu.asterics.component.actuator.ponggame;

public 	class HighScore implements Comparable
{
	String name = "";
	int score = 0;
	
	public HighScore()
	{
		
	}
	
	public HighScore(String name, int score)
	{
		this.score = score;
		this.name = name;
	}

	@Override
	public int compareTo(Object o) 
	{
		if (o instanceof HighScore)
		{
			HighScore s = (HighScore) o;
			if (this.score > s.score)
				return -1;
			if (this.score < s.score)
				return 1;
			
			return 0;
		}
		throw new ClassCastException();
	}
}
