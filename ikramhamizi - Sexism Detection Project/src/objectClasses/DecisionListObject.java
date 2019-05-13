package objectClasses;

public class DecisionListObject implements Comparable<DecisionListObject> {

	private String feature, assignedSexistClass;
	private double logScore;
	
	public DecisionListObject(String feature, String assignedSexistClass, double logScore)
	{
		this.feature = feature;
		this.assignedSexistClass = assignedSexistClass;
		this.logScore = logScore;
	}
	
	@Override
	public int compareTo(DecisionListObject DLobj) {
		
		
		if(logScore == DLobj.getLogScore())
			return 0;
		
		if(logScore < DLobj.getLogScore())
			return -1;
		
		return 1;
	}
	
	public String getFeature() {
		return feature;
	}
	
	public String getAssignedSexistClass() {
		return assignedSexistClass;
	}
	
	public double getLogScore() {
		return logScore;
	}
	
	@Override
	public String toString() {
		return feature + " " + assignedSexistClass + " " + logScore + "\n";
	}
}
