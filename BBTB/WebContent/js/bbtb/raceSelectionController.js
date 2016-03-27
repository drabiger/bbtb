define([ 'angular' ], function(angular) {
	var app = angular.module('raceSelection', [ 'ui.bootstrap' ]);

	var controller = function($http, $scope, $uibModalInstance, currentSelection) {
		$scope.items = [];

		$scope.selectedItem = undefined;

		$scope.setting = false;
		
		$scope.preSelection = currentSelection;

		$http.get('bbtb/api/races/all').success(
				function(data, status, headers, config) {
					$scope.items = data;
					console.log("get races", data);
				}).error(function(data, status, headers, config) {
			// log error
			console.error("error");
		});

		$scope.getItems = function() {
			return $scope.items;
		};

		$scope.getSelectedItem = function() {
			return $scope.selectedItem;
		};

		$scope.setSelectedItem = function(item) {
			$scope.selectedItem = item;
			$scope.ok();
		};

		$scope.ok = function() {
			$uibModalInstance.close($scope.selectedItem);
		};

		$scope.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	};

	app.controller('raceSelectionController', [ '$http', '$scope', 
			'$uibModalInstance', 'currentSelection', controller ]);
});
