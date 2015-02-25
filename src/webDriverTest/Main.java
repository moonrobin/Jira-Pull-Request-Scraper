package webDriverTest;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		System.out.println("Please enter Jira credentials:");
		System.out.println("==============================");
		System.out.print("Username: ");
		String JiraUserName = input.nextLine();
		System.out.print("Password: ");
		String JiraPassWord = input.nextLine();
		input.close();

		for (int i = 0; i < 1000; i++) {
			System.out.println("\b");
		}

		WebDriver driver = new ChromeDriver();
		driver.navigate().to("https://loblaw.atlassian.net");

		String title = driver.getTitle();
		if (title.equals("Atlassian Cloud")) {
			driver.findElement(By.id("username")).sendKeys(JiraUserName);
			driver.findElement(By.id("password")).sendKeys(JiraPassWord);
			driver.findElement(By.id("login")).click();
		}

		else {
			System.out.println("Already logged in");
		}

		driver.navigate()
				.to("https://loblaw.atlassian.net/issues/?filter=15513&jql=project%20%3D%20%22Click%20%26%20Collect%22%20AND%20fixVersion%20%3D%20earliestUnreleasedVersion()%20AND%20Status%20in%20(merged%2C%20Resolved%2C%20Closed%2C%20Done)");
		title = driver.getTitle();
		if (title.equals("Atlassian Cloud")) {
			System.out.println("Invalid Credentials.");
			System.exit(-1);
		}
		
		String format = driver.findElement(By.id("jira")).getAttribute("class");
		if (format
				.equals("aui-layout aui-theme-default ka ajax-issue-search-and-view page-type-navigator page-type-split")) {
			driver.findElement(By.className("header-section-primary"))
					.sendKeys("t");
		}

		List<WebElement> tickets = driver.findElements(By
				.cssSelector("table#issuetable tbody tr"));
		int ticketCount = tickets.size();
		ArrayList<Ticket> ticketList = new ArrayList<Ticket>(ticketCount);

		for (WebElement issue : tickets) {
			String issueKey = issue.getAttribute("data-issuekey");
			String ticketLink = "https://loblaw.atlassian.net/browse/"
					+ issueKey + "?devStatusDetailDialog=pullrequest";
			Ticket currentTicket = new Ticket(issueKey, ticketLink, "");
			ticketList.add(currentTicket);
		}

		driver.navigate().to(ticketList.get(0).getTicketLink());
		String fixVer = driver.findElement(
				By.cssSelector("#fixVersions-field [title]")).getText();

		System.out.println("Found " + ticketCount
				+ " tickets ready for release in fix version " + fixVer + ".");
		System.out.println("Working...");
		int i = 0;
		for (Ticket ticket : ticketList) {
			driver.navigate().to(ticket.getTicketLink());
			List<WebElement> possiblePRs = driver.findElements(By
					.cssSelector(".pullrequest-row"));
			if (possiblePRs.size() > 1) {
				
				for (Iterator<WebElement> PRiterator = possiblePRs.iterator() ; PRiterator.hasNext();) {
					String status = PRiterator.next().findElement(By.cssSelector(".state span")).getText();
					if (status.equals("DECLINED")) {
						PRiterator.remove();
					}
				}
				
				if (possiblePRs.size() > 1) {
					int maxID = 0;
					for (WebElement pullRequest : possiblePRs) {
						int pullRequestID = Integer.parseInt(pullRequest.findElement(By.cssSelector(".pullrequest-id a")).getText().substring(1));
						if (pullRequestID > maxID) {
							maxID = pullRequestID;
						}
					}
					
					for (Iterator<WebElement> PRiterator = possiblePRs.iterator() ; PRiterator.hasNext();) {
						int pullRequestID = Integer.parseInt(PRiterator.next().findElement(By.cssSelector(".pullrequest-id a")).getText().substring(1));
						if (pullRequestID != maxID) {
							PRiterator.remove();
						}
					}
				}

				String pullRequestLink = possiblePRs.get(0).findElement(By.cssSelector(".pullrequest-id a")).getAttribute("href");
				ticketList.get(i).setPullRequestLink(pullRequestLink);
				i++;
			}

			else {
				String pullRequestLink;
				try {
					pullRequestLink = driver.findElement(
							By.className("pullrequest-link")).getAttribute(
							"href");
				} catch (Exception e) {
					pullRequestLink = "No pull request link found on ticket page.";
				}
				ticketList.get(i).setPullRequestLink(pullRequestLink);
				i++;
			}
		}

		String fileName = "CNC_" + fixVer;
		System.out.println("");
		
		File outputFile= new File(fileName + ".txt");
		try {
			PrintWriter output = new PrintWriter(outputFile);
			output.println(fileName);
			System.out.println(fileName);
			for (Ticket ticket : ticketList) {
				System.out.println(ticket.toString());
				output.println(ticket.toString());
			}
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
