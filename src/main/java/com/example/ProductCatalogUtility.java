package com.example;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.util.HashMap;

public class ProductCatalogUtility {

    private String xmlFilePath;
    private HashMap<String, Product> productMap = new HashMap<>();

    public ProductCatalogUtility(String xmlFilePath) {
        this.xmlFilePath = xmlFilePath;
        loadProductsFromXML();
    }

    public int generateNewProductId() {
        int maxId = 0;
        for (Product product : productMap.values()) {
            if (product.getId() > maxId) {
                maxId = product.getId();
            }
        }
        return maxId + 1;
    }

    public void addProductToMap(Product product) {
        productMap.put(product.getName().toLowerCase(), product);
    }

    public void updateProductInMap(Product product) {
        productMap.put(product.getName().toLowerCase(), product);
    }

    public void deleteProductFromMapById(int productId) {
        productMap.values().removeIf(product -> product.getId() == productId);
    }

    public void loadProductsFromXML() {
      try {
          File xmlFile = new File(xmlFilePath);
          if (!xmlFile.exists()) {
              throw new RuntimeException("ProductCatalog.xml not found at path: " + xmlFilePath);
          }

          DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
          DocumentBuilder builder = factory.newDocumentBuilder();
          Document document = builder.parse(xmlFile);

          document.getDocumentElement().normalize();
          NodeList productList = document.getElementsByTagName("product");

          for (int i = 0; i < productList.getLength(); i++) {
              Node node = productList.item(i);

              if (node.getNodeType() == Node.ELEMENT_NODE) {
                  Element productElement = (Element) node;

                  String id = getTagValue("product_id", productElement);
                  String name = getTagValue("product_name", productElement);
                  String price = getTagValue("price", productElement);
                  String description = getTagValue("description", productElement);
                  String manufacturer = getTagValue("manufacturer", productElement);
                  String imageUrl = getTagValue("imageUrl", productElement);
                  String category = getTagValue("category", productElement);
                  String productQuantity = getTagValue("product_quantity", productElement);
                  String onSale = getTagValue("on_sale", productElement);
                  String manufacturerRebate = getTagValue("manufacturer_rebate", productElement);

                  if (id != null && name != null) {
                      Product product = new Product(
                          Integer.parseInt(id),
                          name,
                          Double.parseDouble(price),
                          description,
                          manufacturer,
                          imageUrl,
                          category,
                          Integer.parseInt(productQuantity),
                          Boolean.parseBoolean(onSale),
                          Boolean.parseBoolean(manufacturerRebate)
                      );
                      productMap.put(name.toLowerCase(), product);
                  }
              }
          }
      } catch (Exception e) {
          e.printStackTrace();
      }
    }


