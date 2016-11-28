package com.clarity;

import java.io.InputStream;

import javax.faces.bean.ManagedBean;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@ManagedBean(name="mapService", eager=true)

public class MapService {
  private static final String APPID = "AIzaSyAQnN7wkvykLeYlbkt-iWVSk8ZH_tjx_4k";
  private static final long serialVersionUID = 1L;
  
  public String getMap() {
	String[] urls = getMap("2033 Dove Creek Ct.", "Loveland", "CO");
	return urls[0];
  }
  public String[] getMap(String streetAddress, String city,
      String state) {
    String[] urls = new String[12];
    boolean cannotAccessWebService = false;
    
    for (int i=1; i <= urls.length; ++i) {
      String document = getMapDocumentFromWebService(
          streetAddress, city, state, APPID, i);
      
      if (document == null) {
        cannotAccessWebService = true;
        break;
      }
      urls[i-1] = document;
    }
    
    if (cannotAccessWebService) {
      for (int i=1; i <= urls.length; ++i) {
        urls[i-1] = "images/maps/map-" + i + ".jpeg";
      }      
    }
    return urls;
  }
  private String getMapUrlFromDocument(Document document) {
    NodeList result =
        (NodeList) document.getElementsByTagName("Result");

    Element mapUrl = (Element) result.item(0);
    return mapUrl.getFirstChild().getNodeValue();
  }
  private String getMapDocumentFromWebService(
      String streetAddress, String city, String state,
      String appid, int zoomLevel) {
    String url =
        "https://maps.googleapis.com/maps/api/staticmap?center=" + city + "," + streetAddress + 
         "," + state +"&zoom="+zoomLevel+
        "&size=640x480&markers=size:mid%7Ccolor:red%7C"+city+","+streetAddress+"," + state +
        "&key=" + appid ;
    return url;
  }
  private Document getDocumentFromUrl(String url) {
    HttpClient client = new HttpClient();
    GetMethod get = new GetMethod(url);
    Document document = null;

    try {
      int result = client.executeMethod(get);
      if (result == 200) {
        InputStream in = get.getResponseBodyAsStream();
        document =
            DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(in);
      }
    }
    catch (Exception e) {
      return null;
    }
    return document;
  }
  private String encode(String streetAddress) {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < streetAddress.length(); ++i) {
      if (streetAddress.charAt(i) == ' ')
        buffer.append('+');
      else
        buffer.append(streetAddress.charAt(i));
    }
    return buffer.toString();
  }
}
