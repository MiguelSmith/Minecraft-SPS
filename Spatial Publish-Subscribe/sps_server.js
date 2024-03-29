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
    socketToConnectionIDMap[socket] = connectionID;
    connectionInfoList[connectionID] =
        {
            id: connectionID,                             // id
            socket: socket,                               // socket
            x: Math.floor((Math.random() * 300) - 150),   // x coordinate of client
            y: Math.floor((Math.random() * 300) - 150),   // y coordinate of client
            AoIRadius: 0,                                 // radius of AoI
            subscriptions: {}                             // channels to which connection is subscribed
        };

    //handle a disconnect
    socket.on('disconnect', function(id)
    {
        var id = socketToConnectionIDMap[socket];
        delete connectionInfoList[id];
        console.log("Connection <"+id+"> has disconnected");

        return false;
    });

    // publish message to subscribers
    socket.on('publish', function(connectionID, player, x, y, radius, payload, channel)
    {
        for (var keys in subscriberList[channel]) {
            var sub = subscriberList[channel][keys];
            if (_contains(sub, x, y, radius)) {
                socket.broadcast.emit('publication', connectionID, player, x, y, radius, payload, channel);
            }
        }

        return false;
    });

    socket.on('subscribe', function(channel, x, y, AoI) {
        var connectionID = socketToConnectionIDMap[socket];
        console.log("Received subscription request from " + connectionID + " for <" + x + "," + y + "> for an AoI of " + AoI);

        var connection = connectionInfoList[connectionID];

        // TODO: create duplicate, update and new packet checker

        // create subID for a channel if it doesn't exist
        if (!channelSubID.hasOwnProperty(channel)) {
            console.log("Creating entry for channel " + channel);
            channelSubID[channel] = -1;
        }
        var subID = channelSubID[channel]++;

        // check whether it is a point or area publication
        // NOTE: since this is a rough implementation, this crude method is used since x will always
        // be undefined when it is a publication that is not spatially important
        if (x == undefined) {
            var pack = new VAST.sub(connectionID, subID, connection.x, connection.y, connection.AoIRadius, channel);
        } else {
            var pack = new VAST.sub(connectionID, subID, x, y, AoI, channel);
        }

        // create subscription channel if it doesn't exist
        if (!connection.subscriptions.hasOwnProperty(channel)) {
            connection.subscriptions[channel] = {};
        }
        connection.subscriptions[channel][subID] = pack;

        connectionInfoList[connectionID] = connection;

        // if this is the first subscription to the channel, create key
        if (!subscriberList.hasOwnProperty(channel)) {
            subscriberList[channel] = {};
        }

        subscriberList[channel][subID] = pack;
    });

    /*
    //handle a subscribe or unsubscribe function
    socket.on('function', function(msg)
    {
        console.log(msg);
        var temp = msg.split(';');
        var query = temp[1];
        var id = temp[0];
        var newSubscription = temp[2];
        var AoIRadius = temp[3];
        var x = temp[4];
        var y = temp[5]
        var channel = temp[6];


        var refId = socketToConnectionIDMap[socket];
        var connectionInfo = connectionInfoList[refId];

        switch (query)
        {
            //subscribe
            case 'sub':
            {

                if (newSubscription == "true") {
                    subID++;
                    var pack =  new VAST.sub(refId, subID, connectionInfo.x, connectionInfo.y, AoIRadius, channel);
                    //var pack = new VAST.sub(ref,0,0,temp[3],temp[6]);
                    connectionInfoList[refId].AoIRadius = AoIRadius;
                    console.log('Client '+refId+ ' subscribed to the area around it on channel '+channel);
                } else {
                    var pack =  new VAST.sub(id, x, y, AoIRadius, channel);
                    console.log('Client '+refId+ ' subscribed to the area '+AoIRadius+' units around <'+x+','+y+'> on channel '+channel);
                }
            }
            break;

            //unsubscribe
            case 'unsub':
            {
                if (newSubscription == "true")
                {
                    connectionInfoList[refId].AoIRadius = AoIRadius;
                    var hash = refId+'/'+connectionInfo.x+'/'+connectionInfo.y;
                    console.log('Client '+id+' unsubscribed from the area around it on channel '+channel);
                    var returnStr = ref+';'+hash+';'+connectionInfo.x+';'+connectionInfo.y+';'+channel;
                } else
                {
                    var hash = refId+'/'+x+'/'+y;
                    console.log('Client '+id+' unsubscribed from the area <'+x+','+y+'> on channel '+channel);
                    var returnStr = refId+';'+hash+';'+x+';'+y+';'+channel;
                }
            }
            break;

            //unknown command
            default:
            {
                console.log('Cannot understand function');
                socket.emit('unknown', 'Unsupported function');
            }
            break;
        }

        return false;
    });
    */
    //move
    socket.on('move', function(msg)
    {
        //handle input message
        var temp = msg.split(';');
        var id = temp[0];
        var x = temp[1];
        var y = temp[2];
        var AoIRadius = temp[3];
        var connectionInfo = connectionInfoList[id];
        //var ref = parseInt(temp[0]);
        console.log('Client '+id+ ' changed their (x,y) position to <'+x+','+y+'> with an AoI radius of '+AoIRadius);

        var pack = new VAST.sub(id, connectionInfo.x, connectionInfo.y, connectionInfo.AoIRadius);

        //insert x, y and Aoi into 2D array
        connectionInfo.x = x;
        connectionInfo.y = y;
        connectionInfo.AoIRadius = AoIRadius;

        return false;
    });

    return false;
});

var _contains = function (sub, pubX, pubYy, pubAoI) {
    var dx = sub.x - pubX;
    var dy = sub.y - pubY;
    var dist = Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2));

    if (dist <= (pubAoI + sub.AoI) {
        return true;
    }

    return false;
}

//set the server to listening
http.listen(3000, function()
{
  console.log(http.address());
});
