var App = angular.module('App', []);
App.controller('TodoCtrl', function($scope, $http) {
  $http.get('http://127.0.0.1:8000/api/questions/?format=json')
       .then(function(res){
          $scope.todos = res.data; 
				  
        });
});


function myFunction(question,id) {
//hide buttons
	var buttonsvar = document.getElementById("allbuttons");
while (buttonsvar.firstChild) {
    buttonsvar.removeChild(buttonsvar.firstChild);
} 
	document.getElementById("theid").innerHTML ="id: "+id; 
	document.getElementById("thequestion").innerHTML="Question "+question;
   
}