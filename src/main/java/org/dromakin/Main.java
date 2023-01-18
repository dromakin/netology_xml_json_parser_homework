package org.dromakin;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final String DATA_XML = "data.xml";
    private static final String JSON_FILENAME = "data.json";

    public static void main(String[] args) {

        try {
            logger.info("Running parse csv");
            List<Employee> list = parseXML(DATA_XML);

            logger.info("List of Employee convert to json string");
            String json = listToJson(list);

            logger.info("write json string to file");

            writeString(json, JSON_FILENAME);
        } catch (ParserException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static List<Employee> parseXML(String fileName) throws ParserException {

        List<Employee> employees = new ArrayList<>();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            // optional, but recommended
            // process XML securely, avoid attacks like XML External Entities (XXE)
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(Paths.get(fileName).toFile());
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("employee");

            for (int temp = 0; temp < list.getLength(); temp++) {

                Node node = list.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;

                    // get text
                    long id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
                    String firstname = element.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastname = element.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = element.getElementsByTagName("country").item(0).getTextContent();
                    int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());

                    employees.add(new Employee(id, firstname, lastname, country, age));

                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ParserException(e.getMessage(), e);
        }

        return employees;
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    private static void writeString(String json, String jsonFilename) throws ParserException {
        try (FileWriter file = new FileWriter(jsonFilename)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            throw new ParserException(e.getMessage(), e);
        }
    }

}
