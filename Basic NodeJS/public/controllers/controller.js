
var myApp = angular.module('myApp', []);
myApp.controller('AppCtrl', ['$scope', '$http', function($scope, $http) {
    console.log("Hello World from controller");

    var refresh = function(){
	$http.get('/userlist').then(doneCallbacks, failCallbacks);
	}
	refresh();

	 function doneCallbacks(res) {
	  console.log("Data received");
	  $scope.users = res.data;
	 }

	 function failCallbacks(err) {
	  console.log(err.message);
	 }

	 $scope.addUser = function(){
	 	console.log($scope.user);
	 	$http.post('/userlist',$scope.user).then(donePostCallbacks);
	 }
	 function donePostCallbacks(res) {
	  console.log(res);
	  refresh();
	 }

	 function failPostCallbacks(err) {
	  console.log(err.message);
	 }
}]);﻿