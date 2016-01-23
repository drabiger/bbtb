define([ 'angular' ], function(angular) {
	// create module 'userCreation'
	var app = angular.module('userCreation', [ 'user' ]);

	app.controller('UserCreateFormController', [
			'$http',
			'$scope',
			function($http, $scope) {
				var thiz = this;

				this.displayName;

				this.recaptchaVerified = false;

				this.displayNameMinLength = 5;

				this.createAccount = function(user) {
					console.log("create Account clicked");

					// trust angularjs's input validation
					if (angular.element("#displayName").hasClass("ng-valid")) {
						var result = user.createAccount(user.googleEmail,
								angular.element("#displayName").val(),
								grecaptcha.getResponse());
					}
				};

				this.creationDisabled = function() {
					if (!$('#displayName').hasClass('ng-valid')
							|| !grecaptcha.getResponse()
							|| grecaptcha.getResponse().length === 0)
						return true;
					else
						return false;
				};
				
				$scope.setRecaptchaVerification = function() {
					thiz.recaptchaVerified = true;
					$scope.$apply();
	            };
	            
			} ]);
});

function captchaCallback() {
	$("#recaptcha-state").attr("recaptcha-success", "true");
	var el = document.querySelector('.g-recaptcha'), $el = angular.element(el), $scope = $el
			.scope();

	$scope && $scope.setRecaptchaVerification();
};
