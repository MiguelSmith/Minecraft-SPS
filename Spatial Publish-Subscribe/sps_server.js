/*
    server for a spatial publish/subscribe system
*/
/*
    functions:
    on()                    handles client connection
    subscribe()             subscribe to an area
    unsubscribe()           unsubscribe from an area
    publish()               send a message to subscribers whose AoI intersects the clients
    move()                  change the coordinates and AoI of the client
    ID()                    return the client id to the client on connnection
*/

var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var VAST = require('./types');

var connectionID = -1;
var channelSubID =
{
    "status":-1,
    "login":-1,
    "ingame":-1
};

var subType =
{
    "UNIQUE":0,
    "UPDATE":1,
    "DUPLICATE":2
}

var differenceType =
{
    "INSIDE":0,
    "LIMBO":1,
    "OUTSIDE":2
}

// is there a lobby server
var lobby = false;

//buffer queue for packets
var packets = [];
var packStrings = [];

// map of subscribers for a channel
// key -> channel
// value -> {
//            key -> subID
//            value - > subPack
//          }
var subscriberList = {};

// map of base subscriptions
// key -> username
// value -> subID
var usernames = {};

// map of base server subscriptions
// key -> username
// value -> subID
var usernamesServer = {};

//temporary socket -> ID map
var socketToConnectionIDMap = {};
var connectionInfoList = {};

//serve html file
app.get('/', function(req, res)
{
    res.sendFile(__dirname + '/index.html');
});



/*

//SPS server must handle incoming connections and assign an ID.
SPS server must handle disconnections and removeID from subscribers
SPS server must keep track of channels and users subscribed to channels
SPS server must allow any connection to publish a message to a specific channel
SPS server must allow any connection to spatially publish a message to a specific channel
 */


//handle client connnection
io.on('connection', function(socket) {
    connectionID++;
    console.log("Someone connected. Sending connectionID <" + connectionID + ">");
    socket.emit("ID", connectionID)

    // if lobby server does not exist, emit "type" to determine lobby server availablity
    // callback happens in the "type" event listener
    socket.emit("type", lobby);

    socketToConnectionIDMap[socket.id] = connectionID;
    //console.log(socketToConnectionIDMap);
    connectionInfoList[connectionID] =
        {
            id: connectionID,                                   // id
            socket: socket,                                     // socket
            x: 0,                                               // x coordinate of client
            y: 0,                                               // y coordinate of client
            AoIRadius: 100                                       // radius of AoI (currently make it large enough that they will always send to each other)
        };

    // handle type callback
    // this is followed on the server proxy side by a subscription to channel "lobby"
    socket.on("type", function(type) {
        lobby = type == "server" ? true : false;
        console.log("Lobby: " + lobby);
    });

    //handle a disconnect
    socket.on('disconnect', function(id)
    {
        var id = socketToConnectionIDMap[socket.id];
        delete connectionInfoList[id];
        console.log("Connection <"+id+"> has disconnected");

        return false;
    });

    // publish message to subscribers
    socket.on('publish', function(connectionID, player, x, y, radius, payload, channel, packetName)
    {
        //console.log("Attempting to send packet " + packetName + " from "+connectionID+" to channel " + channel + " for player " + player);

        _publish(socket, connectionID, player, x, y, radius, payload, channel, packetName);

        return true;;
    });

    socket.on('subscribe', function(channel, name, x, y, AoI) {
        _subscribe(socket,channel,name,x,y,AoI);

        return true;
    });

    socket.on("unsubscribe", function (channel, player, x, y, AoI) {
        // TODO: unsubscribe from channel and remove subIDs from channel list
        var connectionID = socketToConnectionIDMap[socket.id];
        console.log("Received unsubscribe request from " + connectionID + " for channel " + channel);

        var connection = connectionInfoList[connectionID];

        // check to see whether it is an unsubscribe from a channel or from an area/point
        if ((x ==undefined) && (y == undefined)) {
            x = connection.x;
            y = connection.y
        }

        //  check seperately for AoI because it could be a change in subscription for their current location
        if (AoI == undefined) {
            AoI = connection.AoIRadius;
        }

        for (var subID in subscriberList[channel]) {
            var sub = subscriberList[channel][subID];

            if ((sub.evaluate(x,y,AoI) == subType.DUPLICATE) && (player == sub.name)) {
                console.log("Unsubscribe subID " + subID + " from channel " + channel);
                delete subscriberList[channel][subID];
            }
        }

        return true;
    });

    //move
    socket.on('move', function(connectionID, name, x, y, radius, payload, channel, packetName)
    {
        //console.log("Moving client " + name + " for connectionID " + connectionID + " to <" + x + "," + y + "> on channel " + channel + " on packet " + packetName);
        var connection = connectionInfoList[connectionID];

        if (usernames.hasOwnProperty(name) && usernamesServer.hasOwnProperty(name)) {
            var sub = subscriberList["ingame"][usernames[name]];
            var subServer = subscriberList["ingame"][usernamesServer[name]];

            console.log("Updating sub from <" + sub.x + "," + sub.y + "> to <" + x + "," + y + ">")
            sub.x = x;
            subServer.x = x;
            sub.y = y;
            subServer.y = y;
            sub.AoI = radius;
            subServer.AoI = radius;
            subscriberList["ingame"][usernames[name]] = sub;
            subscriberList["ingame"][usernamesServer[name]] = subServer;


            //console.log(subscriberList);

            // take this out so that the server can move as well without resending the packet
            //_publish(socket, connectionID, name, x, y, radius, payload, channel, packetName);
        } else {
            console.log("Base subscription does not exist. Create one.")
            //_subscribe(socket, channel, name, x, y, radius);
        }

        return false;
    });

    return false;
});

