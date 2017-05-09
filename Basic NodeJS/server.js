var express= require('express');
var app= express();
var mongojs = require('mongojs');
var db = mongojs('users',['users']);
var bodyParser = require('body-parser');


app.use(express.static(__dirname + "/public"));

jsonParser = bodyParser.json();

app.get('/userlist',function (req,res){
	console.log("I recieved a GET request");

    //get
    db.users.find(function(err,docs){
        console.log(docs);
        res.json(docs);
    });

});

app.post('/userlist',jsonParser,function(req,res){
    console.log(req.body);
    db.users.insert(req.body, function(err,doc){
        res.json(doc);
    })
});

app.listen(80);
console.log("Server running on port 80");