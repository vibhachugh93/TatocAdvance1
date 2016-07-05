package advanced;




import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

//import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.json.JSONException;
//import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
//import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;

public class AdvancedP {


	public static void main(String[] args) throws InterruptedException, ClientProtocolException, IOException, ParseException
	{

		File fExecutable=new File("/home/vibhachugh/firefox/firefox");
		FirefoxBinary ffBinary=new FirefoxBinary(fExecutable);
		FirefoxProfile ffProfile=new FirefoxProfile();
		WebDriver driver=new FirefoxDriver(ffBinary,ffProfile);

		driver.get("http://10.0.1.86/tatoc/advanced/hover/menu");

		//Problem 1
		Actions actions=new Actions(driver);
		WebElement mainmenu=driver.findElement(By.className("menutitle"));
		actions.moveToElement(mainmenu);
		WebElement submenu=driver.findElement(By.xpath("html/body/div/div[2]/div[2]/span[5]"));
		actions.moveToElement(submenu);
		actions.click().build().perform();

		//Problem 2
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		ResultSet rs = null;		
		int id = 0;
		ResultSet rs1 = null;		
		String s=driver.findElement(By.cssSelector("#symboldisplay")).getText();
		System.out.println(s);
		String name=null;
		String pk=null;
		try{  
			Class.forName("com.mysql.jdbc.Driver");  

			Connection con=DriverManager.getConnection("jdbc:mysql://10.0.1.86/tatoc","tatocuser","tatoc01");  

			Statement stmt=con.createStatement();  
			rs = stmt.executeQuery("SELECT id from identity where symbol='" +s+"'");

			while (rs.next()) {
				id = rs.getInt(1);	
				System.out.println("ID: " + id );
			}
			//	rs.close();
			rs1 =stmt.executeQuery("SELECT name,passkey from credentials where id="+id+"");
			while (rs1.next()){
				name=rs1.getString("name");
				pk=rs1.getString("passkey");
				System.out.println("Name" + name + "Passkey" + pk);
			}

			driver.findElement(By.id("name")).sendKeys(name);
			driver.findElement(By.id("passkey")).sendKeys(pk);
			driver.findElement(By.id("submit")).click();

		}catch(Exception e){ System.out.println(e);}
		Thread.sleep(2000);

		//Problem 3
		double totalTime=0;
		JavascriptExecutor js=(JavascriptExecutor)driver;
		if (driver instanceof JavascriptExecutor)
		{
			totalTime=(double)js.executeScript("return player.getTotalTime()");
			System.out.println(totalTime);
			js.executeScript("player.play()");


		} 
		else 
		{
			throw new IllegalStateException("This driver does not support JavaScript!");
		}


		// boolean value;
		/* if(js.executeScript(player.play()[value]==true))
    		   {
    	         driver.findElement(By.linkText("gonext")).click();
    		   }*/
		Thread.sleep((long) ((totalTime+1)*1000));

		driver.findElement(By.linkText("Proceed")).click();

		// Problem 4
		Thread.sleep(2000);
		String sessid = driver.findElement(By.id("session_id")).getText();
		sessid = sessid.substring(12,sessid.length());
		String Resturl = "http://10.0.1.86/tatoc/advanced/rest/service/token/"+sessid;

		URL url = new URL( Resturl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader in = new BufferedReader(
				new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		System.out.println(response.toString());
		String res=new String(response);
		System.out.println(res);
		//JSONObject obj=new JSONObject();
		//res=(String) obj.get("token");

		//System.out.println(res);
      //  String[] res2=res.split("{,}");
        //String[] res1=res.split(",");
       // System.out.println(res2[0]);
       // System.out.println(res2[1]);
        
		JSONParser parsor = new JSONParser();
		JSONObject obj= (JSONObject) parsor.parse(res);
		String signature= (String )obj.get("token");
		URL url1 = new URL("http://10.0.1.86/tatoc/advanced/rest/service/register");
		HttpURLConnection conn1 = (HttpURLConnection) url1.openConnection();


		conn1.setRequestMethod("POST");

		conn1.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        
		String urlParameters = "id="+sessid+"&signature="+signature+"&allow_access=1";
		System.out.println(urlParameters);
		conn1.setDoOutput(true);
		DataOutputStream wr =new DataOutputStream(conn1.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = conn1.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in1 = new BufferedReader(
		        new InputStreamReader(conn.getInputStream()));
		String inputLine1;
		StringBuffer response1 = new StringBuffer();

		while ((inputLine1 = in1.readLine()) != null) {
			response1.append(inputLine1);
		}
		in1.close();
		
		//print result
		System.out.println(response1.toString());

		conn.disconnect();

	    driver.findElement(By.cssSelector(".page a")).click();

		//conn1.disconnect();
	//driver.findElement(By.linkText("Proceed")).click();
		//driver.findElement(By.cssSelector(".page a")).click();


		//Problem 5
		Thread.sleep(2000);
		driver.findElement(By.cssSelector(".page a")).click();
		Thread.sleep(2000);
		BufferedReader br = null;
		String strng=null, sCurrentLine;
		try 
		{
			int i=0;
			br = new BufferedReader(new FileReader("file_handle_test.dat"));
			while ((sCurrentLine = br.readLine()) != null) 
			{
				if(i==2)
					strng = sCurrentLine;
				i++;
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		strng = strng.substring(11,strng.length());
		Thread.sleep(2000);
		driver.findElement(By.id("signature")).sendKeys(strng);
		driver.findElement(By.className("submit")).click();

	}}















