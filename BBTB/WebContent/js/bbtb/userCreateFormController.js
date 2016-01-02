define(['angular'], function(angular) {
  // create module 'userCreation'
  var app = angular.module('userCreation', ['user']); 

  app.controller('UserCreateFormController', ['$http', '$scope', function($http, $scope) {
	 var thiz = this;
	 
	 this.displayName;
	 
	 this.displayNameMinLength = 5;
	 
	 this.createAccount = function(user) {
		console.log("create Account clicked");
		
		// trust angularjs's input validation
		if(angular.element("#displayName").hasClass("ng-valid")) {
			var result = user.createAccount(user.googleEmail, angular.element("#displayName").val(), grecaptcha.getResponse());
		}
	 };
	 
  }]);
});

function captchaCallback() {
	$("#submitButton").removeAttr("disabled");
};
