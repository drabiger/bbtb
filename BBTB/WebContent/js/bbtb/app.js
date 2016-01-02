(function() {
  var app = angular.module('bbtb', []);

  app.controller('RaceController', ['$http', '$scope', function($http, $scope) {
	 var thiz = this;
	 this.races = [];
	 $http.get('bbtb/api/races/all').
	 	success(function(data, status, headers, config) {
	 		thiz.races = data;
	 		console.log("get races", data);
	    }).
	    error(function(data, status, headers, config) {
	    	// log error
	    	console.error("error");
	    });
	 	 
	 this.addRow = function() {
		 for (var i = 0; i < thiz.races.length; ++i) {
			 // only allow one dirty race; force post to server before adding
				// new row
			 if (thiz.races[i].client_dirty) {
				 return;
			 }
		 }
		 var newObject = {name : "", client_dirty : true};
		 thiz.races.push(newObject);
	 };
	 
	 this.save = function() {
		 console.groupCollapsed('call: save');
		 for (var i = 0; i < thiz.races.length; ++i) {
			 console.log('step: %s', thiz.races[i].name);
			 if (thiz.races[i].client_dirty) {
				 var dirtyRace = thiz.races[i];
				 delete dirtyRace.client_dirty;
				 
				 console.log("dirtyRace.uuid = ", dirtyRace.uuid);
				 console.log("dirtyRace[uuid] = ", dirtyRace["uuid"]);
				 
				 if(dirtyRace.uuid) {
					 $http.put('/bbtb/api/races/' + dirtyRace.uuid, dirtyRace).
						 success(function(data, status, headers, config) {
							  console.log('success. location=', headers('Location'));
							  dirtyRace.Location = headers('Location');
						    // this callback will be called asynchronously
						    // when the response is available
						  }).
						  error(function(data, status, headers, config) {
						    // called asynchronously if an error occurs
						    // or server returns response with an error status.
							  console.log('error posting');
						  });
				 } else {
					 $http.post('/bbtb/api/races/', dirtyRace).
						  success(function(data, status, headers, config) {
							  console.log('success. location=', headers('Location'));
							  dirtyRace.Location = headers('Location');
						    // this callback will be called asynchronously
						    // when the response is available
						  }).
						  error(function(data, status, headers, config) {
						    // called asynchronously if an error occurs
						    // or server returns response with an error status.
							  console.log('error posting');
						  });
				 }
			 }			
		 }
		 console.groupEnd();
	 };
	 
	 this.deleteRace = function(raceUuid) {
		 console.log("deleteRace " + raceUuid);
		 for (var i = 0; i < thiz.races.length; ++i) {
			 if(thiz.races[i].uuid == raceUuid) {
				 var raceToDelete = thiz.races[i];
				 var idxToRemoveFromArray = i;
				 console.log("found race with uuid " + raceToDelete.uuid);
				 $http.delete('/bbtb/api/races/' + raceToDelete.uuid).
				 	success(function(data, status, headers, config) {
				 		console.log('successfully deleted.');
				 		thiz.races.splice(idxToRemoveFromArray, 1);
					}).
					error(function(data, status, headers, config) {
						console.log('error deleting');
					});
			 };
		 };
	 };
	 
  }]);
  
  app.directive('showFocus', function($timeout) {
	  return function(scope, element, attrs) {
	    scope.$watch(attrs.showFocus, 
	      function (newValue) { 
	        $timeout(function() {
	            newValue && element[0].focus();
	        });
	      },true);
	  };    
	});
  
})();
