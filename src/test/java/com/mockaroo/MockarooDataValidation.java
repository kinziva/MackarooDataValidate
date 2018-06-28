package com.mockaroo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;

public class MockarooDataValidation {
	WebDriver driver;
	List<String> linesList = new ArrayList<String>();
	List<String> cityList = new ArrayList<String>();
	List<String> countryList = new ArrayList<String>();
	List<String> uniqueCityList = new ArrayList<String>();
	List<String> uniqueCountryList = new ArrayList<String>();
	Set<String> countrySet;
	Set<String> citySet;

	@BeforeClass
	public void setUpClass() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.manage().window().fullscreen();
	}

	// 2
	@BeforeMethod
	public void navigateHomePage() {
		driver.get("https://mockaroo.com/");
	}

	// 3
	@Test(priority = 1)
	public void validateTitle() {
		String actual = driver.getTitle();
		Assert.assertEquals(actual, "Mockaroo - Random Data Generator and API Mocking Tool | JSON / CSV / SQL / Excel");
	}

	// 4
	@Test(priority = 2)
	public void validateMackaroo() {
		String mackaroo = driver.findElement(By.cssSelector(".brand")).getText();
		Assert.assertTrue(mackaroo.equalsIgnoreCase("Mockaroo"));

		String RealisticDataGenerato = driver.findElement(By.cssSelector(".tagline")).getText();
		Assert.assertTrue(RealisticDataGenerato.equalsIgnoreCase("Realistic Data Generator"));
	}

	// 6
	@Test(priority = 4)
	public void displayFieldTypeOptions() {
		Assert.assertTrue(driver.findElement(By.cssSelector(".column.column-header.column-name")).getText()
				.equalsIgnoreCase("Field Name"));
		Assert.assertTrue(driver.findElement(By.cssSelector(".column.column-header.column-type")).getText()
				.equalsIgnoreCase("Type"));
		Assert.assertTrue(driver.findElement(By.cssSelector(".column.column-header.column-options")).getText()
				.equalsIgnoreCase("Options"));
	}

	// 7
	@Test(priority = 5)
	public void isEnabled() {
		Assert.assertTrue(driver
				.findElement(By.xpath("//a[@class= 'btn btn-default add-column-btn add_nested_fields']")).isEnabled());
	}

	// 8. Assert that default number of rows is 1000.
	@Test(priority = 6)
	public void isRow1000() {
		Assert.assertEquals(driver.findElement(By.xpath("//input[@id='num_rows']")).getAttribute("value"), "1000");
	}

	// 9. Assert that default format selection is CSV (dropdown)
	@Test(priority = 7)
	public void isFormatCSV() {
		// Select format = new
		// Select(driver.findElement(By.xpath("//select[@id='schema_file_format']")));
		// Assert.assertTrue(format.getFirstSelectedOption().getText().equalsIgnoreCase("csv"));
		Assert.assertTrue(driver.findElement(By.xpath("//select[@id='schema_file_format']/option[1]"))
				.getAttribute("value").equalsIgnoreCase("CSV"));
	}

	// 10. Assert that Line Ending is Unix(LF)
	@Test(priority = 8)
	public void isUnix() {
		Select format = new Select(driver.findElement(By.xpath("//select[@id='schema_line_ending']")));
		Assert.assertTrue(format.getFirstSelectedOption().getText().equalsIgnoreCase("UNIX (LF)"));
	}

	// 11. Assert that header checkbox is checked and BOM is unchecked
	@Test(priority = 9)
	public void isHeaderCheckedBOMunchecked() {
		Assert.assertTrue(driver.findElement(By.xpath("//input[@id='schema_include_header']")).isSelected());
		Assert.assertFalse(driver.findElement(By.xpath("//input[@id='schema_bom']")).isSelected());
	}

	// 5
	@Test(priority = 10)
	public void removeFields() throws InterruptedException {
		List<WebElement> elements = driver
				.findElements(By.xpath("//a[@class='close remove-field remove_nested_fields']"));
		for (WebElement each : elements) {
			each.click();
		}
		// 12
		driver.findElement(By.xpath("//a[@class= 'btn btn-default add-column-btn add_nested_fields']"))
				.sendKeys(Keys.ENTER + "city");
		// 13
		driver.findElement(By.xpath("(//input[@class='btn btn-default'])[7]")).click();// why
		// driver.findElement(By.xpath("//i[@class= 'fa fa-folder-open']")).click();
		// driver.findElement(By.xpath("//input[@class= 'btn btn-default']")).click();
		// driver.findElement(By.xpath("//div[@class='column column-type']")).click();
		Thread.sleep(1000);
		Assert.assertTrue(
				driver.findElement(By.xpath("//h3[@class='modal-title']")).getText().equalsIgnoreCase("Choose a Type"));
		// 14
		driver.findElement(By.xpath("//input[@id= 'type_search_field']")).sendKeys("city");
		// driver.findElement(By.className("type-name")).click();
		driver.findElement(By.xpath("//div[@class= 'type']")).click();

		// 15
		driver.findElement(By.xpath("//a[@class = 'btn btn-default add-column-btn add_nested_fields']"))
				.sendKeys(Keys.ENTER + "country");
		Thread.sleep(1000);
		driver.findElement(By.xpath("(//input[@class='btn btn-default'])[8]")).click();
		Thread.sleep(1000);
		driver.findElement(By.id("type_search_field")).clear();
		driver.findElement(By.xpath("//input[@id= 'type_search_field']")).sendKeys("country");
		driver.findElement(By.xpath("//div[@class= 'type']")).click();
		Thread.sleep(1000);

		// 16 Click on Download Data.
		driver.findElement(By.xpath("//button[@id= 'download']")).click();
	}

	@Test(priority = 11)
	public void file() throws IOException {
		
		// 17. Open the downloaded file using BufferedReader.
		FileReader fr = new FileReader("/Users/haticeevci/Downloads/MOCK_DATA.csv");
		BufferedReader br = new BufferedReader(fr);
		String actual = br.readLine();

		// 18. Assert that first row is matching with Field names that we selected.
		Assert.assertTrue(actual.equalsIgnoreCase("City,Country"));

		// loading all lines to allList arrayList
		String line = br.readLine();
		while (line != null) {
			linesList.add(line);
			line = br.readLine();
		}
		// 19//Assert that there are 1000 records
		Assert.assertEquals(linesList.size(), 1000);
		System.out.println(linesList.size());

		// 20. From file add all Cities to Cities ArrayList
		for (String eachCity : linesList) {
			cityList.add(eachCity.substring(0, eachCity.indexOf(",")));
		}

		// 21. Add all countries to Countries ArrayList
		for (String eachCountry : linesList) {
			countryList.add(eachCountry.substring(eachCountry.indexOf(",") + 1));
		}

		// 22. Sort all cities and find the city with the longest name and shortest name
		Collections.sort(cityList);

		String maxname = cityList.get(1);
		String minname = cityList.get(0);

		for (String eachCity : cityList) {
			if (eachCity.length() > maxname.length()) {
				maxname = eachCity;
			}
			if (eachCity.length() < minname.length()) {
				minname = eachCity;
			}
		}
		System.out.println("Max City Name: " + maxname);
		System.out.println("Min City Name: " + minname);

		// 26. Add all Countries to countrySet HashSet
		countrySet = new HashSet<String>(countryList);

		// 23. In Countries ArrayList, find how many times each Country is mentioned.
		// and print out ex: Indonesia-10 etc
		for (String eachCountry : countrySet)
			System.out.println("Country: " + eachCountry + " " + Collections.frequency(countryList, eachCountry));

		// 24. From file add all Cities to citiesSet HashSet
		citySet = new HashSet<String>(cityList);

		// 25. Count how many unique cities are in Cities ArrayList and assert that
		// it is matching with the count of citiesSet HashSet.
		System.out.println(citySet.size());
		System.out.println(cityList.size());
		Assert.assertNotEquals(citySet, cityList.size());

		// 27. Count how many unique cities are in Countries ArrayList and assert that
		// it is matching with the count of countrySet HashSet.
		System.out.println("country count at set list: " + countrySet.size());
		System.out.println("country count at list: " + countryList.size());
		Assert.assertNotEquals(countrySet, countryList.size());
	}

	@AfterClass
	public void tearDown() {
		driver.close();
	}
}
