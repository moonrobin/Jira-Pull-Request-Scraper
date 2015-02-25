package webDriverTest;

public class Ticket {
	public static String padRight(String s, int n) {
	    int i = 0; 
		while (i<n) {
			s = s + " ";
			i++;
		}

		return s;
	}

	private String issueKey;
	private String ticketLink;
	private String pullRequestLink;
	
	public String getIssueKey() {
		return issueKey;
	}

	public void setIssueKey(String issueKey) {
		this.issueKey = issueKey;
	}

	public String getTicketLink() {
		return ticketLink;
	}

	public void setTicketLink(String ticketLink) {
		this.ticketLink = ticketLink;
	}

	public String getPullRequestLink() {
		return pullRequestLink;
	}

	public void setPullRequestLink(String pullRequestLink) {
		this.pullRequestLink = pullRequestLink;
	}

	
	public Ticket(String issueKey, String ticketLink, String pullRequestLink) {
		this.issueKey = issueKey;
		this.ticketLink = ticketLink;
		this.pullRequestLink = pullRequestLink;
	}
	
	public Ticket () {
		this.issueKey = "";
		this.ticketLink = "";
		this.pullRequestLink = "";
	}
	@Override public String toString() {
		return padRight(this.issueKey, (13-this.issueKey.length())) + pullRequestLink;
	}
}
