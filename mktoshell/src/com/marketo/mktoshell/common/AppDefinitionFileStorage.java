package com.marketo.mktoshell.common;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.marketo.mktoshell.content.Content;

public class AppDefinitionFileStorage extends
		AsyncTask<Integer, Integer, AppDefinition> {

	private Context mCtx;

	public AppDefinitionFileStorage(Context ctx) {
		mCtx = ctx;
	}

	@Override
	protected AppDefinition doInBackground(Integer... callback) {
		Integer type = callback[0];
		switch (type) {
		case Constants.ACTION_TYPE_STORE:
			String rep = Content.getJson();
			storeInFile(rep);
		case Constants.ACTION_TYPE_READ:
			String storedRep = readFromFile();
			Gson gson = new GsonBuilder().create();
			Content.instantiate(storedRep);
			AppDefinition appDef = gson
					.fromJson(storedRep, AppDefinition.class);
			return appDef;
		}
		return null;
	}

	private String readFromFile() {
		FileInputStream inputStream;
		String val = "";
		try {
			inputStream = mCtx.openFileInput(Constants.APP_DEF_FILE);
			int content;
			while ((content = inputStream.read()) != -1) {
				val += (char) content;
			}
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return val;
	}

	private void storeInFile(String rep) {
		FileOutputStream outputStream;

		try {
			outputStream = mCtx.openFileOutput(Constants.APP_DEF_FILE,
					Context.MODE_PRIVATE);
			outputStream.write(rep.getBytes());
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
