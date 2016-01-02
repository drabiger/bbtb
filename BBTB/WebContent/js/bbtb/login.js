define(['gapi'], function(googleGapi) {
	
	var thiz = this;
	var auth2; // The Sign-In object.
	var currentValidatedUser = null; // The current user.
	var callbackAfterGoogleLoginAttempt = null;

	/**
	 * Calls startAuth after Sign in V2 finishes setting up.
	 */
	this.appStart = function(callback) {
		this.callbackAfterGoogleLoginAttempt = callback;
		gapi.load('auth2', initSigninV2);
	};

	/**
	 * Initializes Signin v2 and sets up listeners.
	 */
	var initSigninV2 = function() {
	  auth2 = gapi.auth2.init({
	      client_id: '290685487945-bs2eir89lom13ffq89850jdth5ir2niv.apps.googleusercontent.com',
	      scope: 'profile'
	  });
	
	  // Listen for sign-in state changes.
	  auth2.isSignedIn.listen(signinChanged);
	
	  // Listen for changes to current user.
	  auth2.currentUser.listen(userChanged);
	
	  // Sign in the user if they are currently signed in.
	  if (auth2.isSignedIn.get() == true) {
	    auth2.signIn();
	  } else {
		  var element = document.getElementById('customBtn');
		  auth2.attachClickHandler(element, {});
	  }
	
	  // Start with the current live values.
	  refreshValues();
	};
	
	/**
	 * Listener method for sign-out live value.
	 *
	 * @param {boolean} val the updated signed out state.
	 */
	var signinChanged = function (val) {
	  console.log('Signin state changed to ', val);
	};
	
	/**
	 * Listener method for when the user changes.
	 *
	 * @param {GoogleUser} user the updated user.
	 */
	var userChanged = function (googleUser) {
	  console.log('User changed to: ', googleUser);
	  if(googleUser !== currentValidatedUser) {
		  thiz.callbackAfterGoogleLoginAttempt(googleUser);
	  }
	};
	
	/**
	 * Retrieves the current user and signed in states from the GoogleAuth
	 * object.
	 */
	var refreshValues = function() {
	  if (auth2){
	    console.log('Refreshing auth values...');
	    var googleUser = auth2.currentUser.get();
	    thiz.callbackAfterGoogleLoginAttempt(googleUser);
	  };
	};
	
	return this;

});