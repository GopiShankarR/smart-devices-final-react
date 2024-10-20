package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@WebServlet(urlPatterns = "/signup", name = "SignupServlet")
public class SignupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
		response.addHeader("Access-Control-Allow-Headers", "Content-Type");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		String errorMessage = "";
		boolean isSaved = false;

		BufferedReader reader = request.getReader();
		JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
		String username = jsonObject.get("username").getAsString();
		String password = jsonObject.get("password").getAsString();
		String userType = jsonObject.get("userType").getAsString();

		System.out.println(username + " " + password + " " + userType);

		if (MySQLDataStoreUtilities.userExists(username, userType)) {
				errorMessage = "Username already exists as " + userType;
		} else {
				isSaved = MySQLDataStoreUtilities.createUser(username, password, userType);
		}

		JsonObject jsonResponse = new JsonObject();
		if (isSaved) {
				jsonResponse.addProperty("success", true);
				jsonResponse.addProperty("userType", userType);
				jsonResponse.addProperty("redirect", getRedirectPath(userType));
		} else {
				jsonResponse.addProperty("success", false);
				jsonResponse.addProperty("error", errorMessage.isEmpty() ? "Failed to save user" : errorMessage);
		}
		response.getWriter().write(jsonResponse.toString());
	}

	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
		response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
		response.addHeader("Access-Control-Max-Age", "3600");
		response.setStatus(HttpServletResponse.SC_OK);
	}

	private String getRedirectPath(String userType) {
		System.out.println("INSIDE SIGNUPSERVLET------->" + userType);
		switch (userType) {
			case "Customer":
				return "/home";
			case "Store Manager":
				return "/store-manager";
			case "Salesman":
				return "/salesman";
			default:
				return "/login";
		}
	}
}
