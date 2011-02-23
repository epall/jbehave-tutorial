package org.jbehave.tutorials.etsy;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.jbehave.web.selenium.DelegatingWebDriverProvider;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;

public class SauceWebDriverProvider extends DelegatingWebDriverProvider {

    public void initialize() {
        try {
            final String username = getProperty("SAUCE_USERNAME");
            final String access_key = getProperty("SAUCE_ACCESS_KEY");

            if(username == null) {
                throw new UnsupportedOperationException(
                        "SAUCE_USERNAME environment variable not specified");
            }
            if(access_key == null) {
                throw new UnsupportedOperationException(
                        "SAUCE_ACCESS_KEY environment variable not specified");
            }
    
            final DesiredCapabilities desiredCapabilities = DesiredCapabilities.firefox();
            desiredCapabilities.setVersion("3.6.");
            desiredCapabilities.setPlatform(Platform.WINDOWS);
            desiredCapabilities.setCapability("name", "JBehave");
            delegate = new ThreadLocal<WebDriver>() {
              @Override protected RemoteWebDriver initialValue() {
                try {
                  return new RemoteWebDriver(new URL("http://" + username + ":" + access_key
                        + "@ondemand.saucelabs.com/wd/hub"), desiredCapabilities);
                } catch(MalformedURLException e) {
                  throw new UnsupportedOperationException(e);
                } catch(UnsupportedOperationException e) {
                  throw e;
                } catch(Exception e) {
                  e.printStackTrace();
                  throw new UnsupportedOperationException(e);
                }
              }
            };
            //openBrowserToJobPage(((RemoteWebDriver) delegate).getSessionId());
        } catch(UnsupportedOperationException e) {
            throw e;
        } catch(Exception e) {
            e.printStackTrace();
            throw new UnsupportedOperationException(e);
        }
    }

    private String getProperty(String key){
      String value = System.getProperty(key);
      if(value == null) {
        // Other Sauce clients use environment variables for credentials
        value = System.getenv(key);
      }
      return value;
    }

    private void openBrowserToJobPage(SessionId sessionId) {
        if(!java.awt.Desktop.isDesktopSupported()) {
            return;
        }
        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

        if(!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
            return;
        }

        java.net.URI uri;
        try {
            uri = new java.net.URI("http://saucelabs.com/jobs/"+sessionId.toString());
            desktop.browse(uri);
        } catch(URISyntaxException e) {
            return;
        } catch(IOException e) {
            return;
        }
    }
}