    private String getTagValue(String tagName, Element element) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            if (node != null) {
                return node.getTextContent();
            }
        }
        return null;
    }

    public synchronized void addProductToXML(Product product) throws Exception {
        File xmlFile = new File(xmlFilePath);
        if (!xmlFile.exists()) {
            throw new Exception("XML file not found at path: " + xmlFilePath);
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        Element newProduct = doc.createElement("product");

        createChildElement(doc, newProduct, "product_id", String.valueOf(product.getId()));
        createChildElement(doc, newProduct, "product_name", product.getName());
        createChildElement(doc, newProduct, "price", String.valueOf(product.getPrice()));
        createChildElement(doc, newProduct, "description", product.getDescription());
        createChildElement(doc, newProduct, "manufacturer", product.getManufacturer());
        createChildElement(doc, newProduct, "imageUrl", product.getImageUrl());
        createChildElement(doc, newProduct, "category", product.getCategory());
        createChildElement(doc, newProduct, "product_quantity", String.valueOf(product.getProductQuantity()));
        createChildElement(doc, newProduct, "on_sale", String.valueOf(product.isOnSale()));
        createChildElement(doc, newProduct, "manufacturer_rebate", String.valueOf(product.hasManufacturerRebate()));

        doc.getDocumentElement().appendChild(newProduct);

        writeXMLFile(doc, xmlFilePath);
    }


    public synchronized void updateProductInXML(Product product) throws Exception {
      File xmlFile = new File(xmlFilePath);
      if (!xmlFile.exists()) {
          throw new Exception("XML file not found at path: " + xmlFilePath);
      }
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(xmlFile);
      doc.getDocumentElement().normalize();

      NodeList productList = doc.getElementsByTagName("product");

      for (int i = 0; i < productList.getLength(); i++) {
          Node node = productList.item(i);

          if (node.getNodeType() == Node.ELEMENT_NODE) {
              Element productElement = (Element) node;
              String productId = getTagValue("product_id", productElement);

              if (productId.equals(String.valueOf(product.getId()))) {
                  setTagValue("product_name", productElement, product.getName());
                  setTagValue("price", productElement, String.valueOf(product.getPrice()));
                  setTagValue("description", productElement, product.getDescription());
                  setTagValue("manufacturer", productElement, product.getManufacturer());
                  setTagValue("imageUrl", productElement, product.getImageUrl());
                  setTagValue("category", productElement, product.getCategory());
                  setTagValue("product_quantity", productElement, String.valueOf(product.getProductQuantity()));
                  setTagValue("on_sale", productElement, String.valueOf(product.isOnSale()));
                  setTagValue("manufacturer_rebate", productElement, String.valueOf(product.hasManufacturerRebate()));
                  break;
              }
          }
      }

      writeXMLFile(doc, xmlFilePath);
    }


    public synchronized void deleteProductFromXML(int productId) throws Exception {
        File xmlFile = new File(xmlFilePath);
        if (!xmlFile.exists()) {
            throw new Exception("XML file not found at path: " + xmlFilePath);
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        NodeList productList = doc.getElementsByTagName("product");

        for (int i = 0; i < productList.getLength(); i++) {
            Node node = productList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element productElement = (Element) node;
                String pid = getTagValue("product_id", productElement);

                if (pid.equals(String.valueOf(productId))) {
                    productElement.getParentNode().removeChild(productElement);
                    break;
                }
            }
        }

        writeXMLFile(doc, xmlFilePath);
    }

    public synchronized void updateProductQuantityInXML(String productId, int quantityOrdered) throws Exception {
      File xmlFile = new File(xmlFilePath);
      if (!xmlFile.exists()) {
          throw new Exception("XML file not found at path: " + xmlFilePath);
      }

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(xmlFile);
      doc.getDocumentElement().normalize();

      NodeList productList = doc.getElementsByTagName("product");

      for (int i = 0; i < productList.getLength(); i++) {
          Node node = productList.item(i);

          if (node.getNodeType() == Node.ELEMENT_NODE) {
              Element productElement = (Element) node;
              String currentProductId = getTagValue("product_id", productElement);

              if (currentProductId.equals(productId)) {
                  String quantityStr = getTagValue("product_quantity", productElement);
                  int currentQuantity = Integer.parseInt(quantityStr);
                  int updatedQuantity = currentQuantity - quantityOrdered;
                  
                  setTagValue("product_quantity", productElement, String.valueOf(updatedQuantity));
                  break;
              }
          }
      }

      writeXMLFile(doc, xmlFilePath);
    }


    private void createChildElement(Document doc, Element parent, String tagName, String textContent) {
        Element elem = doc.createElement(tagName);
        elem.appendChild(doc.createTextNode(textContent));
        parent.appendChild(elem);
    }

    private void setTagValue(String tagName, Element element, String value) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            nodeList.item(0).setTextContent(value);
        } else {
            createChildElement(element.getOwnerDocument(), element, tagName, value);
        }
    }

    private void writeXMLFile(Document doc, String filePath) throws TransformerException {
        doc.getDocumentElement().normalize();

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    }

    public HashMap<String, Product> getProductMap() {
        return productMap;
    }
}
