package jobs;

import java.io.File;
import java.util.Date;

import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.On;

//sec min hr dayofmonth month dayofweek
@On("0 0 0 ? 1-12 SUN")
public class RenameFiles extends Job {

	public void doJob() {
		Logger.info("Renaming google conversion files");
		String dirBase = Play.configuration.getProperty("mkto.googBaseDir");
		File dirBaseFile = new File(dirBase);
		if (dirBaseFile.exists()) {
			File[] dirs = dirBaseFile.listFiles();
			for (File dir : dirs) {
				File latestFile = new File(dir + "/latest.csv");
				Date dt = new Date();
				String newFileName = dt.toString().substring(0, 10) + ".csv";
				File newFile = new File(dir.getAbsolutePath() + "/"
						+ newFileName);
				if (latestFile.exists()) {
					Logger.debug("Renaming file %s to %s",
							latestFile.getAbsolutePath(), newFile);
					latestFile.renameTo(newFile);
				}
			}
		}
	}
}
