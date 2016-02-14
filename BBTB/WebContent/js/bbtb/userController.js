define(['angular', 'bbtb/login'], function(angular, login) {
  var app = angular.module('user', []);

  app.service('authenticatedUser', function() {
	  var authenticatedUser = null;
	  
	  var handlers = [];
	  
	  var service = this;
	  
	  service.getAuthenticatedUser = function() {
		  return authenticatedUser; 
	  };
	  
	  service.setAuthenticatedUser = function(user) {
		  authenticatedUser = user;
		  if(user !== null) {
			  for(var i = 0; i < handlers.length; ++i) {
				  if(typeof handlers[i] !== 'undefined') {
					  handlers[i](authenticatedUser);
				  }
			  }
		  }
	  };
	  
	  service.registerHandler = function(handler) {
		  handlers.push(handler);
	  };
  });
  
  app.controller('UserController', ['$http', '$scope', 'authenticatedUser', function($http, $scope, authenticatedUser) {
	 var thiz = this;
	 
	 this.googleAuthOK = false;
	 
	 thiz.googleEmail = null;
	 
	 this.displayName = null;
	 
	 this.isAuthenticated = false;
	 
	 this.triedAuthentication = false;
	 
	 this.user = null;

	 this.authStatusMessage = "Authenticating...";
	 
	 this.scope = $scope;
	 
	 this.loadCurrentBBTBUser = function() {
		 // try login based on http session		 
		 $http.get('bbtb/api/users/@me').
		 	success(function(data, status, headers, config) {
		 		thiz.user = data;
		 		console.log("get user", data);
		 		if(thiz.user && thiz.user != '') {
		 			thiz.triedAuthentication = true;
		 			thiz.isAuthenticated = true;
		 			authenticatedUser.setAuthenticatedUser(data);
		 		}
		    }).
		    error(function(data, status, headers, config) {
		    	thiz.user = null;
	 			thiz.triedAuthentication = true;
		    	thiz.isAuthenticated = false;
		    	authenticatedUser.setAuthenticatedUser(null);
		    	// log error
		    	console.log("can't access user data. status = " + status);
		    	thiz.authStatusMessage += " no active BBTB session. Trying Google login...";
		 		login.appStart(thiz.callbackAfterGoogleLoginAttempt);
		    });
	 };
	 
	 this.callbackAfterGoogleLoginAttempt = function(googleUser) {
		 // called by login.js after login to Google has been requested
		 if (googleUser.El) {
		      googleUser.getGrantedScopes();
			  console.log('auth-response: ' + JSON.stringify(googleUser.getAuthResponse(), undefined, 2));
			  
			  var id_token = googleUser.getAuthResponse().id_token;
			  if(id_token) {
				  thiz.googleAuthOK = true;
				  thiz.googleEmail = googleUser.getBasicProfile().getEmail();
				  thiz.authStatusMessage += "Google login granted.";
				  thiz.scope.$apply(); // update angularJS view
				  var xhr = new XMLHttpRequest();
				  xhr.open('POST', 'http://localhost:8080/bbtb/auth/tokensignin');
				  xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
				  xhr.onload = function() {
					  if(xhr.status === 200) {
						  // i.e. BBTB GoogleTokenVerifyer confirms Google login
						  // yet, no user data from BBTB
						  thiz.triedAuthentication = true;
						  thiz.isAuthenticated = true;
						  console.log('BBTB confirms Google login: ' + xhr.responseText);

						  // try to get user data from BBTB
						  $http.get('bbtb/api/users/@me').
						  	success(function(data, status, headers, config) {
						 		thiz.user = data;
						 		console.log("Google login granted, BBTB user retrieved", data);
						 		thiz.authStatusMessage += " BBTB user retrieved!";
						 		if(thiz.user) {
						 			thiz.triedAuthentication = true;
						 			thiz.isAuthenticated = true;
						 			authenticatedUser.setAuthenticatedUser(thiz.user);
						 			thiz.scope.$apply(); // update angularJS view
						 		} 
						    }).
						    error(function(data, status, headers, config) {
						    	thiz.user = null;
						    	thiz.triedAuthentication = true;
					 			thiz.isAuthenticated = false;
					 			authenticatedUser.setAuthenticatedUser(null);
					 			thiz.scope.$apply(); // update angularJS view
						    	// log error
						    	thiz.authStatusMessage += " Unexpected error.";
						    	console.log("Got an 200 but still no user?. status = " + status);
						    });
					  } else {
						  thiz.triedAuthentication = true;
						  thiz.isAuthenticated = false;
						  authenticatedUser.setAuthenticatedUser(null);
						  thiz.authStatusMessage += " But no BBTB account linked.";
						  console.log("Google log granted but no BBTB account linked. status = " + status);
						  // window.location.replace("createUser.html");
					  }
				  };
				  xhr.send(id_token);
			  }

		  } else {
			  thiz.authStatusMessage += " No grant from Google.";
			  console.log('no google user logged in');
		  }		 
	 };
	 
	 this.createAccount = function(email, displayName, recaptchaResponseToken) {
		console.log("createAccount");
		
		var newAccount = {
				'email' : email,
				'displayName' : displayName
		};
		
		$http.post('/bbtb/api/users?recaptchaResponseToken=' + recaptchaResponseToken, newAccount).
		 success(function(data, status, headers, config) {
			  console.log('post user success. location=', headers('Location'));
		  }).
		  error(function(data, status, headers, config) {
			  console.log('error posting user. reponse: ' + data);
		  });	
	 };
	 
	 this.logout = function() {
		console.log("logout");
		$http.logout('/bbtb/logout').
		 success(function(data, status, headers, config) {
			  console.log('post user logout. location=', headers('Location'));
		  }).
		  error(function(data, status, headers, config) {
			  console.log('error logout user. reponse: ' + data);
		  });
	 };

	 this.setDisplayName = function(newValue) {
		 this.displayName = newValue;
	 };
	 
	 this.getDisplayName = function() {
		if(thiz.user && thiz.user.displayName) {
			return thiz.user.displayName;
		} else {
			return "undefined";
		}
	 };
	 this.loadCurrentBBTBUser();
	 
  }]);
});