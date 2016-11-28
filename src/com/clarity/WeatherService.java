package com.clarity;

import java.io.InputStream;

import javax.faces.bean.ManagedBean;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@ManagedBean(name="weatherService", eager=true)

public class WeatherService {
  private static final String YAHOO_APPLICATION_ID =
      "dj0yJmk9YWdnSkdybXJSM2dwJmQ9WVdrOWVURlFNVXQxTjJzbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD01Yw--";
  private static final String WEATHER_BASE_URL =
		  "http://weather.yahooapis.com/" + "forecastrss?";
  private static final long serialVersionUID = 1L;

  public String getWeatherForZip(String city,
      boolean isFarenheit) {
    String url =   "https://query.yahooapis.com/v1/public/yql?q=select%20item.condition.text%2Citem.title%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22"+city+"%22)&format=xml&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
    	
//        WEATHER_BASE_URL + "appid=" + YAHOO_APPLICATION_ID
//            + "&" + "p=" + zip + "&u="
//            + (isFarenheit ? "f" : "c");
    		
    return getWeatherFromDocument(getWeatherDocument(url));
  }
  private String getWeatherFromDocument(Document document) {
    Element item =
        (Element) document.getElementsByTagName("item")
            .item(0);

    String title =
        ((Element) item.getElementsByTagName("title").item(0))
            .getFirstChild().getNodeValue();

    Element description =
        (Element) item.getElementsByTagName("yweather:condition")
            .item(0);

    return "<div class='heading'>" + title + "</div>"
        + "<hr/>"
        + description.getAttribute("text");
        }
  private Document getWeatherDocument(String url) {
    Document document = null;
    try {
      HttpClient client = new HttpClient(); // Jakarta Commons
      GetMethod gm = new GetMethod(url);
      if (HttpServletResponse.SC_OK == client
          .executeMethod(gm)) {
        InputStream in = gm.getResponseBodyAsStream();
        document =
            DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(in);
      }
    }
    catch (Exception e) {
      try {
        document =
            DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(
                    "cannedForecast.xml");
      }
      catch (Exception e1) {
        e1.printStackTrace();
      }
    }
    return document;
  }
}
