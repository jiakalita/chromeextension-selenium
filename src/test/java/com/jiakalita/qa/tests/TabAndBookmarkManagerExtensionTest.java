package com.jiakalita.qa.tests;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TabAndBookmarkManagerExtensionTest {

	WebDriver driver;

	@BeforeMethod
	public void setUp() throws Exception {
		System.setProperty("webdriver.chrome.driver", "/Users/parijatkalita/Downloads/chromedriver");
		// driver = new ChromeDriver();

		ChromeOptions options = new ChromeOptions();
		options.addExtensions(new File("/Users/parijatkalita/Downloads/aicaflgmmblfaneodjfhkilgplnpjmig.crx"));
		options.addArguments("--disable-notifications");
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		driver = new ChromeDriver(capabilities);

		System.out.println("Opening extension");
		driver.get("chrome-extension://aicaflgmmblfaneodjfhkilgplnpjmig/board-detail.html");
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		// js = (JavascriptExecutor) driver;
		driver.findElement(By.cssSelector("#choose_bn_skip")).click();
		driver.findElement(By.cssSelector(".introjs-button.introjs-skipbutton")).click();
	}

	@AfterMethod
	public void cleanUp() throws Exception {
		driver.close();
		driver.quit();
	}

	private void navigateToExtension() {
		((JavascriptExecutor) driver).executeScript(
				"window.open('chrome-extension://aicaflgmmblfaneodjfhkilgplnpjmig/board-detail.html', '_blank');");
		// new WebDriverWait(driver, 20).until
		((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
	}

	private void addNewBookmark(String bookmarkTitle, String bookmarkURL) {
		driver.findElements(By.xpath("//button[.='Add']")).get(0).click();
		driver.findElement(By.cssSelector("#createNew #entry_title")).sendKeys(bookmarkTitle);
		driver.findElement(By.cssSelector("#createNew #entry_website_url")).sendKeys(bookmarkURL);
		driver.findElement(By.cssSelector("#createNew button[type='submit']")).click();
	}

	private boolean isBookmarkPresent(String locator) {
		try {
			driver.findElement(
					By.xpath(String.format(".list-group-item.entry-block.ui-state-default a[href='%s']", locator)));
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	private void openTabAtGivenIndex(int tabNo) {
		WebElement openedTabsElement = driver.findElement(By.cssSelector("#showCrntSsion_container span"));
		new WebDriverWait(driver, 20).until(ExpectedConditions.visibilityOf(openedTabsElement));
		new WebDriverWait(driver, 20).until(ExpectedConditions.elementToBeClickable(openedTabsElement));
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", openedTabsElement);
		// new
		// Actions(driver).moveToElement(openedTabsElement).click().build().perform();
		WebElement requiredTab = driver
				.findElements(By.cssSelector("li.list-group-item.pointer.entry-block.tabs a[href]")).get(tabNo);
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", requiredTab);
	}

	private void openTabsWithGivenUrls(String[] urls) {

		for (int i = 0; i < urls.length; ++i) {
			((JavascriptExecutor) driver).executeScript("window.open('" + urls[i] + "', '_blank');");
			// new WebDriverWait(driver, 20).until(
			// driver -> ((JavascriptExecutor) driver).executeScript("return
			// document.readyState").equals("complete"));
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	/*
	 * Open bunch of tabs with different urls and invoke saka and switch to the
	 * fourth result.
	 */

	@Test
	public void addBookmark() throws InterruptedException, IOException {
		String title = driver.getTitle();
		System.out.println("title is: " + title);
		String bookmarkURL = "https://www.google.com";
		WebDriverWait wait = new WebDriverWait(driver, 5);
		addNewBookmark("Test bookmark", bookmarkURL);
		assertTrue(wait
				.until(ExpectedConditions.visibilityOf(driver.findElement(
						By.cssSelector(".list-group-item.entry-block.ui-state-default a[href='" + bookmarkURL + "'"))))
				.isDisplayed());
	}

	@Test
	public void addTabsAndSwitchToFourthResult() throws InterruptedException, IOException {
		String[] urls = { "https://www.javatpoint.com/selenium-features", "https://www.github.com",
				"https://www.webkul.com/", "https://www.google.com" };
		openTabsWithGivenUrls(urls);
		navigateToExtension();
		openTabAtGivenIndex(4);
	}

	@Test
	public void deleteNewlyAddedBookmark() throws InterruptedException, IOException {
		String newBookmarkUrl = "https://www.github.com";
		addNewBookmark("Test bookmark2", newBookmarkUrl);
		WebElement deleteIcon = driver.findElement(
				By.xpath(String.format("//a[@href='%s']/ancestor::li//img[@alt='delete btn']", newBookmarkUrl)));
		// new Actions(driver).moveToElement(deleteIcon).click().build().perform();
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", deleteIcon);
		assertFalse(isBookmarkPresent(newBookmarkUrl));
	}
	/*
	 * public static void takeScreenshot(String fileName) throws IOException { //
	 * Take screenshot and store as a file format File src = ((TakesScreenshot)
	 * driver).getScreenshotAs(OutputType.FILE); // now copy the screenshot to
	 * desired location using copyFile //method FileUtils.copyFile(src, new File(
	 * "/Users/NaveenKhunteta/Documents/MyPOMFramework/PageObjectModel/screenshots/"
	 * + fileName + ".png"));
	 * 
	 * }
	 */

}