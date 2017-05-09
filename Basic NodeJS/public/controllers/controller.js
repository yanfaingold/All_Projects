
var myApp = angular.module('myApp', []);
myApp.controller('AppCtrl', ['$scope', '$http','$log', function($scope, $http, $log) {
    $log.info("Hello World from controller ");

    var refresh = function(){
	$http.get('/userlist').then(doneCallbacks, failCallbacks);
	}
	refresh();

	 function doneCallbacks(res) {
	  $log.info("Data received");
	  $scope.users = res.data;
	 }

	 function failCallbacks(err) {
	  $log.error(err.message);
	 }

	 $scope.addUser = function(){
	 	$log.log($scope.user);
	 	$http.post('/userlist',$scope.user).then(donePostCallbacks);
	 }
	 function donePostCallbacks(res) {
	  $log.log(res);
	  refresh();
	 }

	 function failPostCallbacks(err) {
	  $log.error(err.message);
	 }
}]);﻿