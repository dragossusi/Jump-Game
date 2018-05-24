var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var players = [];
//var nume;
//var skin;
server.listen(8080, function(){
	console.log("Server is now running...");
});
io.on('connection', function(socket){
	console.log("Player Connected!");
	socket.emit('socketID', { id: socket.id });
    socket.on('setName', function(data) {
        for(var i = 0; i < players.length; ++i) {
            if(players[i].id == socket.id) {
                players[i].nume = data.nume;
                players[i].skin = data.skin;
            }
        }
        console.log(data.nume+" "+data.skin);
        socket.broadcast.emit('newPlayer', { id: socket.id , name: data.nume, skin: data.skin});
    });
    socket.on('playerMoved', function(data) {
        data.id = socket.id;
        for(var i = 0; i < players.length; ++i) {
            if(players[i].id == data.id) {
                players[i].x = data.x;
                players[i].y = data.y;
            }
        }
        socket.broadcast.emit('playerMoved', data);
    });
	socket.emit('getPlayers', players);
	socket.on('disconnect', function(){
		console.log("Player Disconnected");
		for(var i = 0; i < players.length; i++){
			if(players[i].id == socket.id){
				players.splice(i, 1);
			}
        }
		socket.broadcast.emit('playerDisconnected', { id: socket.id });
	});
    players.push(new player(socket.id, nume, 0, 0));
});
function player(id, nume, x, y){
 	this.id = id;
 	this.nume = nume;
 	this.skin = skin;
 	this.x = x;
 	this.y = y;
 }