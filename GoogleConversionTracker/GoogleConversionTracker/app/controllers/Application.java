package controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.GoogleCampaign;

import org.apache.commons.io.FileUtils;

import play.Logger;
import play.Play;
import play.mvc.Controller;

import common.Constants;
import common.MarketoUtility;

public class Application extends Controller {

	public static void index(String url) {

		if (url == null) {
			render();
		}  else  {
			GoogleCampaign gc = new GoogleCampaign();
			gc.munchkinId = url;
			gc.campaignURL = null;
			showConversionFiles(Constants.CAMPAIGN_GOOG, gc);
		}
	}

	public static void showConversionFiles(int campaignGoog, GoogleCampaign gc) {
		String urlBase = Play.configuration.getProperty("mkto.serviceUrl");
		String dirBase = Play.configuration.getProperty("mkto.googBaseDir");
		String dirName = dirBase + gc.munchkinId;
		List<String> allConversionFiles = new ArrayList<String>();
		File dirFile = new File(dirName);
		File[] listOfFiles = dirFile.listFiles();
		if (listOfFiles != null) {
			for (File f : listOfFiles) {
				String fqFileName = urlBase + "/public/google/"
						+ gc.munchkinId + "/" + f.getName();
				Logger.debug("File name is : %s", fqFileName);
				allConversionFiles.add(fqFileName);
			}
		}
		render(allConversionFiles);
	}

	public static void addGCLID(String munchkinId, String leadId,
			String action, String gclid, String convName, String convValue,
			String convTime) {
		// always write to the latest.csv file in the folder
		String urlBase = Play.configuration.getProperty("mkto.googBaseDir");
		String dirName = urlBase + munchkinId;
		File dirFile = new File(dirName);
		try {
			Logger.debug("Trying to create directory : %s", dirName);
			FileUtils.forceMkdir(dirFile);
			String fileName = dirName + "/latest.csv";
			Logger.debug("Checking if file : %s exists", fileName);
			File latestFile = new File(fileName);
			if (!latestFile.exists()) {
				Logger.debug("About to create file : %s ", fileName);
				createGoogleConversionFile(latestFile);
			}
			String payload = "\n" + action + "," + gclid + "," + convName + ","
					+ convValue + "," + convTime;
			Logger.debug("Writing %s to file : %s ", payload, fileName);
			appendToFile(fileName, payload);
		} catch (IOException e) {
			Logger.fatal("Unable to create directory/write to file : %s",
					e.getMessage());
			e.printStackTrace();
		}
	}

	private static void appendToFile(String fileName, String payload)
			throws IOException {
		FileWriter fileWriter = new FileWriter(fileName, true);
		BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
		bufferWriter.write(payload);
		bufferWriter.close();
	}

	private static void createGoogleConversionFile(File file)
			throws IOException {
		String googFileHeader = Play.configuration
				.getProperty("mkto.googFileHeader");
		Logger.debug("Writing %s to file : %s ", googFileHeader, file.getPath());
		FileUtils.writeStringToFile(file, googFileHeader);
	}
}