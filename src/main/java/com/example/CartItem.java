package com.example;
import java.io.Serializable;

public class CartItem implements Serializable {
  private String username;
  private String id;
  private String name;
  private String image;
  private double price;
  private int quantity;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public int getQuantity() {
    return quantity;
  }
  
  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public CartItem(String username, String id, String name, String image, double price, int quantity) {
      this.username = username;
      this.id = id;
      this.name = name;
      this.image = image;
      this.price = price;
      this.quantity = quantity;
  }

  @Override
	public String toString() {
		return "CartItem [username=" + username + ", id=" + id + ", name=" + name + ", image=" + image + ", price=" + price + ", quantity=" + quantity
				+ "]";
	}

}