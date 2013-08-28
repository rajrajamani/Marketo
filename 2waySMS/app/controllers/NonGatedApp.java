package controllers;

import models.User;
import play.Logger;
import play.Play;
import play.libs.Crypto;
import play.mvc.Controller;

public class NonGatedApp extends Controller {

	public static void registerUser(String munchkinId, String pw1, String pw2) {
		String currUser = Security.connected();
		String placeholder = "";
		if (currUser == null || "".equals(currUser)) {
			placeholder = "Munchkin Id";
		} else {
			placeholder = currUser; 
		}
		if (munchkinId == null && pw1 == null && pw2 == null) {
			render(placeholder);
		} 
		
		Logger.debug("mId:%s; Pass:%s", munchkinId, pw1);
		User user = User.find("byMunchkinId", munchkinId).first();
		if (user != null) {
			Application.index(null);
		} else {
			String encPw = Crypto.passwordHash(pw1);

			User u1 = new User();
			u1.munchkinId = munchkinId.toLowerCase();
			u1.password = encPw;
			u1.save();
			Application.index(null);
		}

		//should never reach here
		render(placeholder);
	}

}
