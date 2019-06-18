package com.blockchain.komodoapi.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

@Controller
@Scope("session")
public class KomodoController {

	@CrossOrigin(origins="*")
	@ResponseBody
	@RequestMapping(value = "/firecommand", method = RequestMethod.POST, produces = "application/json")
	public String checkAddress( @RequestParam("id") String id, @RequestParam("tx") String tx,  @RequestParam("username") String username,  
			@RequestParam("password") String password,  @RequestParam("ip") String ip,
			@RequestParam("port") String port, @RequestParam(value="params", required=false) String params) {

		StringBuilder output = new StringBuilder("");
		try {
			URL url = new URL("http://"+ip+":"+port+"/");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			String encoding = Base64.getEncoder().encodeToString((username+":"+password).getBytes());
			conn.setRequestProperty("Authorization", "Basic "+encoding);

			StringBuilder input = new StringBuilder("");
			input.append("{\"jsonrpc\": \"1.0\", \"id\":\""+id+"\", \"method\": \""+tx+"\", \"params\": [");
			if(!StringUtils.isEmpty(params)) {
				input.append(params);
			}
			input.append("] }");

			OutputStream os = conn.getOutputStream();
			os.write(input.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return "Failed : Please enter proper parameters";
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));


			String temp;
			System.out.println("Output from Server .... \n");
			while ((temp = br.readLine()) != null) {
				output.append(temp);
				System.out.println(temp);
			}

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
		return output.toString();
	}
}