var _contains = function (sub, pubX, pubY, pubAoI) {
    var dx = sub.x - pubX;
    var dy = sub.y - pubY;
    var dist = Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2));

    var difference = sub.AoI - dist;
    //console.log("pubAoI: " + pubAoI + " sub.AoI: " + sub.AoI + " Distance: " + dist + " Difference: " + difference);

    if (difference >= 0) {
        return true;
    }
    return false;
}

var _subscribe = function (socket, channel, name, x, y, AoI) {
    var connectionID = socketToConnectionIDMap[socket.id];
    console.log("Received subscription request from " + connectionID + " for channel " + channel + " for " + name);

    var connection = connectionInfoList[connectionID];

    // TODO: create duplicate, update and new packet checker

    // create subID for a channel if it doesn't exist
    if (!channelSubID.hasOwnProperty(channel)) {
        //console.log("Creating entry for channel " + channel);
        channelSubID[channel] = -1;
    }
    channelSubID[channel]++;
    var subID = channelSubID[channel];

    if (channel == "ingame" &&
        !usernames.hasOwnProperty(name) &&
        connectionID == 1) {
        usernames[name] = subID;
    }

    if (channel == "ingame" &&
        !usernamesServer.hasOwnProperty(name) &&
        connectionID == 0) {
        usernamesServer[name] = subID;
    }

    // check whether it is a point or area publication
    // NOTE: since this is a rough implementation, this crude method is used since x will always
    // be undefined when it is a publication that is not spatially important
    if (x == undefined) {
        //console.log("Subscribing to <" + connection.x + "," + connection.y + "> with an AoI of " + connection.AoIRadius + ". SubID: "+subID);
        var pack = new VAST.sub(connectionID, subID, connection.x, connection.y, connection.AoIRadius, channel, name);
    } else {
        //console.log("Subscribing to <" + x + "," + y + "> with an AoI of " + AoI + ". SubID: "+subID)
        var pack = new VAST.sub(connectionID, subID, x, y, AoI, channel, name);
    }

/*
    if (connectionID == 0) {
        console.log("Changing AoI to 10000");
        pack.AoI = 10000;
    }
*/

    // if this is the first subscription to the channel, create key
    if (!subscriberList.hasOwnProperty(channel)) {
        subscriberList[channel] = {};
    }

    subscriberList[channel][subID] = pack;
    console.log(subscriberList);
}

var _publish = function(socket, connectionID, player, x, y, radius, payload, channel, packetName) {
    //console.log("Attempting to send packet " + packetName + " from " + connectionID + " to channel " + channel + " for player " + player + " at <" + x + "," + y + "> for radius " + radius);

    if (!subscriberList.hasOwnProperty(channel)) {
        console.log("Trying to publish to a channel that does not exist: " + channel);
        return false;
    }

    for (var keys in subscriberList[channel]) {
        var sub = subscriberList[channel][keys];

        //console.log("Key: " + keys)
        //console.log(sub);

        if (sub.connectionID == connectionID)
            continue;

        if (_contains(sub, x, y, radius)) {
            // if the lobby channel is getting the message, make sure to use the given username.
            // else use the subscription username
            // only necessary for aggregation of packets so we won't implement it here.
            // player = channel == "lobby" ? player : sub.name;
            //console.log("Publishing to " + sub.subID);

            //console.log("Confirming sending packet " + packetName + " from "+connectionID+" to channel " + channel + " for player " + player + " at <" + x + "," + y + "> for radius " + radius);

            socket.broadcast.to(connectionInfoList[sub.connectionID].socket.id).emit('publication', connectionID, player, x, y, radius, payload, channel, 0);
        }
    }
}

//set the server to listening
http.listen(3000, function()
{
  console.log(http.address());
});
